package com.asktech.pgateway.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.login.LoginResponseDto;
import com.asktech.pgateway.dto.login.LogoutRequestDto;
import com.asktech.pgateway.dto.merchant.MerchantUpdateReq;
import com.asktech.pgateway.dto.merchant.OTPConfirmation;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.OtpStatus;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.mail.MailIntegration;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.MobileOtp;
import com.asktech.pgateway.model.UserOTPDetails;
import com.asktech.pgateway.model.UserSession;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MobileOtpRepository;
import com.asktech.pgateway.repository.UserOTPDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.security.JwtGenerator;
import com.asktech.pgateway.util.SmsCallTemplate;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.Validator;

import jdk.jshell.spi.ExecutionControl.UserException;

@Service
public class UserLoginService implements CashFreeFields, ErrorValues {

	@Value("${apiCustomerExclude}")
	String apiCustomerExclude;
	@Autowired
	MerchantDetailsRepository repo;
	@Autowired
	private JwtGenerator jwtGenerator;
	@Autowired
	UserOTPDetailsRepository userOTPDetailsRepository;
	@Autowired
	MobileOtpRepository mobileOtpRepository;

	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;	
	@Value("${otpExpiryTime}")
	long otpExpiryTime;
	@Value("${sessionExpiryTime}")
	long sessionExpiryTime;
	@Value("${otpCount}")
	long otpCount;
	@Value("${failureLogin}")
	int failureLogin;
	@Value("${smsSenderId}")
	String smsSenderId;

	long EXPIRATION_TIME = 60 * 24;
	
	@Autowired
	MailIntegration sendMail;
	@Autowired
	SmsCallTemplate smsCallTemplate;

	static Logger logger = LoggerFactory.getLogger(UserLoginService.class);

