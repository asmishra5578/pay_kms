package com.asktech.pgateway.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.error.ErrorResponseDto;
import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.login.LoginResponseDto;
import com.asktech.pgateway.dto.login.LogoutRequestDto;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.security.JwtGenerator;
import com.asktech.pgateway.service.PGGatewayAdminService;
import com.asktech.pgateway.service.UserLoginService;
import com.asktech.pgateway.util.GoogleCaptchaAssement;
import com.asktech.pgateway.util.JwtUserValidator;
import com.asktech.pgateway.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import jdk.jshell.spi.ExecutionControl.UserException;

@RestController
public class PGUserLoginController implements CashFreeFields, ErrorValues {

	@Autowired
	JwtUserValidator jwtValidator;

	@Autowired
	UserLoginService userLoginService;

	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;

	@Autowired
	private JwtGenerator jwtGenerator;

	@Autowired
	GoogleCaptchaAssement googleCaptchaAssement;

	public PGUserLoginController(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}

	
	
	static Logger logger = LoggerFactory.getLogger(PGUserLoginController.class);

	@PostMapping(value = "/user/login")
	@ApiOperation(value = "User login and send OTP at registered mobile number. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {
		ErrorResponseDto erdto = new ErrorResponseDto();
		
	   if(!googleCaptchaAssement.verifyToken(dto.getCaptchaToken())) {
		 throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}

		if (StringUtils.isEmpty(dto.getUserNameOrEmail())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}		
		
		Object login = userLoginService.getUserLogin(dto);
				
		return ResponseEntity.ok().body(login);
		
//        Object loginOtp = userLoginService.getUserLogin(dto);
//		
//		SuccessResponseDto sdto = new SuccessResponseDto();
//		sdto.getMsg().add("Login OTP has been send to register Mobile Number and Email Id . Valid for 2 Mins");
//		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
//		sdto.getExtraData().put("merchantSignUp", loginOtp);		
//		return ResponseEntity.ok().body(sdto);
		
	}
	
	@PostMapping("/user/merchantSignUp")
	@ApiOperation(value = "Create Merchant.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSignUp(@RequestBody String strCreateMerchant)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			UserException {
		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.merchantSignUp(strCreateMerchant);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Registered Successfully ! Login Credentials has been sent to the registered Email Id .");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantSignUp", merchantCreateResponse);		
		return ResponseEntity.ok().body(sdto);
		
	}

	@PutMapping("user/verify/otp")
	@ApiOperation(value = "Admin or Retailer login and verify OTP at login time.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminVerifyOtp(@RequestParam("otp") int otp, @RequestBody LoginRequestDto dto,
			@RequestHeader("User-Agent") String UserAgent, @RequestHeader("OTPSessionId") String otpSessionId)
			throws UserException, NoSuchAlgorithmException, ValidationExceptions {
		LoginResponseDto loginResponseDto;
		ErrorResponseDto erdto = new ErrorResponseDto();
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (StringUtils.isEmpty(otp)) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		if (StringUtils.isEmpty(dto.getIpAddress())) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);

		}
		dto.setUserAgent(UserAgent);
		loginResponseDto = userLoginService.verifyOtp(otp, dto, otpSessionId);
		String jwt = (jwtGenerator.generate(dto));
		loginResponseDto.setJwtToken(jwt);
		sdto.getMsg().add("verification success");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("LoginData", loginResponseDto);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "user/resend/otp")
	@ApiOperation(value = "Admin or Retailer resend OTP if OTP is not received.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminResenOtp(@RequestParam("email") String emailorphone)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		if (StringUtils.isEmpty(emailorphone)) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}	
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Login OTP has been send to register Mobile Number and Email Id .");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("resend", userLoginService.userResenOtp(emailorphone));
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "api/user/logout")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> userLogout(@RequestBody LogoutRequestDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		ErrorResponseDto erdto = new ErrorResponseDto();
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (StringUtils.isEmpty(dto.getSessionToken()) || StringUtils.isEmpty(dto.getUuid())) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		 userLoginService.userLogout(dto);
		
		sdto.getMsg().add("User Has been Successfully Logged Out from Application !");
		sdto.setSuccessCode(SuccessCode.USER_LOGGED_OUT_SUCCESSFULLY);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "user/initialPasswordChange")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> initialPasswordChange(@RequestParam("userNameOrEmailId") String userNameOrEmailId,
			@RequestParam("password") String password)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		ErrorResponseDto erdto = new ErrorResponseDto();
		SuccessResponseDto sdto = new SuccessResponseDto();

		if (userNameOrEmailId == null) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		 userLoginService.initiatlPasswordChange(userNameOrEmailId, password);
		
		sdto.getMsg().add("Password change successsfully done ! ");
		sdto.setSuccessCode(SuccessCode.RESET_PASSWORD_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/forgetPasswordGenerateOTP")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPassword(@RequestParam("userEmail") String userNameOrEmailId,
			@RequestParam("captchaToken") String captchaToken) throws ValidationExceptions, IOException {

		if (!googleCaptchaAssement.verifyToken(captchaToken)) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		userLoginService.forgotPassword(userNameOrEmailId);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Change OTP has been send to your mail id , The OTP will valid for 10 Mins, Please change the password within timeline.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}
	
	@PostMapping(value = "/forgetPasswordResendOTP")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPasswordResendOtp(@RequestParam("userEmail") String userNameOrEmailId,
			@RequestParam("captchaToken") String captchaToken) throws ValidationExceptions, IOException {

		if (!googleCaptchaAssement.verifyToken(captchaToken)) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		userLoginService.forgotPassword(userNameOrEmailId);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Change OTP has been send to your mail id , The OTP will valid for 10 Mins, Please change the password within timeline.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/forgetPasswordChangeWithOTP")
	@ApiOperation(value = "Admin password change to secure PG gateway ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPasswordChange(@RequestParam("userEmail") String userNameOrEmailId,
			@RequestParam("password") String password, @RequestParam("mailOtp") String otp)
			throws ValidationExceptions {

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}
		
		userLoginService.forgotPasswordChange(userNameOrEmailId,password,otp);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Change has been processed, Please login with new Credentials.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);	
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/merchantPWD/getPassword")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getGetAdminPwd(@RequestParam("emailId") String emailId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantEMail(emailId);
		if(merchantDetails==null) {
				throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		merchantDetails.setPassword(Encryption.getDecryptedPassword(merchantDetails.getPassword()));
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Detail !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("getPassword", merchantDetails);	
		return ResponseEntity.ok().body(sdto);
	}

}
