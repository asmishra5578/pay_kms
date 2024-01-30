package com.asktech.pgateway.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.customInterface.IAllMerchantDetailsReport;
import com.asktech.pgateway.customInterface.ICustomerPgTransaction;
import com.asktech.pgateway.dto.admin.BusinessAssociateCreateRequest;
import com.asktech.pgateway.dto.admin.CreateAdminUserRequest;
import com.asktech.pgateway.dto.admin.CreatePGDetailsRequest;
import com.asktech.pgateway.dto.admin.MerchantCreateRequest;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.admin.ProcessSettlement;
import com.asktech.pgateway.dto.admin.ProcessSettlementRequest;
import com.asktech.pgateway.dto.admin.ProcessSettlementResponse;
import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.login.LoginResponseDto;
import com.asktech.pgateway.dto.login.LogoutRequestDto;
import com.asktech.pgateway.dto.merchant.AllMerchantDetails;
import com.asktech.pgateway.dto.merchant.MerchantKycDetailsResponse;
import com.asktech.pgateway.dto.merchant.MerchantPgdetails;
import com.asktech.pgateway.dto.merchant.MerchantServiceDetails;
import com.asktech.pgateway.dto.merchant.TransactionDetailsDto;
import com.asktech.pgateway.dto.merchant.UploadFileResponse;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.ApprovalStatus;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.KycStatus;
import com.asktech.pgateway.enums.OtpStatus;
import com.asktech.pgateway.enums.PGServices;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.enums.UserTypes;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.mail.MailIntegration;
import com.asktech.pgateway.model.BusinessAssociate;
import com.asktech.pgateway.model.BusinessAssociateCommissionDetails;
import com.asktech.pgateway.model.FileLoading;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.MerchantKycDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.PGServiceDetails;
import com.asktech.pgateway.model.PGServiceThresoldCalculation;
import com.asktech.pgateway.model.RefundDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.UserAdminDetails;
import com.asktech.pgateway.model.UserDetails;
import com.asktech.pgateway.model.UserOTPDetails;
import com.asktech.pgateway.model.UserSession;
import com.asktech.pgateway.repository.BusinessAssociateCommissionDetailsRepo;
import com.asktech.pgateway.repository.BusinessAssociateRepository;
import com.asktech.pgateway.repository.CommissionStructureRepository;
import com.asktech.pgateway.repository.FileUploadRepo;
import com.asktech.pgateway.repository.MerchantBalanceSheetRepository;
import com.asktech.pgateway.repository.MerchantBankDetailsRepository;
import com.asktech.pgateway.repository.MerchantDashBoardBalanceRepository;
import com.asktech.pgateway.repository.MerchantDetailsAddRepository;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantKycDetailsRepository;
import com.asktech.pgateway.repository.MerchantKycDocRepo;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGServicesRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.PGServiceDetailsRepository;
import com.asktech.pgateway.repository.PGServiceThresoldCalculationRepository;
import com.asktech.pgateway.repository.RefundDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.repository.UserAdminDetailsRepository;
import com.asktech.pgateway.repository.UserDetailsRepository;
import com.asktech.pgateway.repository.UserOTPDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.FileUpload;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PGGatewayAdminService implements ErrorValues {

	UserLoginService userLoginService;
	@Autowired
	FileUploadRepo fileUploadRepo;
	@Autowired
	private FileUpload fileStorageService;
	@Autowired
	MerchantKycDocRepo merchantKycDocRepo;
	@Autowired
	MerchantKycDetailsRepository merchantKycDetailsRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantBalanceSheetRepository merchantBalanceSheetRepository;
	@Autowired
	MerchantDashBoardBalanceRepository merchantDashBoardBalanceRepository;
	@Autowired
	MerchantBankDetailsRepository merchantBankDetailsRepository;
	@Autowired
	MerchantPGDetailsRepository merchantPGDetailsRepository;
	@Autowired
	MerchantPGServicesRepository merchantPGServicesRepository;
	@Autowired
	CommissionStructureRepository commissionStructureRepository;
	@Autowired
	UserDetailsRepository userDetailsRepository;
	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;
	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
	@Autowired
	PGServiceDetailsRepository pgServiceDetailsRepository;
	@Autowired
	MerchantDetailsAddRepository merchantDetailsAddRepository;
	@Autowired
	UserOTPDetailsRepository userOTPDetailsRepository;
	@Autowired
	BusinessAssociateRepository businessAssociateRepository;
	@Autowired
	BusinessAssociateCommissionDetailsRepo businessAssociateCommissionDetailsRepo;
	@Autowired
	RefundDetailsRepository refundDetailsRepository;
	@Autowired
	PGServiceThresoldCalculationRepository pgServiceThresoldCalculationRepository;
	@Autowired
	MailIntegration sendMail;

	ObjectMapper mapper = new ObjectMapper();

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminService.class);

	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;

	@Value("${otpExpiryTime}")
	long otpExpiryTime;

	long EXPIRATION_TIME = 60 * 24;

	@SuppressWarnings("unlikely-arg-type")
	public LoginResponseDto getAdminLogin(LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {

		if (dto.getPassword().isEmpty()) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		UserAdminDetails user = userAdminDetailsRepository.findByUserId(dto.getUserNameOrEmail());
		if (user == null) {

			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);

		}


		if (user.getUserStatus().equals(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE.toString())) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}

		logger.info("Input Password :: " + dto.getPassword());
		if (user.getPassword() != null) {
			if (dto.getPassword() == null) {
				throw new ValidationExceptions(PASSWORD_CANT_BE_BLANK,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);

			} else if (!user.getPassword().equals(Encryption.getEncryptedPassword(dto.getPassword()))) {
				throw new ValidationExceptions(PASSWORD_MISMATCH,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
			}
		} else {
			throw new ValidationExceptions(PASSWORD_MISMATCH, FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
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
		session.setUserAdmin(user);
		String hash = Encryption.getSHA256Hash(UUID.randomUUID().toString() + user.getUserId());
		session.setSessionToken(hash);
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		session.setSessionExpiryDate(date);
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		session.setIdealSessionExpiry(idealDate);
		user.setUserSession(session);
		userAdminDetailsRepository.save(user);

		LoginResponseDto loginResponseDto = new LoginResponseDto();

		loginResponseDto.setUuid(user.getUuid());
		loginResponseDto.setEmail(user.getEmailId());
		loginResponseDto.setPhoneNumber(user.getPhoneNumber());
		loginResponseDto.setSessionStatus(user.getUserSession().getSessionStatus());
		loginResponseDto.setSessionToken(user.getUserSession().getSessionToken());
		loginResponseDto.setSessionExpiryDate(user.getUserSession().getSessionExpiryDate());
		loginResponseDto.setUserType(user.getUserType());
		
		return loginResponseDto;
	}

	public void passwordChange(String userNameOrEmailId, String password) throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		UserAdminDetails user = userAdminDetailsRepository.findByEmailId(userNameOrEmailId);

		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPassword(password));

		userAdminDetailsRepository.save(user);

	}

	public void forgotPassword(String userNameOrEmailId) throws ValidationExceptions {
		UserAdminDetails user = userAdminDetailsRepository.findByEmailId(userNameOrEmailId);
		if (user == null) {
			logger.error("User not found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		int otp = (new Random()).nextInt(90000000) + 10000000;

		String message = "Hi " + user.getUserName() + " Your forgot password OTP for change the password is " + otp;

		UserOTPDetails userOTPDetails = userOTPDetailsRepository.findByUuid(user.getUuid());
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(otpExpiryTime, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		if (userOTPDetails == null) {
			userOTPDetails = new UserOTPDetails();
			userOTPDetails.setEmailId(user.getEmailId());
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

		sendMail.sendMailForgotPassword(user.getEmailId(), message, "Forget Password : " + user.getUserName());
	}

	public void forgotPasswordChange(String userNameOrEmailId, String password, String otp)
			throws ValidationExceptions {

		if (!Validator.isValidatePassword(password)) {
			throw new ValidationExceptions(PASSWORD_VALIDATION, FormValidationExceptionEnums.PASSWORD_VALIDATION);
		}

		UserAdminDetails user = userAdminDetailsRepository.findByEmailId(userNameOrEmailId);
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
		userAdminDetailsRepository.save(user);
	}

	public MerchantCreateResponse createMerchant(String merchantCreate, String uuid)
			throws ValidationExceptions, IllegalAccessException, NoSuchAlgorithmException {

		MerchantCreateRequest merchantCreateRequest = new MerchantCreateRequest();
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantsDetails = new MerchantDetails();

		try {

			merchantCreateRequest = mapper.readValue(merchantCreate, MerchantCreateRequest.class);

		} catch (Exception e) {
			throw new ValidationExceptions(JSON_PARSE_ISSUE_MERCHANT_REQUEST,
					FormValidationExceptionEnums.JSON_PARSE_EXCEPTION);
		}

		if (txnParam(merchantCreateRequest.getMerchantName())==false && txnParam(merchantCreateRequest.getPhoneNumber())==false
				&& txnParam(merchantCreateRequest.getEmailId())==false && txnParam(merchantCreateRequest.getCompanyName())==false) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE, FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}

		merchantsDetails = merchantDetailsRepository.findByMerchantEMail(merchantCreateRequest.getEmailId());
		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_EXITS, FormValidationExceptionEnums.MERCHANT_ALREADY_EXISTS);
		} 
		
		UserAdminDetails adminUserDetails = userAdminDetailsRepository.findByEmailId(merchantCreateRequest.getEmailId());
		if(adminUserDetails !=null) {
			throw new ValidationExceptions(EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM, FormValidationExceptionEnums.EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM);
		}
		
		
		merchantsDetails = new MerchantDetails();
		String appId = Utility.generateAppId();
		String secrecKey = Encryption.genSecretKey();

		merchantsDetails.setMerchantID(String.valueOf(Utility.getMerchantsID()));
		merchantsDetails.setMerchantEMail(merchantCreateRequest.getEmailId());
		merchantsDetails.setMerchantName(merchantCreateRequest.getMerchantName());
		merchantsDetails.setPhoneNumber(merchantCreateRequest.getPhoneNumber());
		merchantsDetails.setAppID(appId);
		merchantsDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvv(secrecKey));
		merchantsDetails.setUuid(UUID.randomUUID().toString());
		merchantsDetails.setCreatedBy(uuid);
		merchantsDetails.setSaltKey(UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
		merchantsDetails.setPassword(Encryption.getEncryptedPassword(Encryption.generateRandomPassword(8)));
		merchantsDetails.setTr_mail_flag("Y");
		merchantsDetails.setCompanyName(merchantCreateRequest.getCompanyName());
		merchantsDetails.setSupportEmailId(merchantCreateRequest.getSupportEmailId());
		merchantsDetails.setSupportPhoneNo(merchantCreateRequest.getSupportPhoneNo());
		merchantsDetails.setMerchantType(merchantCreateRequest.getMerchantType());
		merchantsDetails.setLogoUrl(merchantCreateRequest.getLogoUrl());

		merchantsDetails = merchantDetailsRepository.save(merchantsDetails);
		
		// code for default configuration association for Merchant .
		merchantDefaultPgAssociation(merchantsDetails);
		
		merchantCreateResponse.setAppId(appId);
		merchantCreateResponse.setEmailId(merchantsDetails.getMerchantEMail());
		merchantCreateResponse.setKycStatus(merchantsDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantsDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantsDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantsDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(secrecKey);
		merchantCreateResponse.setCompanyName(merchantsDetails.getCompanyName());
		merchantCreateResponse.setSupportEmailId(merchantsDetails.getSupportEmailId());
		merchantCreateResponse.setSupportPhoneNo(merchantsDetails.getSupportPhoneNo());
		merchantCreateResponse.setMerchantType(merchantsDetails.getMerchantType());
		sendMail.sendMailCreateMerchant(merchantsDetails);	
		

		return merchantCreateResponse;
	}
	
	 public boolean txnParam(String val) {
	    	if( val == null || val.isBlank() || val.isEmpty()) {
				return false;
			}
	    	return true;
	    }

	public void merchantDefaultPgAssociation(MerchantDetails merchantsDetails) {
		MerchantPGDetails merchantPGDetails = new MerchantPGDetails();
		MerchantPGServices merchantPGServices = new MerchantPGServices();
	
		for(PGServiceDetails pgServiceDetails : pgServiceDetailsRepository.findAllByDefaultService("Y")) {
			PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgServiceDetails.getPgId());
			
			merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantsDetails.getMerchantID(), pgConfigurationDetails.getPgUuid());
			if(merchantPGDetails== null) {
				merchantPGDetails = new MerchantPGDetails();
				merchantPGDetails.setCreatedBy(merchantsDetails.getCreatedBy());
				merchantPGDetails.setMerchantID(merchantsDetails.getMerchantID());
				merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
				merchantPGDetails.setMerchantPGId(pgConfigurationDetails.getPgUuid());
				merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
				merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey());
				merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
				merchantPGDetails.setReason("Default PG Configuration");
				merchantPGDetails.setStatus(ApprovalStatus.NEW.toString());
				merchantPGDetailsRepository.save(merchantPGDetails);
			}
			
			merchantPGServices = merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(
					merchantsDetails.getMerchantID(), pgConfigurationDetails.getPgUuid(), pgServiceDetails.getPgServices());
					
			if(merchantPGServices==null) {
				merchantPGServices = new MerchantPGServices();
				merchantPGServices.setCreatedBy(merchantsDetails.getCreatedBy());
				merchantPGServices.setMerchantID(merchantsDetails.getMerchantID());
				merchantPGServices.setPgID(pgConfigurationDetails.getPgUuid());
				merchantPGServices.setProcessedBy(merchantsDetails.getCreatedBy());
				merchantPGServices.setService(pgServiceDetails.getPgServices());
				merchantPGServices.setStatus(ApprovalStatus.NEW.toString());
				merchantPGServicesRepository.save(merchantPGServices);	
			}
			
		}
	}
	
	public static boolean hasBlankVariables(Object obj) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			// Danger!
			String str = (String) field.get(obj);
			if (StringUtils.isEmpty(str)) {
				return true;
			}
		}
		return false;
	}
	
	public MerchantCreateResponse refreshSecretKey(String uuid) throws ValidationExceptions, NoSuchAlgorithmException {

		logger.info("merchantView In this Method.");
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		String secrecKey = Encryption.genSecretKey();
		String appId = Utility.generateAppId();

		merchantDetails.setAppID(appId);
		merchantDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvv(secrecKey));
		merchantDetailsRepository.save(merchantDetails);

		merchantCreateResponse.setAppId(appId);
		merchantCreateResponse.setEmailId(merchantDetails.getMerchantEMail());
		merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(secrecKey);

		return merchantCreateResponse;
	}

	public MerchantPGDetails associatePGDetails(String merchantId, String pgUuid, String uuid)
			throws ValidationExceptions {
		logger.info("createPGDetails In this Method.");
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		MerchantPGDetails merchantPGDetails = new MerchantPGDetails();

		if (merchantDetails == null) {

			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (pgUuid == null) {
			throw new ValidationExceptions(MERCHANT_PG_NAME_NOT_FOUND,FormValidationExceptionEnums.MERCHANT_PG_NAME_NOT_FOUND);
		}

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(MERCHANT_PG_NAME_NOT_FOUND,FormValidationExceptionEnums.MERCHANT_PG_NAME_NOT_FOUND);
		}
		if (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
		}

		merchantPGDetails = merchantPGDetailsRepository
				.findByMerchantIDAndMerchantPGId(merchantDetails.getMerchantID(), pgUuid);

		if (merchantPGDetails != null) {

			throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_EXISTS,FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_EXISTS);
		}

		merchantPGDetails = new MerchantPGDetails();
		merchantPGDetails.setMerchantID(merchantDetails.getMerchantID());
		merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
		merchantPGDetails.setMerchantPGId(pgConfigurationDetails.getPgUuid());
		merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
		merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey());
		merchantPGDetails.setStatus(ApprovalStatus.NEW.toString());
		merchantPGDetails.setMerchantPGId(pgUuid);
		merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
		
		return merchantPGDetailsRepository.save(merchantPGDetails);
	}

	public MerchantPGServices associatePGServices(String pgUuid, String merchantService, String uuid,
			String merchantId) throws ValidationExceptions {
		logger.info("associatePGServices In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		//MerchantPGServices merchantPGServices = new MerchantPGServices();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (merchantService == null) {
			throw new ValidationExceptions(MERCHANT_PG_APP_ID_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_PG_APP_ID_NOT_FOUND);
		}

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(MERCHANT_PG_NAME_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_PG_NAME_NOT_FOUND);
		}
		if (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
		}

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository
				.findByMerchantIDAndMerchantPGId(merchantDetails.getMerchantID(), pgUuid);

		if (merchantPGDetails == null) {
			throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_NON_EXISTS,
					FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_NON_EXISTS);
		}

		PGServiceDetails pgServiceDetails = pgServiceDetailsRepository
				.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getPgUuid()), merchantService.toUpperCase());
		if (pgServiceDetails == null) {
			throw new ValidationExceptions(PG_SERVICE_ASSOCIATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_ASSOCIATION_NOT_FOUND);
		}
		
		logger.info("Before Checking DB :: "+ merchantId +" , PG id :: "+String.valueOf(pgConfigurationDetails.getId())+ " , service :: "+merchantService);
		
		MerchantPGServices merchantPGServices = merchantPGServicesRepository.findByMerchantIDAndPgIDAndService(merchantId, String.valueOf(merchantPGDetails.getId()), 
				merchantService);
		
		
		if(merchantPGServices != null ) {
			throw new ValidationExceptions(MERCHANT_SERVICE_PRESENT_AS_ACTIVE,
					FormValidationExceptionEnums.MERCHANT_SERVICE_PRESENT_AS_ACTIVE);
		}
		
		
		merchantPGServices = new MerchantPGServices();
		merchantPGServices.setMerchantID(merchantDetails.getMerchantID());
		merchantPGServices.setPgID(merchantPGDetails.getMerchantPGId());
		merchantPGServices.setService(merchantService.toUpperCase());
		merchantPGServices.setStatus(ApprovalStatus.NEW.toString());
		merchantPGServices.setCreatedBy(uuid);
		merchantPGServices.setProcessedBy(uuid);

		return merchantPGServicesRepository.save(merchantPGServices);
	}

	/*
	public CommissionStructure createCommissionstructure(String merchantPGNme, String merchantService, int pgAmount,
			String pgCommissionType, int askAmount, String askCommissionType, String uuid) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantPGName(merchantPGNme);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		if (merchantService == null) {
			throw new ValidationExceptions(MERCHANT_SERVICE_TYPE, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (pgCommissionType == null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
					FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (pgAmount <= 0) {
			throw new ValidationExceptions(MERCHANT_COMM_AMOUNT, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (askCommissionType == null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
					FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (askAmount <= 0) {
			throw new ValidationExceptions(MERCHANT_COMM_AMOUNT, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (merchantPGDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		CommissionStructure commissionStructure = commissionStructureRepository.findByMerchantIdAndPgIdAndServiceType(
				merchantDetails.getMerchantID(), String.valueOf(merchantPGDetails.getId()), merchantService);

		if (commissionStructure != null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_EXISTS,
					FormValidationExceptionEnums.DUPLICATE_COMMISSON_FOR_MERCHANT);
		}
		logger.info("Before CommissionStructure get repo ");
		commissionStructure = new CommissionStructure();

		commissionStructure.setPgAmount(pgAmount);
		commissionStructure.setPgCommissionType(pgCommissionType);
		commissionStructure.setAskAmount(askAmount);
		commissionStructure.setAskCommissionType(askCommissionType);
		commissionStructure.setMerchantId(merchantDetails.getMerchantID());
		commissionStructure.setPgId(String.valueOf(merchantPGDetails.getId()));
		commissionStructure.setServiceType(merchantService);
		commissionStructure.setStatus(ApprovalStatus.NEW.toString());

		commissionStructureRepository.save(commissionStructure);

		return commissionStructure;
	}
	*/
	/*
	public CommissionStructure createCommissionstructureAskTech(String merchantPGNme, String merchantService,
			int pgAmount, String pgCommissionType, int askAmount, String askCommissionType)
			throws ValidationExceptions {

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantPGName(merchantPGNme);

		if (merchantService == null) {
			throw new ValidationExceptions(MERCHANT_SERVICE_TYPE, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (askCommissionType == null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
					FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (askAmount <= 0) {
			throw new ValidationExceptions(MERCHANT_COMM_AMOUNT, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (merchantPGDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		CommissionStructure commissionStructure = commissionStructureRepository
				.checkCommissionAskTech(String.valueOf(merchantPGDetails.getId()), merchantService);

		if (commissionStructure != null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_EXISTS,
					FormValidationExceptionEnums.DUPLICATE_COMMISSON_FOR_MERCHANT);
		}
		logger.info("Before CommissionStructure get repo ");
		commissionStructure = new CommissionStructure();

		commissionStructure.setAskAmount(askAmount);
		commissionStructure.setAskCommissionType(askCommissionType);
		commissionStructure.setPgAmount(pgAmount);
		commissionStructure.setPgCommissionType(pgCommissionType);
		commissionStructure.setPgId(String.valueOf(merchantPGDetails.getId()));
		commissionStructure.setServiceType(merchantService);
		commissionStructure.setStatus(ApprovalStatus.NEW.toString());

		commissionStructureRepository.save(commissionStructure);

		return commissionStructure;
	}
	*/
	public List<UserDetails> getUserDetails(String custEmailorPhone) throws ValidationExceptions {

		if (custEmailorPhone == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}

		List<UserDetails> userDetails = userDetailsRepository.findAllByEmailIdOrPhoneNumber(custEmailorPhone,
				custEmailorPhone);

		if (userDetails == null) {
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.USER_NOT_FOUND);
		}

		return userDetails;
	}

	public UserAdminDetails createAdminUser(CreateAdminUserRequest createAdminUserRequest) throws ValidationExceptions {

		UserAdminDetails userAdmin = userAdminDetailsRepository.findByEmailId(createAdminUserRequest.getEmailId());

		if (userAdmin != null) {
			throw new ValidationExceptions(ADMIN_USER_EXISTS, FormValidationExceptionEnums.EMAIL_ALREADY_EXISTS);
		}

		MerchantDetails merchantDetails = merchantDetailsRepository
				.findByMerchantEMail(createAdminUserRequest.getEmailId());
		if (merchantDetails != null) {
			throw new ValidationExceptions(ADMIN_USER_EXISTS, FormValidationExceptionEnums.EMAIL_ALREADY_EXISTS);
		}

		UserAdminDetails userAdminDetails = new UserAdminDetails();
		userAdminDetails.setAddress1(createAdminUserRequest.getAddress1());
		userAdminDetails.setAddress2(createAdminUserRequest.getAddress2());
		userAdminDetails.setAddress3(createAdminUserRequest.getAddress3());
		userAdminDetails.setCity(createAdminUserRequest.getCity());
		userAdminDetails.setCompantName(createAdminUserRequest.getCompantName());
		userAdminDetails.setCountry(createAdminUserRequest.getCountry());
		userAdminDetails.setEmailId(createAdminUserRequest.getEmailId());
		userAdminDetails.setPhoneNumber(createAdminUserRequest.getPhoneNumber());
		userAdminDetails.setPincode(createAdminUserRequest.getPincode());
		userAdminDetails.setUserId(createAdminUserRequest.getEmailId());
		userAdminDetails.setUserName(createAdminUserRequest.getUserName());
		userAdminDetails.setUuid(UUID.randomUUID().toString());
		userAdminDetails.setUserType(UserTypes.ADMIN.toString());
		userAdminDetails.setUserStatus(UserStatus.ACTIVE.toString());
		userAdminDetails.setPassword(Encryption.getEncryptedPassword(Encryption.generateRandomPassword(8)));

		UserAdminDetails userAdminDetailsResponse = userAdminDetailsRepository.save(userAdminDetails);

		userAdminDetailsResponse.setPassword(Encryption.getDecryptedPassword(userAdminDetailsResponse.getPassword()));
		return userAdminDetailsResponse;

	}

	public SuccessResponseDto createPg(CreatePGDetailsRequest createPGDetailsRequest, String userName)
			throws ValidationExceptions {

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgName(createPGDetailsRequest.getPgName());
		if (pgConfigurationDetails != null) {

			throw new ValidationExceptions(PG_ALREADY_CREATED, FormValidationExceptionEnums.PG_ALREADYCREATED);

		}

		pgConfigurationDetails = new PGConfigurationDetails();
		pgConfigurationDetails.setCreatedBy(userName);
		pgConfigurationDetails.setPgAppId(createPGDetailsRequest.getPgAppId());
		pgConfigurationDetails.setPgName(createPGDetailsRequest.getPgName());
		pgConfigurationDetails.setPgSecret(Encryption.encryptCardNumberOrExpOrCvv(createPGDetailsRequest.getPgSecretKey()));
		pgConfigurationDetails.setPgUuid(UUID.randomUUID().toString());
		pgConfigurationDetails.setStatus(UserStatus.PENDING.toString());
		pgConfigurationDetails.setPgSaltKey(createPGDetailsRequest.getPgSaltKey());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("PG Created Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", pgConfigurationDetailsRepository.save(pgConfigurationDetails));	
		return sdto;
	}

	public Object getPgDetails() {

		return pgConfigurationDetailsRepository.getAllPgDetails();
	}
	
	public PGServiceDetails createPgServices(String pgUuid, String pgServices, String userId, String defaultTag, 
			long thresoldMonth, long thresoldDay, long thresoldWeek , long thresold3Month , long thresold6Month ,long thresoldYear)
			throws ValidationExceptions {

		if(defaultTag.equalsIgnoreCase("Y") || defaultTag.equalsIgnoreCase("N")) {
			
		}else {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		
		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {

			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);

		}
		PGServiceDetails pgServiceDetails = pgServiceDetailsRepository
				.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getId()), pgServices.toUpperCase());

		if (pgServiceDetails != null) {
			throw new ValidationExceptions(PG_SERVICES, FormValidationExceptionEnums.PG_SERVICES_DEFINED);
		}
		
		if(defaultTag.toUpperCase().equalsIgnoreCase("Y")) {
			PGServiceDetails pgServiceDetailsDefault = pgServiceDetailsRepository.findByPgServicesAndDefaultService(pgServices.toUpperCase(),defaultTag.toUpperCase());
			if(pgServiceDetailsDefault != null) {
				throw new ValidationExceptions(PG_DEFAULT_SERVICE_SCOPE, FormValidationExceptionEnums.PG_DEFAULT_SERVICE_SCOPE);
			}
		}
		pgServiceDetails = new PGServiceDetails();
		pgServiceDetails.setPgId(pgConfigurationDetails.getPgUuid());
		pgServiceDetails.setPgServices(pgServices.toUpperCase());
		pgServiceDetails.setStatus(UserStatus.PENDING.toString());
		pgServiceDetails.setUpdatedBy(userId);
		pgServiceDetails.setDefaultService(String.valueOf(defaultTag));
		pgServiceDetails.setThresoldMonth(thresoldMonth);
		pgServiceDetails.setThresoldDay(thresoldDay);
		pgServiceDetails.setThresoldWeek(thresoldWeek);
		pgServiceDetails.setThresold3Month(thresold3Month);
		pgServiceDetails.setThresold6Month(thresold6Month);
		pgServiceDetails.setThresoldYear(thresoldYear);
		pgServiceDetails.setCreatedBy(userId);	
		
		PGServiceThresoldCalculation pgServiceThresoldCalculation = pgServiceThresoldCalculationRepository.findByPgIdAndServiceType(
				pgConfigurationDetails.getPgUuid(),pgServices.toUpperCase());
		if(pgServiceThresoldCalculation==null) {
			pgServiceThresoldCalculation = new PGServiceThresoldCalculation();
			pgServiceThresoldCalculation.setPgId(pgConfigurationDetails.getPgUuid());
			pgServiceThresoldCalculation.setServiceType(pgServices.toUpperCase());
			pgServiceThresoldCalculation.setCreatedBy(userId);
			pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation);
		}

		return pgServiceDetailsRepository.save(pgServiceDetails);
	}

	public void userLogout(LogoutRequestDto dto, UserAdminDetails user) throws UserException, ValidationExceptions {
		if (!(user.getUserSession().getSessionToken()).equals(dto.getSessionToken())) {
			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		user.getUserSession().setSessionStatus(0);
		// Log4jLogger.saveLog("User logout success==> " + user.toString());
		userAdminDetailsRepository.save(user);
	}

	public SuccessResponseDto merchantStatusAdmin(UserAdminDetails user) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Status Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantStatus", merchantDetailsAddRepository.countTotalMerchantDetailsByUserStatusAndCreatedBy(user.getUuid()));		
		return sdto;
	}

	public SuccessResponseDto merchantStatusList(UserAdminDetails user) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Status Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantStatus", merchantDetailsAddRepository.getCompleteMerchantList(user.getUuid()));		
		return sdto;
	}

	public Object merchantStatusTransactionLastDay(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last Day Transaction Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantDetailsAddRepository.getYesterdayTrDetails(userAdminDetails.getUuid()));		
		return sdto;
	}

	public Object merchantStatusTransactionToday(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Today Transaction Status !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantDetailsAddRepository.getTodayTrDetails(userAdminDetails.getUuid()));		
		return sdto;
	}

	public Object merchantStatusTransactionCurrMonth(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Current Month Transaction Detail !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantDetailsAddRepository.getCurrMonthTrDetails(userAdminDetails.getUuid()));		
		return sdto;
	}

	public Object merchantStatusTransactionLastMonth(UserAdminDetails userAdminDetails) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last Month Transaction Detail !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantDetailsAddRepository.getLastMonthTrDetails(userAdminDetails.getUuid()));		
		return sdto;
	}

	public SuccessResponseDto updatMerchantStatus(String uuid, String merchantId, String statusUpdate)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			merchantDetails.setUserStatus(UserStatus.ACTIVE.toString());
		} else {
			merchantDetails.setUserStatus(UserStatus.BLOCKED.toString());
		}

		merchantDetails.setKycStatus(statusUpdate);
		merchantDetailsRepository.save(merchantDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantDetails);		
		return sdto;
	}

	public SuccessResponseDto updateMerchantPGDetailsStatus(String merchantId, String pgUuid, String statusUpdate)
			throws ValidationExceptions {

		List<MerchantPGServices> updateMerchantPGServices = new ArrayList<MerchantPGServices>();

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantId,pgUuid);

		if (merchantPGDetails == null) {
			throw new ValidationExceptions(MERCHANT_PG_ASSOCIATION_EXISTS,FormValidationExceptionEnums.MERCHANT_PG_ASSOCIATION_EXISTS);

		}

		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
		} else {
			merchantPGDetails.setStatus(UserStatus.BLOCKED.toString());
		}

		if (!merchantPGDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			List<MerchantPGServices> listMerchantPGService = merchantPGServicesRepository.findByMerchantIDAndPgID(merchantId, merchantPGDetails.getMerchantPGId());
			for (MerchantPGServices merchantPGServices : listMerchantPGService) {
				merchantPGServices.setStatus(merchantPGDetails.getStatus());
				updateMerchantPGServices.add(merchantPGServices);
			}
			merchantPGServicesRepository.saveAll(updateMerchantPGServices);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant PG Service Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantPGDetailsRepository.save(merchantPGDetails));		
		return sdto;
	}

	public Object updatePGDetails(String uuid, String pgUuid, String statusUpdate) throws ValidationExceptions {

		List<PGServiceDetails> updatePGServiceDetails = new ArrayList<PGServiceDetails>();
		logger.info("Input PgId :: "+pgUuid);

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);
		}

		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			pgConfigurationDetails.setStatus(UserStatus.ACTIVE.toString());
			pgConfigurationDetails.setUpdatedBy(uuid);
		} else {
			pgConfigurationDetails.setStatus(UserStatus.BLOCKED.toString());
			pgConfigurationDetails.setUpdatedBy(uuid);
		}
		pgConfigurationDetailsRepository.save(pgConfigurationDetails);
		
		

		if (!pgConfigurationDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			List<PGServiceDetails> listPGServiceDetails = pgServiceDetailsRepository
					.findByPgId(String.valueOf(pgConfigurationDetails.getId()));

			for (PGServiceDetails pgServiceDetails : listPGServiceDetails) {
				pgServiceDetails.setStatus(pgConfigurationDetails.getStatus());
				pgServiceDetails.setUpdatedBy(uuid);
				updatePGServiceDetails.add(pgServiceDetails);
			}
			pgServiceDetailsRepository.saveAll(updatePGServiceDetails);
			
			List<MerchantPGDetails> listMerchantPGDetails = merchantPGDetailsRepository.findAllByMerchantPGId(pgUuid);
			
			for(MerchantPGDetails merchantPGDetails : listMerchantPGDetails) {
				List<MerchantPGServices> listMerchantPGService = merchantPGServicesRepository.findAllByPgID(String.valueOf(merchantPGDetails.getId()));
				for(MerchantPGServices merchantPGService : listMerchantPGService) {
					merchantPGService.setStatus(statusUpdate);
					merchantPGServicesRepository.save(merchantPGService);
				}
				
				merchantPGDetails.setStatus(statusUpdate);
				merchantPGDetailsRepository.save(merchantPGDetails);
			}
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("PG Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", pgConfigurationDetails);		
		return sdto;
	}

	public Object getTransactiilteronDetailsWithDateF(String merchantId, String dateFrom, String dateTo)
			throws ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		List<TransactionDetails> listTransactionDetals = new ArrayList<TransactionDetails>();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		if (dateTo.length() != 0) {
			listTransactionDetals = transactionDetailsRepository.getTransactionDateRange(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo));
			
		} else {
			listTransactionDetals = transactionDetailsRepository.getTransactionDate(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom));
		}
		
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for(TransactionDetails tr : listTransactionDetals) {
			TransactionDetailsDto trd = new TransactionDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setAmount(Float.toString(((float)tr.getAmount()/100)));
			trd.setPaymentOption(tr.getPaymentOption());
			trd.setOrderID(tr.getOrderID());
			trd.setStatus(tr.getStatus());
			trd.setPaymentMode(tr.getPaymentMode());
			trd.setTxtMsg(tr.getTxtMsg());
			trd.setTransactionTime(tr.getCreated().toString());
			trd.setMerchantOrderId(tr.getMerchantOrderId());
			trd.setMerchantReturnURL(tr.getMerchantReturnURL());
			if (tr.getVpaUPI() != null) {
				trd.setVpaUPI(
						SecurityUtils.decryptSaveData(tr.getVpaUPI()).replace("\u0000", ""));
			}
			if (tr.getPaymentCode() != null) {
				//trd.setWalletOrBankCode(SecurityUtils.decryptSaveData(tr.getPaymentCode()).replace("\u0000", ""));
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(tr.getCardNumber())).replace("\u0000", ""));
			}
			trdetails.add(trd);
		}
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Transaction Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("TransactionDetails", trdetails);	
		return sdto;

	//	return listTransactionDetals;
	}

	public SuccessResponseDto processsettlement(UserAdminDetails userAdminDetails,
			ProcessSettlementRequest processSettlementRequest) {

		ProcessSettlementResponse processSettlementResponse = new ProcessSettlementResponse();
		List<ProcessSettlement> listProcessSettlement = new ArrayList<ProcessSettlement>();
		boolean flag = false;

		for (ProcessSettlement processSettlement : processSettlementRequest.getProcessSettlement()) {
			MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository
					.findByOrderId(processSettlement.getOrderid());
			if (merchantBalanceSheet == null) {
				processSettlement.setRemarks("OrderID Not Found in System");
				flag = true;

			}

			if ((processSettlement.getCustCommission() + processSettlement.getPgCommission()
					+ processSettlement.getSettlementAmount()) > merchantBalanceSheet.getAmount()) {
				processSettlement
						.setRemarks("Settlemnt Can't be processed due to Amount is less than settlement amount .");
				flag = true;
			}
			if (merchantBalanceSheet.getSettlementStatus().equalsIgnoreCase("PROCESS")) {
				processSettlement.setRemarks("Settlemnt already done .. Can't be process now .");
				flag = true;
			}

			if (!flag) {
				merchantBalanceSheet.setSettlementStatus("PROCESS");
				merchantBalanceSheet.setSettlementDate(new Date());
				processSettlement.setRemarks("Order Processed...");
				merchantBalanceSheet.setAskCommission(processSettlement.getCustCommission());
				merchantBalanceSheet.setPgCommission(processSettlement.getPgCommission());
				merchantBalanceSheet.setSettleAmountToMerchant(processSettlement.getSettlementAmount());
				merchantBalanceSheet.setSettleBy(userAdminDetails.getUuid());
				merchantBalanceSheetRepository.save(merchantBalanceSheet);
			}
			listProcessSettlement.add(processSettlement);
		}
		processSettlementResponse.setProcessSettlement(listProcessSettlement);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Settlement Detail !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("SettlementDetail", processSettlementResponse);		
		return sdto;
	}

	public SuccessResponseDto updateMerchantPGServiceStatus(String merchantId, String pgUuid, String statusUpdate,
			String service, String uuid) throws ValidationExceptions {

		MerchantPGServices merchantPGService =  new MerchantPGServices();
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		logger.info("Input Details :: PGName :: "+pgUuid+" , MerchantId :: "+merchantId);

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(merchantId,
				pgUuid);

		if (merchantPGDetails == null) {
			throw new ValidationExceptions(PG_SERVICE_ASSOCIATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_ASSOCIATION_NOT_FOUND);

		}
		// logger.info("MERCHANT PG STATUS:"+merchantPGDetails.getStatus());
		if (!merchantPGDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			throw new ValidationExceptions(PG_NOT_ACTIVE, FormValidationExceptionEnums.PG_NOT_ACTIVE);
		}
		
		if(statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			merchantPGService = merchantPGServicesRepository
					.findByMerchantIDAndServiceAndStatus(merchantId,  service ,statusUpdate );
			
			if(merchantPGService != null) {
				throw new ValidationExceptions(MERCHANT_SERVICE_PRESENT_AS_ACTIVE,
						FormValidationExceptionEnums.MERCHANT_SERVICE_PRESENT_AS_ACTIVE);
			}
		}

		merchantPGService = merchantPGServicesRepository
				.findByMerchantIDAndPgIDAndService(merchantId, merchantPGDetails.getMerchantPGId(), service);

		if (merchantPGService == null) {
			throw new ValidationExceptions(MERCHANT_PG_SERVICE_NOT_ASSOCIATED,
					FormValidationExceptionEnums.MERCHANT_PG_SERVICE_NOT_ASSOCIATED);

		}
		logger.info("Before Checking ...");
		
		
		
		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {				
			
			merchantPGService.setStatus(UserStatus.ACTIVE.toString());			

		} else {
			merchantPGService.setStatus(UserStatus.BLOCKED.toString());
		}
		merchantPGService.setUpdatedBy(uuid);
		merchantPGService.setProcessedBy(uuid);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant PG Service Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantPGServicesRepository.save(merchantPGService));		
		return sdto;
	}

	public Object updatePGService(String uuid, String pgUuid, String statusUpdate, String service)
			throws ValidationExceptions, JsonProcessingException {

		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgUuid(pgUuid);
		if (pgConfigurationDetails == null) {
			throw new ValidationExceptions(PG_NOT_CREATED, FormValidationExceptionEnums.PG_NOT_CREATED);
		}
		logger.info("pgConfigurationDetails::" + pgConfigurationDetails.getPgName());
		PGServiceDetails pgServiceDetails = pgServiceDetailsRepository
				.findByPgIdAndPgServices(String.valueOf(pgConfigurationDetails.getPgUuid()), service);

		if (pgServiceDetails == null) {
			throw new ValidationExceptions(PG_SERVICE_ASSOCIATION_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_ASSOCIATION_NOT_FOUND);
		}

		if (statusUpdate.equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
			pgServiceDetails.setStatus(UserStatus.ACTIVE.toString());

		} else {
			pgServiceDetails.setStatus(UserStatus.BLOCKED.toString());
		}
		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantPGId(pgConfigurationDetails.getPgUuid());

		if (merchantPGDetails != null) {

			logger.info("MerchantPGDetails :: " + Utility.convertDTO2JsonString(merchantPGDetails));
			if (pgServiceDetails.getStatus().equalsIgnoreCase(UserStatus.BLOCKED.toString())) {
				logger.info("Inside Merchant Service :: " + pgConfigurationDetails.getId());

				List<MerchantPGServices> updMerchantPGServices = new ArrayList<MerchantPGServices>();
				List<MerchantPGServices> listMerchantPGService = merchantPGServicesRepository
						.findAllByPgIDAndService(merchantPGDetails.getMerchantPGId(), service);

				for (MerchantPGServices merchantPGServices : listMerchantPGService) {

					logger.info("Inside Merchant Service :: " + Utility.convertDTO2JsonString(merchantPGServices));
					merchantPGServices.setStatus(UserStatus.BLOCKED.toString());
					updMerchantPGServices.add(merchantPGServices);
				}

				merchantPGServicesRepository.saveAll(updMerchantPGServices);
			}
		}
		pgServiceDetails.setUpdatedBy(uuid);
		pgServiceDetailsRepository.save(pgServiceDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("PG Service Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", pgConfigurationDetails);		
		return sdto;
	}

	public SuccessResponseDto getAllMerchantDetailsReport() throws JsonProcessingException {

		List<IAllMerchantDetailsReport> getMerchantDetailsReport = merchantPGServicesRepository
				.getAllMerchantDetailsReport();

		List<AllMerchantDetails> allmerchants = new LinkedList<AllMerchantDetails>();
		
		String meremail = "";
		AllMerchantDetails m = null;
		String pgname = "";
		List<MerchantPgdetails> merchantPgdetails = new LinkedList<MerchantPgdetails>();
		List<MerchantServiceDetails> merchantServiceDetails = new LinkedList<MerchantServiceDetails>();
		
		for (IAllMerchantDetailsReport mer : getMerchantDetailsReport) {

			
			MerchantPgdetails pg = null;
			
			if (meremail.equals(mer.getMerchantEMail())) {
				if (m != null) {
					
					if (pgname.equals(mer.getPGName()) ) {
						
						MerchantServiceDetails ms = new MerchantServiceDetails();
						ms.setServiceStatus(mer.getServiceStatus());
						ms.setServiceType(mer.getServiceType());
						merchantServiceDetails.add(ms);
					} else {
						logger.info("PGName :: "+mer.getPGName());
						merchantServiceDetails = new LinkedList<MerchantServiceDetails>();
						MerchantServiceDetails ms = new MerchantServiceDetails();
						ms.setServiceStatus(mer.getServiceStatus());
						ms.setServiceType(mer.getServiceType());
						merchantServiceDetails.add(ms);
						
						pg = new MerchantPgdetails();
						pg.setPgname(mer.getPGName());
						pg.setPgstatus(mer.getPGStatus());
						if (merchantServiceDetails != null) {
							pg.setMerchantservicedetails(merchantServiceDetails);
						}
						pgname = mer.getPGName();
						merchantPgdetails.add(pg);
					}
					m.setMerchantpgdetails(merchantPgdetails);
				}
				
			} else {
				
				m = new AllMerchantDetails();
				merchantPgdetails =  new LinkedList<MerchantPgdetails>();
				merchantServiceDetails = new LinkedList<MerchantServiceDetails>();
				
				m.setMerchantEMail(mer.getMerchantEMail());
				m.setPhoneNumber(mer.getPhoneNumber());
				m.setMerchantId(mer.getMerchantId());
				m.setKycStatus(mer.getkycStatus());
				
				pg = new MerchantPgdetails();
				pg.setPgname(mer.getPGName());
				pg.setPgstatus(mer.getPGStatus());
				merchantPgdetails.add(pg);
				
				MerchantServiceDetails ms = new MerchantServiceDetails();
				ms.setServiceStatus(mer.getServiceStatus());
				ms.setServiceType(mer.getServiceType());
				merchantServiceDetails.add(ms);
				pg.setMerchantservicedetails(merchantServiceDetails);
				
				m.setMerchantpgdetails(merchantPgdetails);
				
				meremail = mer.getMerchantEMail();
				pgname = mer.getPGName();
				allmerchants.add(m);
			}
			
			
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Details Report !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", allmerchants);		
		return sdto;
	}

	public BusinessAssociate createBusinessAssociate(BusinessAssociateCreateRequest businessAssociateCreateRequest) throws ValidationExceptions {
		
		
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(businessAssociateCreateRequest.getMerchantId());
		if(merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		
		BusinessAssociate businessAssociate = businessAssociateRepository.findByMerchantID(businessAssociateCreateRequest.getMerchantId());
		if(businessAssociate != null) {
			throw new ValidationExceptions(MERCHANT_ASSO_WITH_BUSI_ASSO + businessAssociate.getName(), FormValidationExceptionEnums.MERCHANT_ASSO_WITH_BUSI_ASSO);
		}
		
		businessAssociate = new BusinessAssociate();
		businessAssociate.setAddress(businessAssociateCreateRequest.getAddress());
		businessAssociate.setBankAccountNo(businessAssociateCreateRequest.getAccountNumber());
		businessAssociate.setBankName(businessAssociateCreateRequest.getBankName());
		businessAssociate.setEmailId(businessAssociateCreateRequest.getEmailId());
		businessAssociate.setIfscCode(businessAssociateCreateRequest.getIfscCode());
		businessAssociate.setMerchantID(businessAssociateCreateRequest.getMerchantId());
		businessAssociate.setMicrCode(businessAssociateCreateRequest.getMicrCode());
		businessAssociate.setName(businessAssociateCreateRequest.getName());
		businessAssociate.setPhoneNumber(businessAssociateCreateRequest.getPhoneNumber());
		businessAssociate.setUuid(Utility.generateAppId());
		
		businessAssociate = businessAssociateRepository.save(businessAssociate);
		
		return businessAssociate;
	}

	public BusinessAssociateCommissionDetails createBusinessAssociateCommission(String busiAssociateuuid,
			String merchantId, String commType, String serviceType, String serviceSubType, double commAmount, String createdBy) throws ValidationExceptions {
	
		BusinessAssociateCommissionDetails  businessAssociateCommissionDetails = new BusinessAssociateCommissionDetails();
		
		BusinessAssociate businessAssociate = businessAssociateRepository.findByUuidAndMerchantID(busiAssociateuuid , merchantId);
		if(businessAssociate==null) {
			throw new ValidationExceptions(BUSINESS_ASSOCIATED_NOT_FOUND, FormValidationExceptionEnums.BUSINESS_ASSOCIATED_NOT_FOUND);
		}
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if(merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		
		if(serviceType.equalsIgnoreCase(PGServices.CARD.toString())) {
			businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo.findByUuidAndMerchantIDAndPaymentTypeAndPaymentSubTypeAndStatus(
					busiAssociateuuid , merchantId, serviceType , serviceSubType ,UserStatus.ACTIVE.toString() );
		}else {
			businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo.findByUuidAndMerchantIDAndPaymentTypeAndStatus(
					busiAssociateuuid , merchantId, serviceType , UserStatus.ACTIVE.toString() );
		}
		
		if(businessAssociate != null) {
			throw new ValidationExceptions(COMMISSION_WITH_MERCHANT_ALREADY_PRESENT, FormValidationExceptionEnums.COMMISSION_WITH_MERCHANT_ALREADY_PRESENT);
			
		}		
		
		businessAssociateCommissionDetails.setCommissionAmount(commAmount);
		businessAssociateCommissionDetails.setCommissionType(commType);
		businessAssociateCommissionDetails.setCreatedBy(createdBy);
		businessAssociateCommissionDetails.setMerchantID(merchantId);
		businessAssociateCommissionDetails.setPaymentType(serviceType);
		businessAssociateCommissionDetails.setPaymentSubType(serviceSubType);
		businessAssociateCommissionDetails.setUuid(busiAssociateuuid);
		businessAssociateCommissionDetailsRepo.save(businessAssociateCommissionDetails);
		
		return businessAssociateCommissionDetailsRepo.save(businessAssociateCommissionDetails);
	}
	
	public BusinessAssociateCommissionDetails updateBusinessAssociateCommission(String busiAssociateuuid, int commId,
			String status, String uuid) throws ValidationExceptions {
		
		if(!status.equalsIgnoreCase(UserStatus.BLOCKED.toString())) {
			throw new ValidationExceptions(COMMISSION_UPDATE, FormValidationExceptionEnums.COMMISSION_UPDATE);
		}
		
		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = 
				businessAssociateCommissionDetailsRepo.findByUuidAndIdAndStatus(busiAssociateuuid,Long.valueOf(commId), UserStatus.ACTIVE.toString());
		
		if(businessAssociateCommissionDetails == null) {
			throw new ValidationExceptions(COMMISSION_NOT_FOUND, FormValidationExceptionEnums.COMMISSION_NOT_FOUND);
		}
		
		businessAssociateCommissionDetails.setStatus(status.toUpperCase());
		businessAssociateCommissionDetails.setUuid(busiAssociateuuid);
		
		return businessAssociateCommissionDetailsRepo.save(businessAssociateCommissionDetails);
	}

	public SuccessResponseDto  getMerchantCommDetails(UserAdminDetails userAdminDetails) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantBalanceSheetRepository.findByAdminCommDetailsTotal(userAdminDetails.getUuid()));		
		return sdto;
	}

	public Object getAdminMerchantCommissionPendindSettlement(UserAdminDetails userAdminDetails) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Commission Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantBalanceSheetRepository.findByAdminMerchantCommissionPendindSettlement(userAdminDetails.getUuid()));		
		return sdto;
	}

	public SuccessResponseDto updateCommissionDetails(UserAdminDetails userAdminDetails, 
			String orderId, int pgComm, int custComm,
			int businessAssocComm, int merchantSettleAmount) throws ValidationExceptions {
		
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository.findByOrderIdAndSettlementStatus(orderId,UserStatus.PENDING.toString());
		if(merchantBalanceSheet == null) {
			throw new ValidationExceptions(TRANSACTION_NOT_FOUND, FormValidationExceptionEnums.TRANSACTION_NOT_FOUND);
		}
		
		if(merchantBalanceSheet.getAmount()!=(pgComm+custComm+businessAssocComm+merchantSettleAmount) ) {
			throw new ValidationExceptions(AMOUNT_NOT_MATCHED_WITH_EDITED_COMM, FormValidationExceptionEnums.AMOUNT_NOT_MATCHED_WITH_EDITED_COMM);
		}
		
		merchantBalanceSheet.setAskCommission(custComm);
		merchantBalanceSheet.setAssociateCommission(businessAssocComm);
		merchantBalanceSheet.setPgCommission(pgComm);
		merchantBalanceSheet.setSettleAmountToMerchant(merchantSettleAmount);
		merchantBalanceSheet.setProcessedBy(userAdminDetails.getUuid());
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Commission Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantBalanceSheetRepository.save(merchantBalanceSheet));		
		return sdto;
		}

	public SuccessResponseDto refundRequest(UserAdminDetails userAdminDetails, String orderId, String merchantId , String refundTxt) throws ValidationExceptions {
		
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(
				merchantId,
				orderId,
				UserStatus.PENDING.toString()); 
		
		if(merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_INITIATE_FAILED, FormValidationExceptionEnums.REFUND_INITIATE_FAILED);
		}
		
		RefundDetails refundDetail = refundDetailsRepository.getAllRefundByMerchantOrderId(orderId);
		if (refundDetail != null) {
			throw new ValidationExceptions(REFUND_DETAILS_EXIST, FormValidationExceptionEnums.REFUND_DETAILS_EXIST);
		}
		
		
		RefundDetails refundDetails = new RefundDetails();
		refundDetails.setAmount(String.valueOf(merchantBalanceSheet.getAmount()));
		refundDetails.setInitiatedBy(userAdminDetails.getUuid());
		refundDetails.setMerchantId(merchantId);
		refundDetails.setMerchantOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setPaymentCode(merchantBalanceSheet.getPaymentCode());
		refundDetails.setPaymentMode(merchantBalanceSheet.getPaymentMode());
		refundDetails.setPaymentOption(merchantBalanceSheet.getTrType());
		refundDetails.setPgOrderId(merchantBalanceSheet.getPgOrderId());
		refundDetails.setPgStatus(merchantBalanceSheet.getPgStatus());		
		refundDetails.setRefOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setStatus(UserStatus.INITIATED.toString());
		refundDetails.setVpaUpi(merchantBalanceSheet.getVpaUPI());
		refundDetails.setUserId(merchantBalanceSheet.getUserId());	
		refundDetails.setRefundMsg(refundTxt);
		
		merchantBalanceSheet.setSettlementStatus(UserStatus.INITIATED.toString());
		merchantBalanceSheet.setPgStatus("REFUNDED");
		merchantBalanceSheetRepository.save(merchantBalanceSheet);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Refund Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundDetailsRepository.save(refundDetails));		
		return sdto;
	}

	public SuccessResponseDto refundRequestUpdate(UserAdminDetails userAdminDetails, String orderId, String merchantId, String status) throws ValidationExceptions {
		
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(
				merchantId,
				orderId,
				UserStatus.INITIATED.toString()); 
		if(merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_UPDATE_FAILED, FormValidationExceptionEnums.REFUND_UPDATE_FAILED);
		}
		
		RefundDetails refundDetails = refundDetailsRepository.findByMerchantIdAndMerchantOrderIdAndStatus(merchantId,orderId,UserStatus.INITIATED.toString());
		if(refundDetails == null) {
			throw new ValidationExceptions(REFUND_UPDATE_FAILED, FormValidationExceptionEnums.REFUND_UPDATE_FAILED);
		}
		
		merchantBalanceSheet.setSettlementStatus(UserStatus.CLOSED.toString());
		merchantBalanceSheetRepository.save(merchantBalanceSheet);
		
		refundDetails.setStatus(status);
		refundDetails.setUpdatedBy(userAdminDetails.getUuid());
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Refund Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundDetailsRepository.save(refundDetails));		
		return sdto;
	}

	public MerchantCreateResponse merchantSignUp(String merchantCreate)
			throws ValidationExceptions, IllegalAccessException, NoSuchAlgorithmException {

		MerchantCreateRequest merchantCreateRequest = new MerchantCreateRequest();
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantsDetails = new MerchantDetails();

		try {

			merchantCreateRequest = mapper.readValue(merchantCreate, MerchantCreateRequest.class);

		} catch (Exception e) {
			throw new ValidationExceptions(JSON_PARSE_ISSUE_MERCHANT_REQUEST,
					FormValidationExceptionEnums.JSON_PARSE_EXCEPTION);
		}

		if (txnParam(merchantCreateRequest.getMerchantName())==false || txnParam(merchantCreateRequest.getPhoneNumber())==false
				|| txnParam(merchantCreateRequest.getEmailId())==false || txnParam(merchantCreateRequest.getCompanyName())==false || 
				txnParam(merchantCreateRequest.getMerchantType())==false) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE, FormValidationExceptionEnums.PLEASE_FILL_THE_MANDATORY_FIELDS);
		}
		
		if(!Validator.isValidEmail(merchantCreateRequest.getEmailId())) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidPhoneNumber(merchantCreateRequest.getPhoneNumber())) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		merchantsDetails = merchantDetailsRepository.findByMerchantEMail(merchantCreateRequest.getEmailId());
		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM, FormValidationExceptionEnums.MERCHANT_EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM);
		} 
		
		merchantsDetails = merchantDetailsRepository
				.findByPhoneNumber(merchantCreateRequest.getPhoneNumber());
		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM,
					FormValidationExceptionEnums.MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM);
		}
		
		UserAdminDetails adminUserDetails = userAdminDetailsRepository.findByEmailId(merchantCreateRequest.getEmailId());
		if(adminUserDetails !=null) {
			throw new ValidationExceptions(EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM, FormValidationExceptionEnums.EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM);
		}
		
		merchantsDetails = new MerchantDetails();
		String appId = Utility.generateAppId();
		String secrecKey = Encryption.genSecretKey();
		merchantsDetails.setMerchantID(String.valueOf(Utility.getMerchantsID()));
		merchantsDetails.setMerchantEMail(merchantCreateRequest.getEmailId());
		merchantsDetails.setMerchantName(merchantCreateRequest.getMerchantName());
		merchantsDetails.setPhoneNumber(merchantCreateRequest.getPhoneNumber());
		merchantsDetails.setAppID(appId);
		merchantsDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvv(secrecKey));
		merchantsDetails.setUuid(UUID.randomUUID().toString());
		merchantsDetails.setCreatedBy("SignUp");
		merchantsDetails.setSaltKey(UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
		merchantsDetails.setPassword(Encryption.getEncryptedPassword(Encryption.generateRandomPassword(8)));
		merchantsDetails.setTr_mail_flag("Y");
		merchantsDetails.setKycStatus(KycStatus.PENDING.toString());
		merchantsDetails.setUserStatus(UserStatus.PENDING.toString());
		merchantsDetails.setCompanyName(merchantCreateRequest.getCompanyName());
		merchantsDetails.setCompanyType(merchantCreateRequest.getCompanyType());
		merchantsDetails.setSupportEmailId(merchantCreateRequest.getSupportEmailId());
		merchantsDetails.setSupportPhoneNo(merchantCreateRequest.getSupportPhoneNo());
		merchantsDetails.setOtpStatus(OtpStatus.DISABLE.toString());
		merchantsDetails.setMerchantType(merchantCreateRequest.getMerchantType());
		merchantsDetails.setLogoUrl(merchantCreateRequest.getLogoUrl());

		merchantsDetails = merchantDetailsRepository.save(merchantsDetails);
		
		// code for default configuration association for Merchant .
		merchantDefaultPgAssociation(merchantsDetails);
		
		merchantCreateResponse.setAppId(appId);
		merchantCreateResponse.setEmailId(merchantsDetails.getMerchantEMail());
		merchantCreateResponse.setKycStatus(merchantsDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantsDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantsDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantsDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(secrecKey);
		merchantCreateResponse.setCompanyName(merchantsDetails.getCompanyName());
		merchantCreateResponse.setCompanyType(merchantsDetails.getCompanyType());
		merchantCreateResponse.setSupportEmailId(merchantsDetails.getSupportEmailId());
		merchantCreateResponse.setSupportPhoneNo(merchantsDetails.getSupportPhoneNo());
		merchantCreateResponse.setMerchantType(merchantsDetails.getMerchantType());
		
		try {
		    sendMail.sendMailCreateMerchant(merchantsDetails);
			} catch (Exception e) {
				throw new ValidationExceptions(EMAIL_SEND_ERROR, FormValidationExceptionEnums.EMAIL_SEND_ERROR);
			}
		
		return merchantCreateResponse;
	}
	
	
	public MerchantKycDetailsResponse merchantKycDetails(String merchantId, String merchantLegalName, String panCardNumber, String GstId, 
			String webstieUrl, String businessEntityType, String productDescription,  String tanNumber, String regName,  String regAddress,
			String regPinCode,String regNumber, String regEmailAddress, String commName, String commAddress,String commPinCode,String commNumber, 
			String commEmailAddress, MultipartFile cancelledChequeOrAccountProof,MultipartFile certificateOfIncorporation,MultipartFile businessPAN
			, MultipartFile certificateOfGST, MultipartFile directorKYC, MultipartFile aoa, MultipartFile moa, 
			MultipartFile certficateOfNBFC, MultipartFile certficateOfBBPS, MultipartFile certificateOfSEBIOrAMFI)
			throws ValidationExceptions, IllegalAccessException, NoSuchAlgorithmException, IOException {
		
		validateKycDetail(panCardNumber, GstId, webstieUrl, tanNumber, regPinCode, regNumber, regEmailAddress);
			
		MerchantKycDetails merchantKycDetails = new MerchantKycDetails();
		
		MerchantDetails merchantsDetails = new MerchantDetails();
		merchantsDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantsDetails == null) {

			throw new ValidationExceptions(MERCHANT_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		
		MerchantKycDetails merchantKycDetailsCheck = new MerchantKycDetails();
		merchantKycDetailsCheck = merchantKycDetailsRepository
				.findByMerchantID(merchantsDetails.getMerchantID());
		if (merchantKycDetailsCheck != null) {
			throw new ValidationExceptions(MERCHANT_KYC_DETAIL_PRESENT,
					FormValidationExceptionEnums.MERCHANT_KYC_DETAILS_PRESENT);
		}
		
		MerchantKycDetailsResponse res = kycDetailAndDocs(merchantKycDetailsCheck,merchantsDetails.getMerchantID(), merchantLegalName, panCardNumber, GstId, 
		webstieUrl, businessEntityType, productDescription,  tanNumber, regName,  regAddress,regPinCode,regNumber,
		regEmailAddress, commName, commAddress,commPinCode,commNumber, commEmailAddress,cancelledChequeOrAccountProof,certificateOfIncorporation,businessPAN,certificateOfGST,directorKYC,aoa,moa,
		certficateOfNBFC,certficateOfBBPS,certificateOfSEBIOrAMFI);
		
		return res;
	}
	
	public void validateKycDetail(String panCardNumber, String GstId, String webstieUrl, String tanNumber, 
			String regPinCode, String regNumber, String regEmailAddress) throws ValidationExceptions {
		if(!Validator.isValidEmail(regEmailAddress)) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidPhoneNumber(regNumber)) {
			throw new ValidationExceptions(MOBILE_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidPinCode(regPinCode)) {
			throw new ValidationExceptions(PIN_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(txnParam(GstId)) {
		if(!Validator.isValidGstNumber(GstId)) {
			throw new ValidationExceptions(GSTID_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);	
		}}
		
		if(txnParam(panCardNumber)) {
		if(!Validator.isValidPANNumber(panCardNumber)) {
			throw new ValidationExceptions(PAN_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}}
		
		if(txnParam(tanNumber)) {
		if(!Validator.isValidAlphaNumber(tanNumber)) {
			throw new ValidationExceptions(TAN_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);	
		}}
		
		if(!Validator.isValidWebUrl(webstieUrl)) {
			throw new ValidationExceptions(WEBSITE_URL_VALIDATION_FAILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
	}
	
	public SuccessResponseDto updatMerchantKycStatus(String merchantId, String statusUpdate)
			throws ValidationExceptions {

		MerchantKycDetails merchantKycDetails = merchantKycDetailsRepository.findByMerchantID(merchantId);
		if (merchantKycDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		
		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}	
		
		if (statusUpdate.equalsIgnoreCase(KycStatus.YES.toString())) {
			merchantKycDetails.setMerchantKycStatus(KycStatus.YES.toString());
		} else {
			merchantKycDetails.setMerchantKycStatus(KycStatus.NO.toString());
		}

		merchantDetails.setKycStatus(statusUpdate);
		merchantDetailsRepository.save(merchantDetails);
		
		merchantKycDetails.setMerchantKycStatus(statusUpdate);
		merchantKycDetailsRepository.save(merchantKycDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Kyc Status updated!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKycStatus", merchantKycDetails);	
		return sdto;
	}
	
	public MerchantKycDetailsResponse kycDetailAndDocs(MerchantKycDetails merchantKycDetails, String merchantId, String merchantLegalName, String panCardNumber, String GstId, 
			String webstieUrl, String businessEntityType, String productDescription,  String tanNumber, String regName,  String regAddress,
			String regPinCode,String regNumber, String regEmailAddress, String commName, String commAddress,String commPinCode,String commNumber, 
			String commEmailAddress, MultipartFile cancelledChequeOrAccountProof,MultipartFile certificateOfIncorporation,MultipartFile businessPAN
			, MultipartFile certificateOfGST, MultipartFile directorKYC, MultipartFile aoa, MultipartFile moa, 
			MultipartFile certficateOfNBFC, MultipartFile certficateOfBBPS, MultipartFile certificateOfSEBIOrAMFI) throws EncryptedDocumentException, NoSuchAlgorithmException, ValidationExceptions, IOException {
		
		if(merchantKycDetails==null) {
			 merchantKycDetails = new MerchantKycDetails();
		}
		
		validateKycDetail(panCardNumber, GstId, webstieUrl, tanNumber, regPinCode, regNumber, regEmailAddress);
		
        MerchantKycDetailsResponse merchantKycDetailsResponse = new MerchantKycDetailsResponse();
		
		merchantKycDetails.setMerchantID(merchantId);
		merchantKycDetails.setMerchantLegalName(merchantLegalName);
		merchantKycDetails.setPanCardNumber(panCardNumber);
		merchantKycDetails.setGstId(GstId);
		merchantKycDetails.setWebstieUrl(webstieUrl);
		merchantKycDetails.setBusinessEntityType(businessEntityType);
		merchantKycDetails.setProductDescription(productDescription);
		merchantKycDetails.setTanNumber(tanNumber);
		merchantKycDetails.setMerchantKycStatus(KycStatus.PENDING.toString());
		merchantKycDetails.setRegName(regName);
		merchantKycDetails.setRegAddress(regAddress);
		merchantKycDetails.setRegEmailAddress(regEmailAddress);
		merchantKycDetails.setRegNumber(regNumber);
		merchantKycDetails.setRegPinCode(regPinCode);
		merchantKycDetails.setCommName(commName);
		merchantKycDetails.setCommAddress(commAddress);
		merchantKycDetails.setCommEmailAddress(commEmailAddress);
		merchantKycDetails.setCommNumber(commNumber);
		merchantKycDetails.setCommPinCode(commPinCode);
		merchantKycDetails.setBusinessEntityType(businessEntityType);
		
		if(cancelledChequeOrAccountProof!=null) {
		UploadFileResponse uploadedCancelledChequeOrAccountProof = kycFileUpload(cancelledChequeOrAccountProof, merchantId);
		merchantKycDetails.setCancelledChequeOrAccountProof(uploadedCancelledChequeOrAccountProof.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setCancelledChequeOrAccountProof(null);
		}
		if(certficateOfBBPS!=null) {
		UploadFileResponse uploadedCertficateOfBBPS = kycFileUpload(certficateOfBBPS, merchantId);
		merchantKycDetails.setCertficateOfBBPS(uploadedCertficateOfBBPS.getFileDownloadLink());
		}
        else {
        	merchantKycDetails.setCertficateOfBBPS(null);
		}
		if(certficateOfNBFC!=null) {
		UploadFileResponse uploadedCertficateOfNBFC = kycFileUpload(certficateOfNBFC, merchantId);
		merchantKycDetails.setCertficateOfNBFC(uploadedCertficateOfNBFC.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setCertficateOfNBFC(null);	
		}
		if(certificateOfGST!=null) {
		UploadFileResponse uploadedCertificateOfGST = kycFileUpload(certificateOfGST, merchantId);
		merchantKycDetails.setCertificateOfGST(uploadedCertificateOfGST.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setCertificateOfGST(null);
		}
		if(certificateOfIncorporation!=null) {
		UploadFileResponse uploadedCertificateOfIncorporation = kycFileUpload(certificateOfIncorporation,merchantId);
		merchantKycDetails.setCertificateOfIncorporation(uploadedCertificateOfIncorporation.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setCertificateOfIncorporation(null);
		}
		if(certificateOfSEBIOrAMFI!=null) {
		UploadFileResponse uploadedCertificateOfSEBIOrAMFI = kycFileUpload(certificateOfSEBIOrAMFI, merchantId);
		merchantKycDetails.setCertificateOfSEBIOrAMFI(uploadedCertificateOfSEBIOrAMFI.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setCertificateOfSEBIOrAMFI(null);
		}
		if(aoa!=null) {
		UploadFileResponse uploadedAoa = kycFileUpload(aoa, merchantId);
		merchantKycDetails.setAoa(uploadedAoa.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setAoa(null);
		}
		if(moa!=null) {
		UploadFileResponse uploadedMoa = kycFileUpload(moa, merchantId);
		merchantKycDetails.setMoa(uploadedMoa.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setMoa(null);
		}
		if(businessPAN!=null) {
		UploadFileResponse uploadedBusinessPAN = kycFileUpload(businessPAN, merchantId);
		merchantKycDetails.setBusinessPAN(uploadedBusinessPAN.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setBusinessPAN(null);
		}
		if(directorKYC!=null) {
		UploadFileResponse uploadedDirectorKYC = kycFileUpload(directorKYC, merchantId);
		merchantKycDetails.setDirectorKYC(uploadedDirectorKYC.getFileDownloadLink());
		}
		else {
			merchantKycDetails.setDirectorKYC(null);
		}
		
		merchantKycDetails = merchantKycDetailsRepository.save(merchantKycDetails);

		merchantKycDetailsResponse.setMerchantLegalName(merchantKycDetails.getMerchantLegalName());
		merchantKycDetailsResponse.setPanCardNumber(merchantKycDetails.getPanCardNumber());
		merchantKycDetailsResponse.setGstId(merchantKycDetails.getGstId());
		merchantKycDetailsResponse.setWebstieUrl(merchantKycDetails.getWebstieUrl());
		merchantKycDetailsResponse.setBusinessEntityType(merchantKycDetails.getBusinessEntityType());
		merchantKycDetailsResponse.setProductDescription(merchantKycDetails.getProductDescription());
		merchantKycDetailsResponse.setTanNumber(merchantKycDetails.getTanNumber());
		merchantKycDetailsResponse.setRegName(merchantKycDetails.getRegName());
		merchantKycDetailsResponse.setRegAddress(merchantKycDetails.getRegAddress());
		merchantKycDetailsResponse.setRegEmailAddress(merchantKycDetails.getRegEmailAddress());
		merchantKycDetailsResponse.setRegNumber(merchantKycDetails.getRegNumber());
		merchantKycDetailsResponse.setRegPinCode(merchantKycDetails.getRegPinCode());
		merchantKycDetailsResponse.setCommName(merchantKycDetails.getCommName());
		merchantKycDetailsResponse.setCommAddress(merchantKycDetails.getCommAddress());
		merchantKycDetailsResponse.setCommEmailAddress(merchantKycDetails.getCommEmailAddress());
		merchantKycDetailsResponse.setCommNumber(merchantKycDetails.getCommNumber());
		merchantKycDetailsResponse.setCommPinCode(merchantKycDetails.getCommPinCode());
		merchantKycDetailsResponse.setBusinessEntityType(merchantKycDetails.getBusinessEntityType());
		merchantKycDetailsResponse.setCancelledChequeOrAccountProof(merchantKycDetails.getCancelledChequeOrAccountProof());
		merchantKycDetailsResponse.setCertficateOfBBPS(merchantKycDetails.getCertficateOfBBPS());
		merchantKycDetailsResponse.setCertficateOfNBFC(merchantKycDetails.getCertficateOfNBFC());
		merchantKycDetailsResponse.setCertificateOfGST(merchantKycDetails.getCertificateOfGST());
		merchantKycDetailsResponse.setCertificateOfIncorporation(merchantKycDetails.getCertificateOfIncorporation());
		merchantKycDetailsResponse.setCertificateOfSEBIOrAMFI(merchantKycDetails.getCertificateOfSEBIOrAMFI());
		merchantKycDetailsResponse.setAoa(merchantKycDetails.getAoa());
		merchantKycDetailsResponse.setMoa(merchantKycDetails.getMoa());
		merchantKycDetailsResponse.setBusinessPAN(merchantKycDetails.getBusinessPAN());
		merchantKycDetailsResponse.setDirectorKYC(merchantKycDetails.getDirectorKYC());

		return merchantKycDetailsResponse;
	}
	
	public SuccessResponseDto updateKycDetails(String merchantId, String merchantLegalName, String panCardNumber, String GstId, 
			String webstieUrl, String businessEntityType, String productDescription,  String tanNumber, String regName,  String regAddress,
			String regPinCode,String regNumber, String regEmailAddress, String commName, String commAddress,String commPinCode,String commNumber, 
			String commEmailAddress, MultipartFile cancelledChequeOrAccountProof,MultipartFile certificateOfIncorporation,MultipartFile businessPAN
			, MultipartFile certificateOfGST, MultipartFile directorKYC, MultipartFile aoa, MultipartFile moa, 
			MultipartFile certficateOfNBFC, MultipartFile certficateOfBBPS, MultipartFile certificateOfSEBIOrAMFI)
 			throws ValidationExceptions, EncryptedDocumentException, NoSuchAlgorithmException, IOException {
		
		validateKycDetail(panCardNumber, GstId, webstieUrl, tanNumber, regPinCode, regNumber, regEmailAddress);
    	 
        MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
  		if (merchantDetails == null) {
  			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
  		}
  		
    	MerchantKycDetails merchantKycDetails = merchantKycDetailsRepository.findByMerchantID(merchantId);
 		if (merchantKycDetails == null) {
 			throw new ValidationExceptions(MERCHANT_KYC_DETAIL_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_KYC_DETAIL_NOT_FOUND);
 		}
 		
 		if(merchantKycDetails.getMerchantKycStatus().equalsIgnoreCase((KycStatus.APPROVED.toString()))){
 			throw new ValidationExceptions(MERCHANT_KYC_HAS_BEEN_APPROVED, FormValidationExceptionEnums.KYC_STATUS);
 		}
 		
 		MerchantKycDetailsResponse res = kycDetailAndDocs(merchantKycDetails,merchantDetails.getMerchantID(), merchantLegalName, panCardNumber, GstId, 
 				webstieUrl, businessEntityType, productDescription,  tanNumber, regName,  regAddress,regPinCode,regNumber,
 				regEmailAddress, commName, commAddress,commPinCode,commNumber, commEmailAddress,cancelledChequeOrAccountProof,certificateOfIncorporation,businessPAN,certificateOfGST,directorKYC,aoa,moa,
 				certficateOfNBFC,certficateOfBBPS,certificateOfSEBIOrAMFI);
 		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Kyc updated Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKycStatus", res);	
		return sdto;
 	}
	
	public SuccessResponseDto getMerchantKyc(String merchantId)
 			throws ValidationExceptions {
	
		MerchantKycDetails merchantKycDetails = merchantKycDetailsRepository.findByMerchantID(merchantId);
 		if (merchantKycDetails == null) {
 			throw new ValidationExceptions(MERCHANT_KYC_DETAIL_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_KYC_DETAIL_NOT_FOUND);
 		}
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Kyc Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKycDetails", merchantKycDetails);	
		return sdto;
	}
	
	public UploadFileResponse kycFileUpload(MultipartFile file, String merchantid)
			throws ValidationExceptions, NoSuchAlgorithmException, EncryptedDocumentException, IOException {
		logger.info("kycFileUpload Service");
		if (file.getSize() > 104857600) {
			throw new ValidationExceptions(INVALID_FILE_SIZE, FormValidationExceptionEnums.INVALID_FILE_SIZE);
		}

		String fileName = fileStorageService.storeFile(file,
				Utility.randomStringGenerator(10) + "_" + merchantid + "_" + file.getOriginalFilename());

		logger.info(fileName);
		String[] fl = fileName.split("\\|");
		FileLoading fileLoading = new FileLoading();
		fileLoading.setFileName(fl[0]);
		fileLoading.setFileHash(fl[1]);
		fileLoading.setFileSize(String.valueOf(file.getSize()));
		fileLoading.setFileStatus("UPLOADED");
		fileLoading.setFileType(file.getContentType());
		fileLoading.setPurpose("MERCHANT_KYC_DOC");
		fileLoading.setMerchantid(merchantid);
		fileUploadRepo.save(fileLoading);
		logger.info("Sent for Processing");
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/downloadFile/")
				.path(fl[0]).toUriString();
		
		return  new UploadFileResponse(fl[0], fileDownloadUri, "UPLOAD SUCCESS");

	}
	
	public boolean validateDateFormat(String dateToValdate) throws ValidationExceptions {
   	 
   	 if(dateToValdate.isBlank() || dateToValdate.isEmpty()) {
   		 throw new ValidationExceptions(DATE_PARAMETER_IS_MANDATORY, FormValidationExceptionEnums.DATE_PARAMETER_IS_MANDATORY);
   	 }

	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    formatter.setLenient(false);
	    Date parsedDate = null;
	    try {
	        parsedDate = formatter.parse(dateToValdate);
	       // System.out.println("++validated DATE TIME ++"+formatter.format(parsedDate));

	    } catch (ParseException e) {
	        return false;
	    }
	    return true;
	}
	
	 public void dateWiseValidation(String start_date, String end_date) throws ValidationExceptions {
	    	boolean checkStartFormat = validateDateFormat(start_date);
			boolean checkEndFormat = validateDateFormat(end_date);
			
			if(checkStartFormat == false || checkEndFormat == false || start_date.trim().contains(" ") || end_date.trim().contains(" ")|| start_date.matches(".*[a-zA-Z]+.*") || end_date.matches(".*[a-zA-Z]+.*")) {
				throw new ValidationExceptions(DATE_FORMAT, FormValidationExceptionEnums.DATE_FORMAT);
			}
			
	    }
	 
  public SuccessResponseDto pgTransactionReport(String merchantId, String dateFrom, String dateTo) throws ValidationExceptions, ParseException {
		 
	  if(dateFrom != null && dateTo !=null) {
  		dateWiseValidation(dateFrom,dateTo);
  	 }
	  
	   SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		List<ICustomerPgTransaction> res = transactionDetailsRepository.customerPgTransaction(merchantId, dateFrom, dateTo);
		if(res.isEmpty()) {
			throw new ValidationExceptions(MERCHNT_DETAILS_NOT_MAPPED, FormValidationExceptionEnums.INFORMATION_NOT_FOUND);
		}
		
		sdto.getExtraData().put("customerpgTransaction",res);	
		return sdto;
	}
	
}