	public Object getUserLogin(LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {
		logger.info("User Login");
		if (dto.getPassword().isEmpty()) {
			logger.info("User Password Blank");
			throw new ValidationExceptions(PASSWORD_CANT_BE_BLANK, FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
		}
		
		MerchantDetails user = repo.findByMerchantEMail(dto.getUserNameOrEmail());
		logger.info("User Details::"+Utility.convertDTO2JsonString(user));
		if (user == null) {
			logger.info("User Not found");
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		MerchantDetails mer = repo.getBlockedUserActive(user.getMerchantID());
		logger.info("Blocked User Details::"+Utility.convertDTO2JsonString(mer));
		if(mer!=null) {
			mer.setUserStatus("ACTIVE");
			repo.save(mer);
		}
		
		if(user.getUserStatus().equals(UserStatus.ACTIVE.toString()) && user.getFailCount()>failureLogin) {
			user.setFailCount(1);
			repo.save(user);
		}
		logger.info("User Not Failed");
		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			logger.info("User Blocked");
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}
		
		int remainAttempt = failureLogin - user.getFailCount();
		logger.info("User Attepts::"+Integer.toString(remainAttempt));
		if (user.getPassword() != null) {
			if (dto.getPassword() == null) {
				throw new ValidationExceptions(PASSWORD_CANT_BE_BLANK,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);

			} //else if (!user.getPassword().equals(Encryption.getEncryptedPassword(dto.getPassword()))) {
			else if (!dto.getPassword().equals(Encryption.getDecryptedPassword(user.getPassword()))) {
				logger.info("User Password Valid");
				if(user.getFailCount() >= failureLogin) {
					failureMerchantDetails(user, false);
					logger.info("User Password failureMerchantDetails USER_HAS_BEEN_BLOCKED");
					throw new ValidationExceptions(USER_HAS_BEEN_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);
				}else {
					logger.info("User Password failureMerchantDetails");
					failureMerchantDetails(user, true);
				}
				throw new ValidationExceptions(PASSWORD_MISMATCH+remainAttempt,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
			}
		} else {
			throw new ValidationExceptions(PASSWORD_MISMATCH+remainAttempt, FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
		}

		logger.info("User Password check complete");
		if(user.getInitialPwdChange()==null) {
			throw new ValidationExceptions(INITIAL_PASSWORD_CHANGE_REQUEST,
					FormValidationExceptionEnums.INITIAL_PASSWORD_CHANGE_REQUIRED);
		}

		mobileOtpRepository.truncateOtp();
		MobileOtp mobileOtp = null;
		SuccessResponseDto sdto = new SuccessResponseDto();
		
		if(user.getOtpStatus().equalsIgnoreCase(OtpStatus.DISABLE.toString())) {
			logger.info("OTP Disabled Login Success");
			LoginResponseDto logindata = loginWithOutOtp(dto);
			String jwt = (jwtGenerator.generate(dto));
			logindata.setJwtToken(jwt);
			sdto.getMsg().add("Merchant Logged in Successfully!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("LoginData", logindata);
		}
		else if(user.getOtpStatus().equalsIgnoreCase(OtpStatus.ENABLE.toString())) {
			logger.info("OTP Enable OTP Generated");
			 mobileOtp = sendMobileOtp(user);
				
				OTPConfirmation otpConfirmation = new OTPConfirmation();
				otpConfirmation.setUserId(dto.getUserNameOrEmail());
				otpConfirmation.setOtpSessionId(mobileOtp.getOtpSessionId());
				
			sdto.getMsg().add("Login OTP has been send to register Mobile Number and Email Id . Valid for 2 Mins");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("merchantSignUp", otpConfirmation);
		}

		return sdto;
		
//		  MobileOtp mobileOtp = null;
//		  mobileOtp = sendMobileOtp(user);
//				
//				
//				OTPConfirmation otpConfirmation = new OTPConfirmation();
//				otpConfirmation.setUserId(dto.getUserNameOrEmail());
//				otpConfirmation.setOtpSessionId(mobileOtp.getOtpSessionId());
//				
//				return otpConfirmation;
	}
	
	public void failureMerchantDetails(MerchantDetails merchantDetails, boolean status) {
		
		if(!status) {
			logger.info("failureMerchantDetails status::"+status);
			merchantDetails.setUserStatus(UserStatus.BLOCKED.toString());
			merchantDetails.setUpdateBy("SYSTEM_LOGIN_ATTEMPT");
			merchantDetails.setUpdateReason("Due to Multiple Failure login Attempt");
		}
		logger.info("failureMerchantDetails failcount::"+merchantDetails.getFailCount()+1);
		merchantDetails.setFailCount(merchantDetails.getFailCount()+1);
		repo.save(merchantDetails);
	}
	
	public OTPConfirmation userResenOtp(String emailorphone) throws UserException, ValidationExceptions {
		MerchantDetails user = repo.findByMerchantEMail(emailorphone);
		if (user == null) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);			
		}

		MobileOtp mobileOtp =  sendMobileOtp(user);
		
		OTPConfirmation otpConfirmation = new OTPConfirmation();
		otpConfirmation.setUserId(emailorphone);
		otpConfirmation.setOtpSessionId(mobileOtp.getOtpSessionId());
		
		return otpConfirmation;
	}
	public LoginResponseDto verifyOtp(int otp, LoginRequestDto dto , String sessionId) throws UserException, ValidationExceptions {
		
		long EXPIRATION_TIME = 60 * 24;
		if (otp>1000000) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}
		MobileOtp motp = mobileOtpRepository.findByOtpAndUserNameAndOtpSessionId(otp, dto.getUserNameOrEmail(), sessionId);
		if (motp == null ) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((motp.getExpDate()).before(dat)) {
			mobileOtpRepository.delete(motp);			
			throw new ValidationExceptions(OTP_EXPIRED, FormValidationExceptionEnums.OTP_EXPIRED);
		}
		
		MerchantDetails user = repo.findByMerchantEMail(dto.getUserNameOrEmail());
		if (user == null) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}
		
		if (user.getUserSession() != null) {
			user.getUserSession().setSessionStatus(0);
		}

		UserSession session = new UserSession();
		if (user.getUserSession() != null) {
			session = user.getUserSession();
		}
		session.setSessionStatus(1);
		session.setUserAgent(dto.getUserAgent());
		session.setIpAddress(dto.getIpAddress());
		session.setUser(user);
		String hash = Encryption.getSHA256Hash(UUID.randomUUID().toString() + user.getMerchantEMail());
		session.setSessionToken(hash);
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		session.setSessionExpiryDate(date);
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		session.setIdealSessionExpiry(idealDate);
		user.setUserSession(session);
		user.setFailCount(0);
		user.setUpdateReason("SECCESS_LOGIN");
		user.setUpdateBy("LOGIN_SYSTEM");
		repo.save(user);
		
		mobileOtpRepository.delete(motp);

		LoginResponseDto loginResponseDto = new LoginResponseDto();

		loginResponseDto.setUuid(user.getUuid());
		loginResponseDto.setEmail(user.getMerchantEMail());
		loginResponseDto.setPhoneNumber(user.getPhoneNumber());
		loginResponseDto.setSessionStatus(user.getUserSession().getSessionStatus());
		loginResponseDto.setSessionToken(user.getUserSession().getSessionToken());
		loginResponseDto.setSessionExpiryDate(user.getUserSession().getSessionExpiryDate());
		loginResponseDto.setMerchantId(user.getMerchantID());
		return loginResponseDto;
	}
	
    public LoginResponseDto loginWithOutOtp(LoginRequestDto dto) throws UserException, ValidationExceptions {
		
		long EXPIRATION_TIME = 60 * 24;
		logger.info("loginWithOutOtp");
		MerchantDetails user = repo.findByMerchantEMail(dto.getUserNameOrEmail());
		if (user == null) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}
		
		if (user.getUserSession() != null) {
			user.getUserSession().setSessionStatus(0);
		}

		UserSession session = new UserSession();
		if (user.getUserSession() != null) {
			session = user.getUserSession();
		}
		session.setSessionStatus(1);
		session.setUserAgent(dto.getUserAgent());
		session.setIpAddress(dto.getIpAddress());
		session.setUser(user);
		String hash = Encryption.getSHA256Hash(UUID.randomUUID().toString() + user.getMerchantEMail());
		session.setSessionToken(hash);
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		session.setSessionExpiryDate(date);
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		session.setIdealSessionExpiry(idealDate);
		user.setUserSession(session);
		user.setFailCount(0);
		user.setUpdateReason("SECCESS_LOGIN");
		user.setUpdateBy("LOGIN_SYSTEM");
		repo.save(user);
		logger.info("Session Set complete");

		LoginResponseDto loginResponseDto = new LoginResponseDto();

		loginResponseDto.setUuid(user.getUuid());
		loginResponseDto.setEmail(user.getMerchantEMail());
		loginResponseDto.setPhoneNumber(user.getPhoneNumber());
		loginResponseDto.setSessionStatus(user.getUserSession().getSessionStatus());
		loginResponseDto.setSessionToken(user.getUserSession().getSessionToken());
		loginResponseDto.setSessionExpiryDate(user.getUserSession().getSessionExpiryDate());
		loginResponseDto.setMerchantId(user.getMerchantID());
		return loginResponseDto;
	}
	
	
	private MobileOtp sendMobileOtp(MerchantDetails user) throws UserException, ValidationExceptions {
		
			int otp = new Random().nextInt(900000) + 100000;
			String message = "Hi " + user.getMerchantName() + ", Welcome to EazyPaymentz.Your OTP for login is " + otp;
			logger.info("SMS::" + message);
			// String message = "Hi " + user.getMerchantName() + " Welcome to IMobile, Your
			// OTP for login is " + otp;
			String mobileNo = user.getPhoneNumber();
			logger.info("MOBILE::" + mobileNo);
			MobileOtp mobileOtp = sendSMSMessage(mobileNo, user.getMerchantEMail(), otp);
			logger.info("smsSenderId::" + smsSenderId);
			try {
				smsCallTemplate.smsSendbyApi(message, mobileNo, smsSenderId);
				sendMail.sendLoginOtpMail(user.getMerchantName(), user.getMerchantEMail(), user.getPhoneNumber(),
					String.valueOf(otp));
			} catch (Exception e) {
				throw new ValidationExceptions(SMS_SEND_ERROR, FormValidationExceptionEnums.SMS_SEND_ERROR);
			}
			
			return mobileOtp;
	}
	
