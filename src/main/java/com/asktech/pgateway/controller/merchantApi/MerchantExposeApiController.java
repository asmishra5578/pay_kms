package com.asktech.pgateway.controller.merchantApi;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.controller.PGGatewayAdminController;
import com.asktech.pgateway.dto.merchant.MerchantTransaction;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.service.merchantApi.ServiceMerchantApiExposer;
import com.asktech.pgateway.util.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/merchantApi")
public class MerchantExposeApiController implements ErrorValues{

	@Autowired
	ServiceMerchantApiExposer serviceMerchantApiExposer;

	
	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);

	@RequestMapping(value = "/transactionStatus", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<?> merchantTrStatus(@RequestBody MultiValueMap<String, String>  formData, @RequestHeader("Authorization")  String tokenInfo)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, NoSuchAlgorithmException, JsonProcessingException {	
		logger.info("merchantTrStatus ::"+GeneralUtils.MultiValueMaptoJson(formData));
		MerchantTransaction merchantTransaction = serviceMerchantApiExposer.transactionStatus(tokenInfo, formData);		
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionStatus", merchantTransaction.getListMerchantTransactionResponse());
		return ResponseEntity.ok().header("Authorization",merchantTransaction.getHeader() ).body(sdto);
	}
	
	// @RequestMapping(value = "/refundRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	// public ResponseEntity<?> refundRequest(@RequestBody MultiValueMap<String, String>  formData, @RequestHeader("Authorization")  String tokenInfo)
	// 		throws UserException, JWTException, SessionExpiredException, ValidationExceptions, NoSuchAlgorithmException, JsonProcessingException {	
		
	// 	MerchantRefundResponse merchantRefundResponse = serviceMerchantApiExposer.generateRefundRequest(tokenInfo, formData);		
		
	// 	SuccessResponseDto sdto = new SuccessResponseDto();
	// 	sdto.getMsg().add("Request Processed Successfully!");
	// 	sdto.setSuccessCode(SuccessCode.API_SUCCESS);
	// 	sdto.getExtraData().put("refundRequest", merchantRefundResponse.getRefundDetails());
	// 	return ResponseEntity.ok().header("Authorization",merchantRefundResponse.getHeader() ).body(sdto);
	// }
	
	
	

	
}
