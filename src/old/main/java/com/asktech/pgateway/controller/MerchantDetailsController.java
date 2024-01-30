package com.asktech.pgateway.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.merchant.MerchantDashBoardBalance;
import com.asktech.pgateway.dto.merchant.MerchantResponse;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.CommissionStructure;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.model.MerchantBankDetails;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.UserDetails;
import com.asktech.pgateway.service.PGGatewayAdminService;
import com.asktech.pgateway.service.UserLoginService;
import com.asktech.pgateway.util.JwtUserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/api")
public class MerchantDetailsController implements ErrorValues{
	
	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);
	
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	@Autowired
	UserLoginService userLoginService;
	@Autowired
	JwtUserValidator jwtValidator;

	
	@PutMapping(value = "user/passwordChange")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> initialPasswordChange(@RequestParam("userNameOrEmailId") String userNameOrEmailId ,@RequestParam("password") String password)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (userNameOrEmailId == null) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		 userLoginService.passwordChange(userNameOrEmailId, password);
		
		sdto.getMsg().add("Password change successsfully done for userId :: "+userNameOrEmailId);
		sdto.setSuccessCode(SuccessCode.RESET_PASSWORD_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping("/updateMerchantKey")
	public ResponseEntity<?> updateMerchantKey(@RequestParam("uuid") String uuid) throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions, 
	UserException, JWTException, SessionExpiredException{
		
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		
		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.refreshSecretKey(uuid);
		
		return ResponseEntity.ok().body(merchantCreateResponse);
	}
	
	@GetMapping("/getMerchantDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchant(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantResponse merchantResponse = pgGatewayAdminService.merchantView(uuid);
		
		return ResponseEntity.ok().body(merchantResponse);
	}
	
	@GetMapping("/getTransactionDetails")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getMerchanttrDetails(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		List<TransactionDetails> transactionDetails = pgGatewayAdminService.getTransactionDetails(uuid);
		
		return ResponseEntity.ok().body(transactionDetails);
	}
	
	@GetMapping("/getLast3DaysTransaction")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getLast3DaysTransaction(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<TransactionDetails> transactionDetails = pgGatewayAdminService.getLast3DaysTransaction(uuid);
		
		return ResponseEntity.ok().body(transactionDetails);
	}
	
	@GetMapping("/getSettleDetailsLat7Days")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getSettleDetailsLat7Days(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<MerchantBalanceSheet> merchantBalanceSheet = pgGatewayAdminService.getSettleDetailsLat7Days(uuid);
		
		return ResponseEntity.ok().body(merchantBalanceSheet);
	}
	
	@GetMapping("/getUnSettleDetails")
	@ApiOperation(value = "Get erchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getUnSettleDetails(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<MerchantBalanceSheet> transactionDetails = pgGatewayAdminService.getUnSettleDetails(uuid);
		
		return ResponseEntity.ok().body(transactionDetails);
	}
	@GetMapping("/dashBoardBalance")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getDashBoardBalance(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		List<MerchantDashBoardBalance> merchantDashBoardBalance = pgGatewayAdminService.getDashBoardBalance(uuid);
		
		return ResponseEntity.ok().body(merchantDashBoardBalance);
	}
	
	@PostMapping("/createBankDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createbankDetails(@RequestParam("uuid") String uuid, MerchantBankDetails merchantBankDetails ) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		merchantBankDetails = pgGatewayAdminService.createBankDetails(merchantBankDetails,uuid);
		
		return ResponseEntity.ok().body(merchantBankDetails);
	}
	
	@PostMapping("/createMerchantPGDetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchantPGDetals(@RequestParam("uuid") String uuid, 
			@RequestParam("merchantPGNme") String merchantPGNme,
			@RequestParam("merchantPGAppId") String merchantPGAppId,
			@RequestParam("merchantPGSecret") String merchantPGSecret) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		MerchantPGDetails merchantPGDetails = pgGatewayAdminService.createPGDetails(merchantPGNme,merchantPGAppId,merchantPGSecret,uuid);
		
		return ResponseEntity.ok().body(merchantPGDetails);
	}
	
	@PostMapping("/createMerchantPGServices")
	@ApiOperation(value = "Post Merchant Services from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createMerchantPGSerices(@RequestParam("uuid") String uuid, 
			@RequestParam("merchantPGNme") String merchantPGNme,
			@RequestParam("merchantService") String merchantService) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		MerchantPGServices merchantPGServices = pgGatewayAdminService.createPGServices(merchantPGNme,merchantService,uuid);
		
		return ResponseEntity.ok().body(merchantPGServices);
	}
	
	
	@PostMapping("/createCommissionStructure4Merchant")
	@ApiOperation(value = "Post Merchant Services from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createCommStrucForMerchant(@RequestParam("uuid") String uuid, 
			@RequestParam("merchantPGNme") String merchantPGNme,
			@RequestParam("merchantService") String merchantService,
			@RequestParam("pgCommissionType") String pgCommissionType,
			@RequestParam("pgAmount") int pgAmount,
			@RequestParam("askCommissionType") String asCommissionType,
			@RequestParam("askAmount") int askAmount
			) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		CommissionStructure commissionStructure= pgGatewayAdminService.createCommissionstructure(merchantPGNme,merchantService,pgAmount,pgCommissionType,askAmount,asCommissionType,uuid);
		
		return ResponseEntity.ok().body(commissionStructure);
	}
	
	@PostMapping("/createCommissionStructure4Asktech")
	@ApiOperation(value = "Post Merchant Services from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createCommissionstructureAskTech(
			@RequestParam("merchantPGNme") String merchantPGNme,
			@RequestParam("merchantService") String merchantService,		
			@RequestParam("pgCommissionType") String pgCommissionType,
			@RequestParam("pgAmount") int pgAmount,
			@RequestParam("askCommissionType") String asCommissionType,
			@RequestParam("askAmount") int askAmount
			) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		CommissionStructure commissionStructure= pgGatewayAdminService.createCommissionstructureAskTech(merchantPGNme,merchantService,pgAmount,pgCommissionType,askAmount,asCommissionType);
		
		return ResponseEntity.ok().body(commissionStructure);
	}
	
	@GetMapping("/getBankdetails")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getBankDetails(@RequestParam("uuid") String uuid) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		MerchantBankDetails merchantBankDetails = pgGatewayAdminService.getBankDetails(uuid);
		
		return ResponseEntity.ok().body(merchantBankDetails);
	}
	
	@PutMapping("/updateBankdetails")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateBankDetails(@RequestParam("uuid") String uuid, @RequestBody MerchantBankDetails merchantBankDetails) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		merchantBankDetails = pgGatewayAdminService.updateBankDetails(uuid,merchantBankDetails);
		
		return ResponseEntity.ok().body(merchantBankDetails);
	}
	
	@GetMapping("/getCustomer")
	@ApiOperation(value = "Update Merchant Bank Details as per Merchant Request.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getCustomerkDetails(@RequestParam("emailIdOrPhone") String custEmailorPhone) throws UserException, JWTException, SessionExpiredException, JsonProcessingException,IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		logger.info("In the controller");
		UserDetails userDetails = pgGatewayAdminService.getUserDetails(custEmailorPhone);
		
		return ResponseEntity.ok().body(userDetails);
	}
	
}