	private MobileOtp updateMerchantSendMobileOtp(MerchantDetails user, MerchantUpdateReq dto) throws UserException, ValidationExceptions {
		
		int otp = new Random().nextInt(900000) + 100000;
		String message = "Hi " + user.getMerchantName() + ", Welcome to EazyPaymentz.Your OTP for Updating Profile is " + otp;
		logger.info("SMS::" + message);
		// String message = "Hi " + user.getMerchantName() + " Welcome to IMobile, Your
		// OTP for login is " + otp;
		String mobileNo = dto.getPhoneNumber();
		logger.info("MOBILE::" + mobileNo);
		MobileOtp mobileOtp = sendSMSMessage(mobileNo, dto.getEmailId(), otp);
		logger.info("smsSenderId::" + smsSenderId);
		try {
			smsCallTemplate.smsSendbyApi(message, mobileNo, smsSenderId);
			sendMail.sendLoginOtpMail(user.getMerchantName(), dto.getEmailId(), dto.getPhoneNumber(),
				String.valueOf(otp));
		} catch (Exception e) {
			throw new ValidationExceptions(SMS_SEND_ERROR, FormValidationExceptionEnums.SMS_SEND_ERROR);
		}
		
		return mobileOtp;
}
	
	private MobileOtp sendSMSMessage(String phoneNumber, String userName,int otp) throws UserException, ValidationExceptions {
		MobileOtp motp = null;
		ZonedDateTime SessionexpireTime = ZonedDateTime.now(ZoneOffset.UTC).plus(sessionExpiryTime, ChronoUnit.MINUTES);
		Date dateExpiry = Date.from(SessionexpireTime.toInstant());
		motp = mobileOtpRepository.findBymobileNo(phoneNumber);

		if (motp == null) {
			motp = new MobileOtp();
			ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
			
			Date date = Date.from(expirationTime.toInstant());
			motp.setCount(1);
			motp.setExpDate(date);
			motp.setUserName(userName);
			motp.setMobileNo(phoneNumber);
			motp.setOtpSessionExpiry(dateExpiry);
			motp.setOtpSessionId(Utility.generateAppId());
			motp.setOtp(otp);
		} else {
			Calendar cal = Calendar.getInstance();
			Date dat = cal.getTime();
			if (motp.getCount() >= otpCount) {			
				
				logger.info("Checking the otp Counter");
				throw new ValidationExceptions(SMS_OTP_COUNTER_REACHED.replace("<AttemptCount>", String.valueOf(otpCount)), FormValidationExceptionEnums.TRY_AFTER_5_MINUTES);
			}
			if (motp.getExpDate().before(dat)){				
				throw new ValidationExceptions(OTP_EXPIRED, FormValidationExceptionEnums.OTP_EXPIRED);
			}
			logger.info("After Checking the otp Counter");
			if (motp.getCount() == otpCount) {
				motp.setCount(1);
			} else {
				motp.setCount(motp.getCount() + 1);
			}
			motp.setOtp(otp);
			motp.setOtpSessionExpiry(dateExpiry);
		}
		return mobileOtpRepository.save(motp);
	}

