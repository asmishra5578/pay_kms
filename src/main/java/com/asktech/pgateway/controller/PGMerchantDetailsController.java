package com.asktech.pgateway.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.merchant.Cards;
import com.asktech.pgateway.dto.merchant.DashBoardDetails;
import com.asktech.pgateway.dto.merchant.ExposeCashfreeApi;
import com.asktech.pgateway.dto.merchant.MerchantKycDetailsResponse;
import com.asktech.pgateway.dto.merchant.MerchantResponse;
import com.asktech.pgateway.dto.merchant.MerchantSettlement;
import com.asktech.pgateway.dto.merchant.MerchantUpdateReq;
import com.asktech.pgateway.dto.merchant.TransactionDetailsDto;
import com.asktech.pgateway.dto.payout.beneficiary.AssociateBankDetails;
import com.asktech.pgateway.dto.payout.beneficiary.CreateBeneficiaryRequest;
import com.asktech.pgateway.dto.payout.beneficiary.DeleteBeneficiaryRequest;
import com.asktech.pgateway.dto.payout.beneficiary.VerifyBankAccount;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.KycStatus;
import com.asktech.pgateway.enums.RefundStatus;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.model.MerchantBankDetails;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.UserDetails;
import com.asktech.pgateway.service.PGGatewayAdminService;
import com.asktech.pgateway.service.PaymentMerchantService;
import com.asktech.pgateway.service.UserLoginService;
import com.asktech.pgateway.service.merchantApi.ServiceMerchantApiExposer;
import com.asktech.pgateway.service.payout.BulkTransactionUpload;
import com.asktech.pgateway.util.FileUpload;
import com.asktech.pgateway.util.GoogleCaptchaAssement;
import com.asktech.pgateway.util.JwtUserValidator;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/api")
public class PGMerchantDetailsController implements ErrorValues {

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);

	@Autowired
	private FileUpload fileStorageService;
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	@Autowired
	PaymentMerchantService paymentMerchantService;
	@Autowired
	UserLoginService userLoginService;
	@Autowired
	ServiceMerchantApiExposer serviceMerchantApiExposer;
	@Autowired
	JwtUserValidator jwtValidator;
	@Autowired
	GoogleCaptchaAssement googleCaptchaAssement;

	@PutMapping(value = "user/passwordChange")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> initialPasswordChange(@RequestParam("userNameOrEmailId") String userNameOrEmailId,
			@RequestParam("password") String password ,@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails merchantDetails = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		if(!merchantDetails.getUuid().equalsIgnoreCase(uuid)) {
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.USER_NOT_EXISTS);
		}
		
		if(!merchantDetails.getMerchantEMail().equalsIgnoreCase(userNameOrEmailId)) {
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.USER_NOT_EXISTS);
		}
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (userNameOrEmailId == null) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		userLoginService.passwordChange(userNameOrEmailId, password);

		sdto.getMsg().add("Password change successsfully done! ");
		sdto.setSuccessCode(SuccessCode.RESET_PASSWORD_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping("/updateMerchantDetails")
	public ResponseEntity<?> updateMerchantDetails(@RequestParam("uuid") String uuid,  @RequestBody MerchantUpdateReq dto) throws IllegalAccessException,
			NoSuchAlgorithmException, ValidationExceptions, UserException, JWTException, SessionExpiredException, jdk.jshell.spi.ExecutionControl.UserException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		Object merchantResponse = userLoginService.updateMerchant(user.getUuid(),dto);
		
//		SuccessResponseDto sdto = new SuccessResponseDto();
//		sdto.getMsg().add("OTP has been send to the Specified Mobile Number and Email Id . Valid for 2 Mins");
//		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
//		sdto.getExtraData().put("MerchantDetail", merchantResponse);
		return ResponseEntity.ok().body(merchantResponse);
	}
	
	@PutMapping("/updateMerchant/verify/otp")
	@ApiOperation(value = "update Merchant Details with OTP verification.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantVerifyOtp(@RequestParam("uuid") String uuid,@RequestParam("otp") int otp,
			@RequestBody MerchantUpdateReq dto, @RequestHeader("OTPSessionId") String otpSessionId) throws UserException, NoSuchAlgorithmException, ValidationExceptions, JWTException, SessionExpiredException, jdk.jshell.spi.ExecutionControl.UserException {
	
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		if (StringUtils.isEmpty(otp)) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		
		MerchantCreateResponse merchantres = userLoginService.merchantUpdateVerifyOtp(user.getUuid(), otp, dto, otpSessionId);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("verification success! Merchant Details has been Updated Successfully.");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("LoginData", merchantres);		
		return ResponseEntity.ok().body(sdto);
	}
	
	@PutMapping(value = "/updateMerchant/resend/otp")
	@ApiOperation(value = "Merchant resend OTP if OTP is not received.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantResenOtp(@RequestParam("uuid") String uuid,  @RequestBody MerchantUpdateReq dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, jdk.jshell.spi.ExecutionControl.UserException, NoSuchAlgorithmException {		
		
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
        Object merchantResponse = userLoginService.updateMerchant(user.getUuid(),dto);
		
//		SuccessResponseDto sdto = new SuccessResponseDto();
//		sdto.getMsg().add("OTP has been send to the Specified Mobile Number and Email Id . Valid for 2 Mins!");
//		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
//		sdto.getExtraData().put("resendOtp", merchantResponse);
		return ResponseEntity.ok().body(merchantResponse);
	}
	
	@PutMapping("/updateMerchantKey")
	public ResponseEntity<?> updateMerchantKey(@RequestParam("uuid") String uuid) throws IllegalAccessException,
			NoSuchAlgorithmException, ValidationExceptions, UserException, JWTException, SessionExpiredException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.refreshSecretKey(user.getUuid());
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant updated!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", merchantCreateResponse);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/getMerchantDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchant(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantResponse merchantResponse = paymentMerchantService.merchantView(user.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetails", merchantResponse);	
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/getCheckSession")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getCheckSession(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		// MerchantResponse merchantResponse =
		// paymentMerchantService.merchantView(uuid);
		String re = "{\"user\": \"" + user.getMerchantName() + "\"}";
		return ResponseEntity.ok().body(re);
	}

	@GetMapping("/getTransactionDetails")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchanttrDetails(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		return ResponseEntity.ok().body(paymentMerchantService.getTransactionDetails(user.getUuid()));
	}

	@GetMapping("/getTransactionDetailsPaging")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchanttrDetailsPaging(@RequestParam("uuid") String uuid,
			@RequestParam("pageNo") int pageNo, @RequestParam("pageRecords") int pageRecords)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		return ResponseEntity.ok().body(paymentMerchantService.getTransactionDetails(user.getUuid(), pageNo, pageRecords));
	}

	@GetMapping("/getLast3DaysTransaction")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getLast3DaysTransaction(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<TransactionDetailsDto> transactionDetails = paymentMerchantService.getLast3DaysTransaction(user.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Last 3 Days Transaction Detail !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetail", transactionDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/getSettleDetailsLat7Days")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getSettleDetailsLat7Days(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<MerchantBalanceSheet> merchantBalanceSheet = paymentMerchantService.getSettleDetailsLat7Days(user.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Last 7 Days Settlement Detail !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("settlementDetail", merchantBalanceSheet);	
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/getUnSettleDetails")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getUnSettleDetails(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<MerchantSettlement> transactionDetails = paymentMerchantService.getUnSettleDetails(user.getUuid());
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Unsettled Status Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("UnsettledDetail", transactionDetails);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/getSettleDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getSettleDetails(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		List<MerchantSettlement> transactionDetails = paymentMerchantService.getSettleDetails(user.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Settlement status Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("settlementDetail", transactionDetails);	
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/dashBoardBalance")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getDashBoardBalance(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		DashBoardDetails merchantDashBoardBalance = paymentMerchantService.getDashBoardBalance(user.getUuid());
	
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Balance Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("dashboardBalance", merchantDashBoardBalance);	
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping("/createBankDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })

	public ResponseEntity<?> createbankDetails(@RequestParam("uuid") String uuid,
			@RequestBody MerchantBankDetails merchantBankDetails) throws UserException, JWTException, SessionExpiredException,
			JsonProcessingException, IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantBankDetails bankDetails = paymentMerchantService.createBankDetails(merchantBankDetails, user.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Bank Details Successfully Updated !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("BankDetails", bankDetails);	
		return ResponseEntity.ok().body(sdto);
	}

	/*
	 * @PostMapping("/createCommissionStructure4Merchant")
	 *
	 * @ApiOperation(value = "Post Merchant Services from Merchant Credentials.",
	 * authorizations = {
	 *
	 * @Authorization(value = "apiKey") }) public ResponseEntity<?>
	 * createCommStrucForMerchant(@RequestParam("uuid") String uuid,
	 *
	 * @RequestParam("merchantPGNme") String merchantPGNme,
	 *
	 * @RequestParam("merchantService") String merchantService,
	 *
	 * @RequestParam("pgCommissionType") String pgCommissionType,
	 *
	 * @RequestParam("pgAmount") int pgAmount,
	 *
	 * @RequestParam("askCommissionType") String asCommissionType,
	 *
	 * @RequestParam("askAmount") int askAmount ) throws UserException,
	 * JWTException, SessionExpiredException,
	 * JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException,
	 * ValidationExceptions{
	 *
	 * CommissionStructure commissionStructure=
	 * pgGatewayAdminService.createCommissionstructure(merchantPGNme,merchantService
	 * ,pgAmount,pgCommissionType,askAmount,asCommissionType,uuid);
	 *
	 * return ResponseEntity.ok().body(commissionStructure); }
	 *
	 * @PostMapping("/createCommissionStructure4Asktech")
	 *
	 * @ApiOperation(value = "Post Merchant Services from Merchant Credentials.",
	 * authorizations = {
	 *
	 * @Authorization(value = "apiKey") }) public ResponseEntity<?>
	 * createCommissionstructureAskTech(
	 *
	 * @RequestParam("merchantPGNme") String merchantPGNme,
	 *
	 * @RequestParam("merchantService") String merchantService,
	 *
	 * @RequestParam("pgCommissionType") String pgCommissionType,
	 *
	 * @RequestParam("pgAmount") int pgAmount,
	 *
	 * @RequestParam("askCommissionType") String asCommissionType,
	 *
	 * @RequestParam("askAmount") int askAmount ) throws UserException,
	 * JWTException, SessionExpiredException,
	 * JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException,
	 * ValidationExceptions{
	 *
	 * CommissionStructure commissionStructure=
	 * pgGatewayAdminService.createCommissionstructureAskTech(merchantPGNme,
	 * merchantService,pgAmount,pgCommissionType,askAmount,asCommissionType);
	 *
	 * return ResponseEntity.ok().body(commissionStructure); }
	 */
	@GetMapping("/getBankdetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getBankDetails(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantBankDetails merchantBankDetails = paymentMerchantService.getBankDetails(user.getUuid());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Bank Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetails", merchantBankDetails);	
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping("/updateBankdetails")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateBankDetails(@RequestParam("uuid") String uuid,
			@RequestBody MerchantBankDetails merchantBankDetails)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantBankDetails bankDetails = paymentMerchantService.updateBankDetails(user.getUuid(), merchantBankDetails);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Bank Details updated!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bankDetails", bankDetails);
		return ResponseEntity.ok().body(sdto);
	}
	
	@PutMapping("/merchant/updateOtpStatus")
	@ApiOperation(value = "Update Merchant OtpStatus Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateOtpStatus(@RequestParam("uuid") String uuid,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantDetails details = paymentMerchantService.updateOtpStatus(user.getUuid(), status);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("OTP Details updated!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("merchantDetails", details);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("/getCustomer")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getCustomerkDetails(@RequestParam("uuid") String uuid,@RequestParam("emailIdOrPhone") String custEmailorPhone)
			throws UserException, JWTException, SessionExpiredException, JsonProcessingException,
			IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions {

		logger.info("In the controller");
		jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<UserDetails> userDetails = pgGatewayAdminService.getUserDetails(custEmailorPhone);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("User Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("userDetails", userDetails);	
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/merchant/transactionDetailsDateFilter")
	@ApiOperation(value = "Admin User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> adminTransactionDetailsDateWise(@RequestParam("uuid") String uuid,
			@RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

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

		return ResponseEntity.ok().body(
				pgGatewayAdminService.getTransactiilteronDetailsWithDateF(user.getMerchantID(), dateFrom, dateTo));
	}

	@GetMapping(value = "/merchant/settlementDetailsDateFilter")
	@ApiOperation(value = "Admin User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> settlementDetailsDateWise(@RequestParam("uuid") String uuid,
			@RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

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
		
	logger.info(dateTo+ " and "+ dateFrom);
		return ResponseEntity.ok()
				.body(paymentMerchantService.getSettlementDetailsWithDateF(user.getMerchantID(), dateFrom, dateTo));
	}

	@GetMapping(value = "/merchant/lastDaySettleMent")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSettleMentLastDay(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantLastDaySettlement(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/currDaySettleMent")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSettleMentCurrDay(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantCurrDaySettlement(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/last7DaysSettleMent")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSettleMentLast7Days(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantLast7DaySettlement(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/currMonthSettleMent")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSettleMentCurrMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantCurrMonthSettlement(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/lastMonthSettleMent")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSettleMentlastMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantLastMonthSettlement(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/last90DaysSettleMent")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantSettleMentLast90Days(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantLast90DaySettlement(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/detailsMerchantDetailsReport")
	@ApiOperation(value = "Merchant User with Date wise transaction ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantDetailsReport(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getMerchantDetailsReport(user.getMerchantID()));
	}

	@GetMapping(value = "/merchant/merchantTransactionLastDay")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionYesterday(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.merchantStatusTransactionLastDay(user));
	}

	@GetMapping(value = "/merchant/merchantTransactionToday")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionToday(@RequestParam("uuid") String uuid,
			HttpServletRequest request)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.merchantStatusTransactionToday(user));
	}

	@GetMapping(value = "/merchant/merchantTransactionCurrMonth")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionCurrMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.merchantStatusTransactionCurrMonth(user));
	}

	@GetMapping(value = "/merchant/merchantTransactionLastMonth")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> adminMerchantTransactionLastMonth(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.merchantStatusTransactionLastMonth(user));
	}

	@PostMapping(value = "/merchant/merchantCreateApiForCustomer")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantCreateApiForCustomer(@RequestParam("uuid") String uuid,
			@RequestParam("custName") String custName, @RequestParam("custPhone") String custPhone,
			@RequestParam("custEmail") String custEmail, @RequestParam("custAmount") String custAmount,
			@RequestParam("linkExpiry") int linkExpiry, @RequestParam("orderNote") String orderNote,
			@RequestParam("returnUrl") String returnUrl, @RequestParam("source") String source,
			@RequestParam("captchaToken") String captchaToken)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, InvalidKeyException,
			NoSuchAlgorithmException, ParseException, IOException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		if(!googleCaptchaAssement.verifyToken(captchaToken)) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}
		
		if ((returnUrl.strip().length() < 6)) {
			return ResponseEntity.ok().body(paymentMerchantService.merchantCreateApiForCustomer(uuid, user, custName,
					custPhone, custEmail, custAmount, linkExpiry, orderNote, source));
		} else {
			String orderId = Utility.getRandomId();
			return ResponseEntity.ok().body(paymentMerchantService.merchantCreateApiForCustomer(uuid, user, custName,
					custPhone, custEmail, custAmount, linkExpiry, orderNote, returnUrl, orderId, source));
		}

	}

	@GetMapping(value = "/merchant/merchantListApiForCustomer")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantListApiForCustomer(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.merchantStatusTransactionLastMonth(user));
	}

	@GetMapping(value = "/merchant/getCustomerApiRequestReport")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getCustomerApiRequestReport(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().body(paymentMerchantService.getCustomerApiRequestReport(user));
	}

	@PostMapping(value = "/merchant/addBeneficiaryBankAccount")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> addBeneficiaryBankAccount(@RequestParam("uuid") String uuid,
			@RequestBody CreateBeneficiaryRequest createBeneficiaryRequest) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, JsonProcessingException, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok()
				.body(paymentMerchantService.addBeneficiaryBankAccount(user, createBeneficiaryRequest));
	}

	@DeleteMapping(value = "/merchant/deleteBeneficiaryBankAccount")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> deleteBeneficiaryBankAccount(@RequestParam("uuid") String uuid,
			@RequestBody DeleteBeneficiaryRequest deleteBeneficiaryRequest) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, JsonProcessingException, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok()
				.body(paymentMerchantService.deleteBeneficiaryBankAccount(user, deleteBeneficiaryRequest));
	}

	@PutMapping(value = "/merchant/associateBeneficiaryBankAccount")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> associateBeneficiaryBankAccount(@RequestParam("uuid") String uuid,
			@RequestBody AssociateBankDetails associateBankDetails) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, JsonProcessingException, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok()
				.body(paymentMerchantService.associateBeneficiaryBankAccount(user, associateBankDetails));
	}

	@PutMapping(value = "/merchant/verfiyBankAnnount")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> verfiyBankAnnount(@RequestParam("uuid") String uuid,
			@RequestBody VerifyBankAccount verifyBankAccount) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, JsonProcessingException, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
				.body(paymentMerchantService.verifyBankAccount(user, verifyBankAccount));
	}

	@PutMapping(value = "/merchant/reVerfiyBankAnnount")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> reVerfiyBankAnnount(@RequestParam("uuid") String uuid,
			@RequestBody VerifyBankAccount verifyBankAccount) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, JsonProcessingException, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
				.body(paymentMerchantService.verifyBankAccount(user, verifyBankAccount));
	}

	@GetMapping(value = "/merchant/customerDetailsReport")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getCustomerDetailsReport(@RequestParam("uuid") String uuid,
			@RequestParam("mobileNo") String mobileNo, @RequestParam("emailId") String emailId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("Inside /merchant/customerDetailsReport");

		return ResponseEntity.ok().body(paymentMerchantService.getCustomerDetailsReport(user, mobileNo, emailId));
	}

	@GetMapping(value = "/merchant/customerDetailsReportAll")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getCustomerDetailsReport(@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("Inside /merchant/customerDetailsReport");

		return ResponseEntity.ok().body(paymentMerchantService.getCustomerDetailsAll(user));
	}

	@GetMapping(value = "/merchant/settlementReportFilter")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantSettleMentReportFilterWise(@RequestParam("uuid") String uuid,
			@RequestParam("orderId") String orderId, @RequestParam("status") String status,
			@RequestParam("pageNo") int pageNo, @RequestParam("pageRecords") int pageRecords,
			@RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		return ResponseEntity.ok().body(paymentMerchantService.getMerchantSettleMentReportFilterWise(user, orderId,
				status, pageNo, pageRecords, dateTo, dateFrom));
	}

	@GetMapping(value = "/merchant/transactionDetailsFilter")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantTransactionReportFilterWise(@RequestParam("uuid") String uuid,
			@RequestParam("orderId") String orderId, @RequestParam("status") String status,
			@RequestParam("pageNo") int pageNo, @RequestParam("pageRecords") int pageRecords,
			@RequestParam("dateTo") String dateTo, @RequestParam("dateFrom") String dateFrom)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		return ResponseEntity.ok().body(paymentMerchantService.getMerchantTransactionFilterWise(user, orderId, status,
				pageNo, pageRecords, dateTo, dateFrom));
	}

	@GetMapping(value = "/merchant/dateSettleMentMerchantWise")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> curentMonthSettleMentMerchantWise(@RequestParam("uuid") String uuid,
			@RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		if (!Validator.isValidateDateFormat(dateFrom) || !Validator.isValidateDateFormat(dateTo)) {
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
				.body(paymentMerchantService.curentMonthSettleMentMerchantWise(user, dateFrom, dateTo));
	}

	@GetMapping(value = "/merchant/customerWiseTransaction")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> customerWiseTransaction(@RequestParam("uuid") String uuid,
			@RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		if (!Validator.isValidateDateFormat(dateFrom) || !Validator.isValidateDateFormat(dateTo)) {
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

		return ResponseEntity.ok().body(paymentMerchantService.customerWiseTransaction(user, dateFrom, dateTo));
	}

	@Autowired
	private BulkTransactionUpload bulkTransactionUpload;

	@PostMapping("/merchant/bulkAccountTransaction")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> bulkAccountTransaction(@RequestParam("file") MultipartFile file,
			@RequestParam("uuid") String uuid) throws ValidationExceptions, NoSuchAlgorithmException,
			EncryptedDocumentException, IOException, UserException, JWTException, SessionExpiredException, InvalidFormatException {
		logger.info("bulkAccountTransaction Controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		return ResponseEntity.ok().body(bulkTransactionUpload.bulkAccountTransaction(file, user.getMerchantID()));
		// return new UploadFileResponse(fl[0], fileDownloadUri, file.getContentType(),
		// file.getSize(), fl[1]);
	}
	
	
	/*
	 * @PostMapping("/merchant/bulkUPITransaction") public UploadFileResponse
	 * bulkUPITransaction(@RequestParam("file") MultipartFile file) {
	 *
	 * String fileName = fileStorageService.storeFile(file);
	 *
	 * String fileDownloadUri =
	 * ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
	 * .path(fileName).toUriString();
	 *
	 * return new UploadFileResponse(fileName, fileDownloadUri,
	 * file.getContentType(), file.getSize()); }
	 *
	 * @PostMapping("/merchant/bulkWalletTransaction") public UploadFileResponse
	 * bulkWalletTransaction(@RequestParam("file") MultipartFile file) {
	 *
	 * String fileName = fileStorageService.storeFile(file);
	 *
	 * String fileDownloadUri =
	 * ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
	 * .path(fileName).toUriString();
	 *
	 * return new UploadFileResponse(fileName, fileDownloadUri,
	 * file.getContentType(), file.getSize()); }
	 *
	 *
	 * @GetMapping("/downloadFile/{fileName:.+}") public ResponseEntity<Resource>
	 * downloadFile(@PathVariable String fileName, HttpServletRequest request) { //
	 * Load file as Resource Resource resource =
	 * fileStorageService.loadFileAsResource(fileName);
	 *
	 * // Try to determine file's content type String contentType = null; try {
	 * contentType =
	 * request.getServletContext().getMimeType(resource.getFile().getAbsolutePath())
	 * ; } catch (IOException ex) { logger.info("Could not determine file type."); }
	 *
	 * // Fallback to the default content type if type could not be determined if
	 * (contentType == null) { contentType = "application/octet-stream"; }
	 *
	 * return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
	 * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
	 * resource.getFilename() + "\"") .body(resource); }
	 */
	
	@PostMapping("/merchant/merchantKycDetails")
	@ApiOperation(value = "Create Merchant KYC.", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> merchantKycDetails(@RequestParam String uuid,
			@RequestParam(value="merchantLegalName", required=true) String merchantLegalName,@RequestParam(value="panCardNumber", required=true) String panCardNumber,
			@RequestParam(value="GstId", required=false) String GstId,@RequestParam(value="webstieUrl", required=true) String webstieUrl,@RequestParam(value="businessEntityType", required=true) String businessEntityType,
			@RequestParam(value="productDescription", required=true) String productDescription,@RequestParam(value="tanNumber", required=false) String tanNumber,@RequestParam(value="regName", required=true) String regName,
			@RequestParam(value="regAddress", required=true) String regAddress,@RequestParam(value="regPinCode", required=true) String regPinCode,@RequestParam(value="regNumber", required=true) String regNumber,
			@RequestParam(value="regEmailAddress", required=true) String regEmailAddress,@RequestParam(value="commName", required=false) String commName,@RequestParam(value="commAddress", required=false) String commAddress,
			@RequestParam(value="commPinCode", required=false) String commPinCode,@RequestParam(value="commNumber", required=false) String commNumber,@RequestParam(value="commEmailAddress", required=false) String commEmailAddress,
			@RequestParam(value="cancelledChequeOrAccountProof", required=false) MultipartFile cancelledChequeOrAccountProof,
			@RequestParam(value="certificateOfIncorporation", required=false) MultipartFile certificateOfIncorporation,
			@RequestParam(value="businessPAN", required=false) MultipartFile businessPAN
			,@RequestParam(value="certificateOfGST", required=false) MultipartFile certificateOfGST, 
			@RequestParam(value="directorKYC", required=false) MultipartFile directorKYC, @RequestParam(value="aoa", required=false) MultipartFile aoa
			,@RequestParam(value="moa", required=false) MultipartFile moa, @RequestParam(value="certficateOfNBFC", required=false) MultipartFile certficateOfNBFC, 
			@RequestParam(value="certficateOfBBPS", required=false) MultipartFile certficateOfBBPS,
			@RequestParam(value="certificateOfSEBIOrAMFI", required=false) MultipartFile certificateOfSEBIOrAMFI)
			throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions,
			UserException, JWTException, SessionExpiredException, EncryptedDocumentException, IOException {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		MerchantKycDetailsResponse merchantKycDetailsResponse = pgGatewayAdminService.merchantKycDetails(user.getMerchantID(), merchantLegalName, panCardNumber, GstId, 
				webstieUrl, businessEntityType, productDescription,  tanNumber, regName,  regAddress,regPinCode,regNumber,
				regEmailAddress, commName, commAddress,commPinCode,commNumber, commEmailAddress,cancelledChequeOrAccountProof,certificateOfIncorporation,businessPAN,certificateOfGST,directorKYC,aoa,moa,
				certficateOfNBFC,certficateOfBBPS,certificateOfSEBIOrAMFI);

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Kyc details uploaded Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantKyc", merchantKycDetailsResponse);		
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "/merchant/updateMerchantKycStatus")
	@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
	public ResponseEntity<?> MerchantKycStatusUpdate(@RequestParam("uuid") String uuid,
			 @RequestParam("status") String statusUpdate)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {

		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

		if (!Validator.containsEnum(KycStatus.class, statusUpdate)) {
			throw new ValidationExceptions(KYC_STATUS, FormValidationExceptionEnums.KYC_STATUS);
		}

		return ResponseEntity.ok()
				.body(pgGatewayAdminService.updatMerchantKycStatus(user.getMerchantID(), statusUpdate));
	}
	
	@PutMapping("/merchant/updateMerchantKycDetails")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateMerchantKycDetails(@RequestParam("uuid") String uuid,
			@RequestParam(value="merchantLegalName", required=true) String merchantLegalName,@RequestParam(value="panCardNumber", required=true) String panCardNumber,
			@RequestParam(value="GstId", required=false) String GstId,@RequestParam(value="webstieUrl", required=true) String webstieUrl,@RequestParam(value="businessEntityType", required=true) String businessEntityType,
			@RequestParam(value="productDescription", required=true) String productDescription,@RequestParam(value="tanNumber", required=false) String tanNumber,@RequestParam(value="regName", required=true) String regName,
			@RequestParam(value="regAddress", required=true) String regAddress,@RequestParam(value="regPinCode", required=true) String regPinCode,@RequestParam(value="regNumber", required=true) String regNumber,
			@RequestParam(value="regEmailAddress", required=true) String regEmailAddress,@RequestParam(value="commName", required=false) String commName,@RequestParam(value="commAddress", required=false) String commAddress,
			@RequestParam(value="commPinCode", required=false) String commPinCode,@RequestParam(value="commNumber", required=false) String commNumber,@RequestParam(value="commEmailAddress", required=false) String commEmailAddress,
			@RequestParam(value="cancelledChequeOrAccountProof", required=false) MultipartFile cancelledChequeOrAccountProof,
			@RequestParam(value="certificateOfIncorporation", required=false) MultipartFile certificateOfIncorporation,
			@RequestParam(value="businessPAN", required=false) MultipartFile businessPAN
			,@RequestParam(value="certificateOfGST", required=false) MultipartFile certificateOfGST, 
			@RequestParam(value="directorKYC", required=false) MultipartFile directorKYC, @RequestParam(value="aoa", required=false) MultipartFile aoa
			,@RequestParam(value="moa", required=false) MultipartFile moa, @RequestParam(value="certficateOfNBFC", required=false) MultipartFile certficateOfNBFC, 
			@RequestParam(value="certficateOfBBPS", required=false) MultipartFile certficateOfBBPS,
			@RequestParam(value="certificateOfSEBIOrAMFI", required=false) MultipartFile certificateOfSEBIOrAMFI) throws UserException, JWTException, SessionExpiredException, IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions, EncryptedDocumentException, IOException{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		return ResponseEntity.ok().body(pgGatewayAdminService.updateKycDetails(user.getMerchantID(), merchantLegalName, panCardNumber, GstId, 
				webstieUrl, businessEntityType, productDescription,  tanNumber, regName,  regAddress,regPinCode,regNumber,
				regEmailAddress, commName, commAddress,commPinCode,commNumber, commEmailAddress,cancelledChequeOrAccountProof,certificateOfIncorporation,businessPAN,certificateOfGST,directorKYC,aoa,moa,
				certficateOfNBFC,certficateOfBBPS,certificateOfSEBIOrAMFI));
	}
	
	@GetMapping("/merchant/getMerchantKycDetails")
	@ApiOperation(value = "Get Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchantKycDetails(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		return ResponseEntity.ok().body(pgGatewayAdminService.getMerchantKyc(user.getMerchantID()));
	}
	
	@PostMapping(value = "/merchant/refundRequest")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> refundRequest(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantOrderId") String orderId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtValidator.validatebyJwtMerchantDetails(uuid);		
		
		return ResponseEntity.ok().body(paymentMerchantService.refundRequest(uuid,orderId,merchantDetails.getMerchantID()));
	}
	
	@PutMapping(value = "/merchant/refundUpdate")
	@ApiOperation(value = "Initiate Refund request", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> refundRequestUpdate(
			@RequestParam("uuid") String uuid,
			@RequestParam("merchantOrderId") String orderId	,
			@RequestParam("status") String status)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtValidator.validatebyJwtMerchantDetails(uuid);	
		
		if (!Validator.containsEnum(RefundStatus.class, status)) {
			throw new ValidationExceptions(REFUND_STATUS, FormValidationExceptionEnums.REFUND_STATUS);
		}
		
		return ResponseEntity.ok().body(paymentMerchantService.refundRequestUpdate(uuid,orderId,merchantDetails.getMerchantID(),status));
	}
	
	@GetMapping(value = "/merchant/getRefundDetails")
	@ApiOperation(value = "Get Associated Merchants Commission", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getRefundDetails(
			@RequestParam("uuid") String uuid)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtValidator.validatebyJwtMerchantDetails(uuid);		
		
		return ResponseEntity.ok().body(paymentMerchantService.refundDetail(merchantDetails.getMerchantID()));
	}
	
	 @GetMapping("/downloadFile/{fileName:.+}")
	  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
	        // Load file as Resource
	        Resource resource = fileStorageService.loadFileAsResource(fileName);

	        // Try to determine file's content type
	        String contentType = null;
	        try {
	            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
	        } catch (IOException ex) {
	            logger.info("Could not determine file type.");
	        }

	        // Fallback to the default content type if type could not be determined
	        if(contentType == null) {
	            contentType = "application/octet-stream";
	        }

	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
	                .body(resource);
	    }
	 
	 @PostMapping(value = "merchant/cashfreeCards")
		@ApiOperation(value = "User can logout. ", authorizations = { @Authorization(value = "apiKey") })
		public ResponseEntity<?> cashfreeCards(@RequestParam("uuid") String uuid,
				@RequestBody Cards cardDetails) throws UserException, JWTException,
				SessionExpiredException, ValidationExceptions, JsonProcessingException, ParseException {

			MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);

			ExposeCashfreeApi res = serviceMerchantApiExposer.cardDetail(cardDetails);
			SuccessResponseDto sdto = new SuccessResponseDto();
			sdto.getMsg().add("Request Processed Successfully !");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("cardDetail", res);	
			return ResponseEntity.ok()
					.body(sdto);
		}
	 
		@GetMapping(value = "/merchant/CustomerPgTransaction")
		@ApiOperation(value = "customer pg transaction ", authorizations = {
				@Authorization(value = "apiKey") })
		public ResponseEntity<?> CustomerPgTransaction(@RequestParam("uuid") String uuid, 
				@RequestParam("dateFrom") String dateFrom, @RequestParam("dateTo") String dateTo)
				throws UserException, JWTException, SessionExpiredException, ValidationExceptions, ParseException, JsonProcessingException {
	
			MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
	
	
			return ResponseEntity.ok().body(pgGatewayAdminService.pgTransactionReport(user.getMerchantID(), dateFrom, dateTo));
		}
	 

}
