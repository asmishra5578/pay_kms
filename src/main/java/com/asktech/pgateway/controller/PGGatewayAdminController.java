package com.asktech.pgateway.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.admin.BusinessAssociateCreateRequest;
import com.asktech.pgateway.dto.admin.CreateAdminUserRequest;
import com.asktech.pgateway.dto.admin.CreatePGDetailsRequest;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.admin.ProcessSettlementRequest;
import com.asktech.pgateway.dto.error.ErrorResponseDto;
import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.login.LoginResponseDto;
import com.asktech.pgateway.dto.login.LogoutRequestDto;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.PGServices;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.enums.UserTypes;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.BusinessAssociate;
import com.asktech.pgateway.model.BusinessAssociateCommissionDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.PGServiceDetails;
import com.asktech.pgateway.model.UserAdminDetails;
import com.asktech.pgateway.repository.UserAdminDetailsRepository;
import com.asktech.pgateway.schedular.SwitchPgServiceDynamically;
import com.asktech.pgateway.schedular.ThresholdUpdateService;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.security.JwtGenerator;
import com.asktech.pgateway.service.PGGatewayAdminService;
import com.asktech.pgateway.util.CommissionCalculator;
import com.asktech.pgateway.util.JwtUserValidator;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class PGGatewayAdminController implements ErrorValues {

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);

	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	@Autowired
	CommissionCalculator commissionCalculator;
	@Autowired
	SwitchPgServiceDynamically switchPgServiceDynamically;
	@Autowired
	ThresholdUpdateService thresholdUpdateService;

	@Autowired
	private JwtGenerator jwtGenerator;
	@Autowired
	private JwtUserValidator jwtUserValidator;

	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;

	@PostMapping(value = "/admin/login")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> loginAdmin(@RequestBody LoginRequestDto dto) throws UserException,
			NoSuchAlgorithmException, IOException, ValidationExceptions, jdk.jshell.spi.ExecutionControl.UserException {
		ErrorResponseDto erdto = new ErrorResponseDto();

		if (StringUtils.isEmpty(dto.getUserNameOrEmail())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		LoginResponseDto loginResponseDto = pgGatewayAdminService.getAdminLogin(dto);

		if (loginResponseDto.getUserType().equalsIgnoreCase(UserTypes.ADMIN.toString())) {
			String jwt = (jwtGenerator.generate(dto));
			loginResponseDto.setJwtToken(jwt);
		} else {
			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.USER_ROLE_ISSUE, null,
					"User does not have Admin Previledges.", false, 100);
			return ResponseEntity.ok().body(erdto);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Logged In Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("loginData", loginResponseDto);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/admin/forgetPasswordGenerateOTP")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPassword(@RequestParam("userEmail") String userNameOrEmailId)
			throws ValidationExceptions {

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		pgGatewayAdminService.forgotPassword(userNameOrEmailId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Change OTP has been send to your mail id , The OTP will valid for 10 Mins, Please change the password within timeline.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);	
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/admin/forgetPasswordResendOTP")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> forgetPasswordResendOtp(@RequestParam("userEmail") String userNameOrEmailId)
			throws ValidationExceptions {

		ErrorResponseDto erdto = new ErrorResponseDto();
		if (userNameOrEmailId.length() == 0) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		pgGatewayAdminService.forgotPassword(userNameOrEmailId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Change OTP has been send to your mail id , The OTP will valid for 10 Mins, Please change the password within timeline.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);	
		return ResponseEntity.ok().body(sdto);
		}

	@PostMapping(value = "/admin/forgetPasswordChangeWithOTP")
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

		pgGatewayAdminService.forgotPasswordChange(userNameOrEmailId, password, otp);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Password Change has been processed, Please login with new Credentials.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);	
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/super/login")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> loginSuper(@RequestBody LoginRequestDto dto) throws UserException,
			NoSuchAlgorithmException, IOException, ValidationExceptions, jdk.jshell.spi.ExecutionControl.UserException {
		ErrorResponseDto erdto = new ErrorResponseDto();

		if (StringUtils.isEmpty(dto.getUserNameOrEmail())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		LoginResponseDto loginResponseDto = pgGatewayAdminService.getAdminLogin(dto);
		if (loginResponseDto.getUserType().equalsIgnoreCase(UserTypes.SUPER.toString())) {

			String jwt = (jwtGenerator.generate(dto));
			loginResponseDto.setJwtToken(jwt);
		} else {
			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.USER_ROLE_ISSUE, null,
					"User is not have Super Admin Role", false, 100);
			return ResponseEntity.ok().body(erdto);
		}

		return ResponseEntity.ok().body(loginResponseDto);
	}

	@PutMapping(value = "api/admin/passwordChange")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> initialPasswordChange(@RequestParam("userName") String userNameOrEmailId,
			@RequestParam("password") String password)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		SuccessResponseDto sdto = new SuccessResponseDto();
		if (userNameOrEmailId == null) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		pgGatewayAdminService.passwordChange(userNameOrEmailId, password);

		sdto.getMsg().add("Password change successsfully done ! ");
		sdto.setSuccessCode(SuccessCode.RESET_PASSWORD_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@SuppressWarnings("deprecation")
	@PostMapping(value = "/api/adminCreate")
	@ApiOperation(value = "Admin login to secure PG gateway ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createAdminUser(@RequestBody CreateAdminUserRequest createAdminUserRequest,
			@RequestParam String uuid) throws UserException, NoSuchAlgorithmException, IOException,
			ValidationExceptions, com.asktech.pgateway.exception.UserException, JWTException, SessionExpiredException {
		ErrorResponseDto erdto = new ErrorResponseDto();

		logger.info("Input DTO :: " + Utility.convertDTO2JsonString(createAdminUserRequest));

		if (StringUtils.isEmpty(createAdminUserRequest.getEmailId())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		if (StringUtils.isEmpty(createAdminUserRequest.getPhoneNumber())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		if (StringUtils.isEmpty(createAdminUserRequest.getUserName())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null,
					"Field can't be empty", false, 100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (userAdminDetails.getUserType().equalsIgnoreCase(UserTypes.SUPER.toString())) {

			userAdminDetails = pgGatewayAdminService.createAdminUser(createAdminUserRequest);
		} else {
			throw new ValidationExceptions(SUPER_USER_ROLE, FormValidationExceptionEnums.USER_ROLE_ISSUE);
		}
		
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Logged In Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("adminDetail", userAdminDetails);		
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping("api/createMerchant")
	@ApiOperation(value = "Create Merchant by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchant(@RequestBody String strCreateMerchant, @RequestParam String uuid)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			com.asktech.pgateway.exception.UserException, JWTException, SessionExpiredException {

		jwtUserValidator.validatebyJwtAdminDetails(uuid);

		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.createMerchant(strCreateMerchant, uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Registered Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantCreateResponse);		
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping("api/createPGDeatils")
	@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createPGDetails(@RequestBody CreatePGDetailsRequest createPGDetailsRequest,
			@RequestParam String uuid) throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			com.asktech.pgateway.exception.UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		
		return ResponseEntity.ok().body(pgGatewayAdminService.createPg(createPGDetailsRequest,userAdminDetails.getUserId()));
	}

	@PostMapping("api/createPGServices")
	@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> createPGServices(
			@RequestParam String uuid, @RequestParam("pgUuid") String pgUuid,
			@RequestParam("pgServices") String pgServices,
			@RequestParam("defaultTag") String defaultTag,
			@RequestParam("thresoldMonth") long thresoldMonth,
			@RequestParam("thresoldDay") long thresoldDay,
			@RequestParam("thresoldWeek") long thresoldWeek,
			@RequestParam("thresold3Month") long thresold3Month,
			@RequestParam("thresold6Month") long thresold6Month,
			@RequestParam("thresoldYear") long thresoldYear) throws IllegalAccessException, NoSuchAlgorithmException,
			ValidationExceptions, com.asktech.pgateway.exception.UserException, JWTException, SessionExpiredException {
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		PGServiceDetails pgServiceDetails = pgGatewayAdminService.createPgServices(pgUuid, pgServices,
				userAdminDetails.getUserId(), defaultTag ,thresoldMonth ,thresoldDay, thresoldWeek, thresold3Month,thresold6Month,thresoldYear);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("PG Created Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetail", pgServiceDetails);		
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("api/getpgDetails")
	@ApiOperation(value = "Create PG by Admin User.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getPGDetails(@RequestParam String uuid)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			com.asktech.pgateway.exception.UserException, JWTException, SessionExpiredException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		Object listPGConfigurationDetails = pgGatewayAdminService.getPgDetails();

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("PG Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("pgDetails", listPGConfigurationDetails);		
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping("/api/createMerchantPGDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchantPGDetals(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("pgUuid") String pgUuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		MerchantPGDetails merchantPGDetails = pgGatewayAdminService.associatePGDetails(merchantId, pgUuid, uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant PG Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantPGDetails);		
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping("/api/createMerchantPGServices")
	@ApiOperation(value = "Post Merchant Services from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchantPGSerices(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, 
			@RequestParam("pgUuid") String pgUuid,
			@RequestParam("merchantService") String merchantService)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(PGServices.class, merchantService)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		MerchantPGServices merchantPGServices = pgGatewayAdminService.associatePGServices(pgUuid,
				merchantService, uuid, merchantId);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant PG Service Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", merchantPGServices);		
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "api/admin/logout")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> userLogout(@RequestBody LogoutRequestDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		SuccessResponseDto sdto = new SuccessResponseDto();
		if (StringUtils.isEmpty(dto.getUuid()) || StringUtils.isEmpty(dto.getSessionToken())) {
			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(dto.getUuid());
		pgGatewayAdminService.userLogout(dto, userAdminDetails);
		sdto.getMsg().add("user logged out successfully");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);

		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "api/admin/merchantStatus")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantStatus(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusAdmin(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantList")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantList(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusList(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionLastDay")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionYesterday(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionLastDay(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionToday")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionToday(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionToday(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionCurrMonth")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionCurrMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionCurrMonth(userAdminDetails));
	}

	@GetMapping(value = "api/admin/merchantTransactionLastMonth")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionLastMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.merchantStatusTransactionLastMonth(userAdminDetails));
	}

	@PutMapping(value = "api/admin/updatePGDetails")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminUpdatePGDetails(@RequestParam("uuid") String uuid,
			@RequestParam("pgUuid") String pgUuid, @RequestParam("status") String statusUpdate)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updatePGDetails(userAdminDetails.getUuid(), pgUuid, statusUpdate));
	}

	@PutMapping(value = "api/admin/updatePGService")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminUpdatePGServices(@RequestParam("uuid") String uuid,
			@RequestParam("pgUuid") String pgUuid, @RequestParam("status") String statusUpdate,
			@RequestParam("service") String service)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, JsonProcessingException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		if (!Validator.containsEnum(PGServices.class, service)) {
			throw new ValidationExceptions(PG_SERVICE_NOT_FOUND, FormValidationExceptionEnums.PG_SERVICE_NOT_FOUND);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updatePGService(userAdminDetails.getUuid(), pgUuid, statusUpdate, service));
	}

	@PutMapping(value = "api/admin/updateMerchantStatus")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantStatusUpdate(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestParam("status") String statusUpdate)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updatMerchantStatus(userAdminDetails.getUuid(), merchantId, statusUpdate));
	}

	@PutMapping(value = "api/admin/updateMerchantPGDetails")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantPGDetailsUpdate(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, 
			@RequestParam("pgUuid") String pgUuid,
			@RequestParam("status") String statusUpdate)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updateMerchantPGDetailsStatus(merchantId, pgUuid, statusUpdate));
	}

	@PutMapping(value = "api/admin/updateMerchantPGServices")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantPGservicesUpdate(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("pgUuid") String pgUuid,
			@RequestParam("status") String statusUpdate,
			@RequestParam("service") String service)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.containsEnum(UserStatus.class, statusUpdate)) {
			throw new ValidationExceptions(USER_STATUS, FormValidationExceptionEnums.USER_STATUS);
		}

		if (!Validator.containsEnum(PGServices.class, service)) {
			throw new ValidationExceptions(PG_SERVICE_NOT_FOUND, FormValidationExceptionEnums.PG_SERVICE_NOT_FOUND);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updateMerchantPGServiceStatus(merchantId, pgUuid, statusUpdate, service , uuid));
	}

	@GetMapping(value = "/api/admin/transactionDetailsDateFilter")
	@ApiOperation(value = "Admin User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminTransactionDetailsDateWise(@RequestParam("uuid") String uuid,
			@RequestParam("merchantId") String merchantId, @RequestParam("dateFrom") String dateFrom,
			@RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		if (!Validator.isValidateDateFormat(dateFrom) || dateFrom == null) {
			logger.info("Date validation error 1 ... " + dateTo);
			throw new ValidationExceptions(DATE_FORMAT_VALIDATION, FormValidationExceptionEnums.DATE_FORMAT_VALIDATION);
		}
		if (dateTo.length() != 0) {
			if (!Validator.isValidateDateFormat(dateTo)) {
				logger.info("Date validation error ... " + dateTo);
				throw new ValidationExceptions(DATE_FORMAT_VALIDATION,
						FormValidationExceptionEnums.DATE_FORMAT_VALIDATION);
			}
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.getTransactiilteronDetailsWithDateF(merchantId, dateFrom, dateTo));
	}

	@PostMapping(value = "/api/admin/settlement")
	@ApiOperation(value = "Admin User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminsettlement(@RequestParam("uuid") String uuid,
			@RequestBody ProcessSettlementRequest processSettlementRequest)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.processsettlement(userAdminDetails, processSettlementRequest));
	}

	@GetMapping(value = "/api/admin/allMerchantDetailsReport")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantDetailsReport(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException, JsonProcessingException {

		@SuppressWarnings("unused")
		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		return ResponseEntity.ok().body(pgGatewayAdminService.getAllMerchantDetailsReport());
	}

	@GetMapping(value = "/api/admin/getAdminDetails")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getGetAdminDetails(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("adminDetail", userAdminDetails);		
		return ResponseEntity.ok().body(sdto);
	}
	
	@GetMapping(value = "admin/getPassword")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getGetAdminPwd(@RequestParam("emailId") String emailId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = userAdminDetailsRepository.findByEmailId(emailId);
		userAdminDetails.setPassword(Encryption.getDecryptedPassword(userAdminDetails.getPassword()));

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Admin Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("adminDetail", userAdminDetails);		
		return ResponseEntity.ok().body(sdto);
	}
	
	@PostMapping(value = "/api/admin/createBusinessAssociate")
	@ApiOperation(value = "Create Business Associate with MerchantId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createBusinessAssociate(
			@RequestParam("uuid") String uuid,
			@RequestBody BusinessAssociateCreateRequest businessAssociateCreateRequest)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		BusinessAssociate businessAssociate = pgGatewayAdminService.createBusinessAssociate(businessAssociateCreateRequest);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Business Associate with Merchant Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", businessAssociate);		
		return ResponseEntity.ok().body(sdto);
	}
	
	@PostMapping(value = "/api/admin/createBusinessAssociateCommission")
	@ApiOperation(value = "Create Business Associate with MerchantId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createBusinessAssociateCommission(
			@RequestParam("uuid") String uuid,
			@RequestParam("busiAssociateuuid") String busiAssociateuuid,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("commType") String commType,
			@RequestParam("serviceType") String serviceType,
			@RequestParam("serviceSubType") String serviceSubType,
			@RequestParam("commAmount") double commAmount)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = pgGatewayAdminService.createBusinessAssociateCommission(
				busiAssociateuuid,merchantId,commType,serviceType,serviceSubType,commAmount,uuid);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Business Assocaiate Commission Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", businessAssociateCommissionDetails);		
		return ResponseEntity.ok().body(sdto);
	}
	
	@PutMapping(value = "/api/admin/updateBusinessAssociateCommission")
	@ApiOperation(value = "Create Business Associate with MerchantId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateBusinessAssociateCommission(
			@RequestParam("uuid") String uuid,
			@RequestParam("busiAssociateuuid") String busiAssociateuuid,
			@RequestParam("commissionId") int commId,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);
		
		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = pgGatewayAdminService.updateBusinessAssociateCommission(
				busiAssociateuuid,commId,status,uuid);		

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Business Assocaiate Commission Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetail", businessAssociateCommissionDetails);		
		return ResponseEntity.ok().body(sdto);
	}
	
	@GetMapping(value = "/api/admin/merchantCommissionDetailsTotal")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAdminMerchantCommission(
			@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);		
		
		return ResponseEntity.ok().body(pgGatewayAdminService.getMerchantCommDetails(userAdminDetails));
	}
	
	@GetMapping(value = "/api/admin/merchantCommissionDetailsPendingSettlement")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getAdminMerchantCommissionPendindSettlement(
			@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);		
		
		return ResponseEntity.ok().body(pgGatewayAdminService.getAdminMerchantCommissionPendindSettlement(userAdminDetails));
	}
	
	@PutMapping(value = "/api/admin/updateCommissionDetails")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateCommissionDetails(
			@RequestParam("uuid") String uuid,
			@RequestParam("orderId") String orderId,
			@RequestParam("pg_comm") int pgComm,
			@RequestParam("cust_comm") int custComm,
			@RequestParam("businessAssocComm") int businessAssocComm,
			@RequestParam("merchantSettleAmount") int merchantSettleAmount)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);		
		
		return ResponseEntity.ok().body(pgGatewayAdminService.updateCommissionDetails(userAdminDetails,orderId,pgComm,custComm,businessAssocComm,merchantSettleAmount));
	}

	@PostMapping(value = "/api/admin/refundRequest")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> refundRequest(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantOrderId") String orderId	,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("refundTxt") String refundTxt)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);		
		
		return ResponseEntity.ok().body(pgGatewayAdminService.refundRequest(userAdminDetails,orderId,merchantId, refundTxt));
	}
	
	@PutMapping(value = "/api/admin/refundUpdate")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> refundRequestUpdate(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantOrderId") String orderId	,
			@RequestParam("merchantId") String merchantId,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		UserAdminDetails userAdminDetails = jwtUserValidator.validatebyJwtAdminDetails(uuid);		
		
		return ResponseEntity.ok().body(pgGatewayAdminService.refundRequestUpdate(userAdminDetails,orderId,merchantId,status));
	}
	
	@GetMapping("requestSwitchOverPgServices")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> requestSwitchOverPgServices()
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException, JsonProcessingException {
		switchPgServiceDynamically.checkThresholdLimit();
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("RequestSwitchOver", null);	
		return ResponseEntity.ok().body(sdto);
		
	}
	
	@GetMapping("requestRevertSwitchOver")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> requestRevertSwitchOver()
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException, JsonProcessingException {
		thresholdUpdateService.ThresholdUpdate();
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("RevertSwitchOver", null);		
		return ResponseEntity.ok().body(sdto);
	}
}