	public void userLogout(LogoutRequestDto dto) throws UserException, ValidationExceptions {

		MerchantDetails user = repo.findByuuid(dto.getUuid());

		if (!(user.getUserSession().getSessionToken()).equals(dto.getSessionToken())) {
			logger.error("Session Token does not Exist");

			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		user.getUserSession().setSessionStatus(0);
		repo.save(user);
	}
	
	public void initiatlPasswordChange(String userNameOrEmailId, String password) throws ValidationExceptions {

		if(!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}
		
		MerchantDetails user = repo.findByMerchantEMail(userNameOrEmailId);

		if (user==null) {
			logger.error("User ot found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPassword(password));
		user.setInitialPwdChange("Y");
		user.setUserStatus(UserStatus.ACTIVE.toString());
		
		repo.save(user);
		
	}

	public void passwordChange(String userNameOrEmailId, String password) throws ValidationExceptions {
		
		if(!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}
		
		MerchantDetails user = repo.findByMerchantEMail(userNameOrEmailId);

		if (user==null) {
			logger.error("User ot found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPassword(password));
		
		
		repo.save(user);
		
	}
	
	public void forgotPassword(String userNameOrEmailId) throws ValidationExceptions {
		MerchantDetails user = repo.findByMerchantEMail(userNameOrEmailId);
		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		int otp = (new Random()).nextInt(90000000) + 10000000;

		String message = "Hi " + user.getMerchantName() + " Your forgot password OTP for change the password is " + otp;

		UserOTPDetails userOTPDetails = userOTPDetailsRepository.findByUuid(user.getUuid());
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		if (userOTPDetails == null) {
			userOTPDetails = new UserOTPDetails();
			userOTPDetails.setEmailId(user.getMerchantEMail());
			userOTPDetails.setExpDate(date);
			userOTPDetails.setMobileNo(user.getPhoneNumber());
			userOTPDetails.setModeOfTr("MAIL");
			userOTPDetails.setOptCount(0);
			userOTPDetails.setUuid(user.getUuid());
			userOTPDetails.setOtpValue(String.valueOf(otp));

		} else {
			userOTPDetails.setExpDate(date);
			userOTPDetails.setModeOfTr("MAIL");
			userOTPDetails.setOptCount(userOTPDetails.getOptCount() + 1);
			userOTPDetails.setOtpValue(String.valueOf(otp));

		}
		userOTPDetailsRepository.save(userOTPDetails);

		sendMail.sendMailForgotPassword(user.getMerchantEMail(), message, "Forget Password : " + user.getMerchantName());
	}

	public void forgotPasswordChange(String userNameOrEmailId, String password, String otp)
			throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		MerchantDetails user = repo.findByMerchantEMail(userNameOrEmailId);
		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		UserOTPDetails userOTPDetails = userOTPDetailsRepository.findByUuid(user.getUuid());
		if (!userOTPDetails.getOtpValue().equalsIgnoreCase(otp)) {
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}

		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((userOTPDetails.getExpDate()).before(dat)) {
			userOTPDetailsRepository.delete(userOTPDetails);
			throw new ValidationExceptions(OTP_EXPIRED, FormValidationExceptionEnums.OTP_EXPIRED);
		}
		
		user.setPassword(Encryption.getEncryptedPassword(password));
		userOTPDetailsRepository.delete(userOTPDetails);
		repo.save(user);
	}
	
	public Object updateMerchant(String uuid, MerchantUpdateReq dto ) throws ValidationExceptions, NoSuchAlgorithmException, UserException {

		logger.info("merchantView In this Method.");
		
		MerchantDetails merchantDetails = repo.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		
		if(merchantDetails.getOtpStatus().equalsIgnoreCase(OtpStatus.DISABLE.toString())) {
			
			merchantDetails.setMerchantEMail(dto.getEmailId());
			merchantDetails.setPhoneNumber(dto.getPhoneNumber());
			
			repo.save(merchantDetails);
			MerchantCreateResponse merchantCreateResponse= new MerchantCreateResponse();
			merchantCreateResponse.setAppId(merchantDetails.getAppID());
			merchantCreateResponse.setEmailId(merchantDetails.getMerchantEMail());
			merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
			merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
			merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
			merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
			merchantCreateResponse.setSecretId(merchantDetails.getSecretId());
			
			sdto.getMsg().add("Merchant Details have been updated successfully!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("updateDetail", merchantCreateResponse);
		}
		else if(merchantDetails.getOtpStatus().equalsIgnoreCase(OtpStatus.ENABLE.toString())) {
			MobileOtp mobileOtp = updateMerchantSendMobileOtp(merchantDetails,dto);
			OTPConfirmation otpConfirmation = new OTPConfirmation();
			otpConfirmation.setUserId(dto.getEmailId());
			otpConfirmation.setOtpSessionId(mobileOtp.getOtpSessionId());
				
			sdto.getMsg().add("OTP has been send to the Specified Mobile Number and Email Id . Valid for 2 Mins!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("updateDetail", otpConfirmation);
		}

		return sdto;
		
//		MobileOtp mobileOtp = updateMerchantSendMobileOtp(merchantDetails,dto);
//		OTPConfirmation otpConfirmation = new OTPConfirmation();
//		otpConfirmation.setUserId(dto.getEmailId());
//		otpConfirmation.setOtpSessionId(mobileOtp.getOtpSessionId());
//		
//		return otpConfirmation;
	}
	
   public MerchantCreateResponse merchantUpdateVerifyOtp(String uuid, int otp, MerchantUpdateReq dto , String sessionId) throws UserException, ValidationExceptions {
	   
	     MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
	     MerchantDetails merchantDetails = repo.findByuuid(uuid);

	     if (merchantDetails == null) {
		  throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
	     }
	
		if (otp>1000000) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}
		MobileOtp motp = mobileOtpRepository.findByOtpAndUserNameAndOtpSessionId(otp, dto.getEmailId(), sessionId);
		if (motp == null ) {
			logger.info("Invalid otp==> ");
			throw new ValidationExceptions(OTP_MISMATCH, FormValidationExceptionEnums.OTP_MISMATCH);
		}
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if ((motp.getExpDate()).before(dat)) {
			mobileOtpRepository.delete(motp);			
			throw new ValidationExceptions(MERCHANT_OTP_EXPIRED, FormValidationExceptionEnums.OTP_EXPIRED);
		}
		

		merchantDetails.setMerchantEMail(dto.getEmailId());
		merchantDetails.setPhoneNumber(dto.getPhoneNumber());
		
		repo.save(merchantDetails);
		
		mobileOtpRepository.delete(motp);
		
		merchantCreateResponse.setAppId(merchantDetails.getAppID());
		merchantCreateResponse.setEmailId(merchantDetails.getMerchantEMail());
		merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(merchantDetails.getSecretId());
		
		return merchantCreateResponse;

	}
	

}
