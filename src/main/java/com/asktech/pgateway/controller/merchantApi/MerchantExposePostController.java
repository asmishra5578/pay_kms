package com.asktech.pgateway.controller.merchantApi;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.merchant.MerchantRefundResponse;
import com.asktech.pgateway.dto.merchant.MerchantTransaction;
import com.asktech.pgateway.dto.seam.CrLinkdto;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.service.PaymentMerchantService;
import com.asktech.pgateway.service.merchantApi.ServiceMerchantApiExposer;
import com.asktech.pgateway.service.merchantApi.UpdateTransactionStatus;
import com.asktech.pgateway.util.EncryptSignature;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class MerchantExposePostController implements ErrorValues {

	static Logger logger = LoggerFactory.getLogger(MerchantExposePostController.class);

	@Autowired
	PaymentMerchantService paymentMerchantService;
	@Autowired
	ServiceMerchantApiExposer serviceMerchantApiExposer;

	@PostMapping(value = "/merchantApi/crLink")
	public ResponseEntity<?> merchantCreateApiForCustomer(@ModelAttribute CrLinkdto formData)
			throws UserException, NoSuchAlgorithmException, JsonProcessingException, InvalidKeyException,
			UnsupportedEncodingException, ParseException, ValidationExceptions {
		String appid = formData.getAppid();
		MerchantDetails user = paymentMerchantService.getMerchantFromAppId(appid);
		String uuid = user.getUuid();
		String custName = formData.getCustName();
		String custPhone = formData.getCustPhone();
		String custEmail = formData.getCustEmail();
		String custAmount = formData.getCustAmount();
		String orderId = formData.getOrderId();
		String secret = formData.getSecret();
		int linkExpiry = 5;
		if (formData.getLinkExpiry() != null) {
			linkExpiry = Integer.parseInt(formData.getLinkExpiry());
		}
		String orderNote = formData.getOrderNote();
		String returnUrl = formData.getReturnUrl();
		String source = formData.getSource();
		String storedSignature = Encryption.decryptCardNumberOrExpOrCvv(user.getSecretId());
		logger.info(secret + "|" + storedSignature);

		if (!storedSignature.equals(secret)) {
			throw new ValidationExceptions(SIGNATURE_VERIFICATION_FAILED,
					FormValidationExceptionEnums.SIGNATURE_VERIFICATION_FAILED);
		}
		if (returnUrl == null) {
			returnUrl = "";
		}

		if ((returnUrl.strip().length() < 6)) {
			return ResponseEntity.ok().body(paymentMerchantService.merchantCreateApiForCustomer(uuid, user, custName,
					custPhone, custEmail, custAmount, linkExpiry, orderNote, source));
		} else {
			return ResponseEntity.ok().body(paymentMerchantService.merchantCreateApiForCustomer(uuid, user, custName,
					custPhone, custEmail, custAmount, linkExpiry, orderNote, returnUrl, orderId, source));
		}

	}

	@Value("${apiCustomerReturnUrl}")
	String apiCustomerReturnUrl;
	
	@PostMapping(value = "/merchantApi/v2/crLink")
	public ResponseEntity<?> merchantCreateApiForCustomerTo(@ModelAttribute CrLinkdto formData) throws UserException,
			NoSuchAlgorithmException, JsonProcessingException, InvalidKeyException, UnsupportedEncodingException,
			ParseException, ValidationExceptions, IllegalArgumentException, IllegalAccessException {
		LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>();
		String appid = formData.getAppid();
		MerchantDetails user = paymentMerchantService.getMerchantFromAppId(appid);
		String uuid = user.getUuid();
		String custName = formData.getCustName();
		String custPhone = formData.getCustPhone();
		String custEmail = formData.getCustEmail();
		String custAmount = formData.getCustAmount();
		String orderId, returnUrl, source = "";
		String orderNote = formData.getOrderNote();
				 source = formData.getOrderNote();
		String secret = formData.getSecret();

		if (!Validator.isValidName(custName)) {
			throw new ValidationExceptions(DATA_INVALID + " custName", FormValidationExceptionEnums.DATA_INVALID);
		}
		if (!Validator.isValidPhoneNumber(custPhone)) {
			throw new ValidationExceptions(DATA_INVALID + " custPhone", FormValidationExceptionEnums.DATA_INVALID);
		}
		if (!Validator.isValidEmail(custEmail)) {
			throw new ValidationExceptions(DATA_INVALID + " custEmail", FormValidationExceptionEnums.DATA_INVALID);
		}
		if (!Validator.isNumeric(custAmount)) {
			throw new ValidationExceptions(DATA_INVALID + " Amount", FormValidationExceptionEnums.DATA_INVALID);
		}
		orderId = Utility.getRandomId();
		;
		postData.put("appid", appid);
		postData.put("custAmount", custAmount);
		postData.put("custPhone", custPhone);
		postData.put("custName", custName);
		postData.put("custEmail", custEmail);
		postData.put("orderId", orderId);

		if (orderNote != null) {
			postData.put("orderNote", orderNote);
		} else {
			orderNote = "";
		}
		int linkExpiry = 5;
		if (formData.getLinkExpiry() != null) {
			linkExpiry = Integer.parseInt(formData.getLinkExpiry());
			postData.put("linkExpiry", formData.getLinkExpiry());
		}
		String storedSignature = Encryption.decryptCardNumberOrExpOrCvv(user.getSecretId());
		String hashstr = user.getMerchantID() + "|" + appid + "|" + "PERMALINK";
		String genHash = EncryptSignature.getSecretHash(hashstr, storedSignature);
		logger.debug("SIGNATURE::" + genHash + "///" + secret);
		if (!genHash.equals(secret)) {
			throw new ValidationExceptions(SIGNATURE_VERIFICATION_FAILED,
					FormValidationExceptionEnums.SIGNATURE_VERIFICATION_FAILED);
		}
		/*
		 * String genSignature = EncryptSignature
		 * .encryptSignature(Encryption.decryptCardNumberOrExpOrCvv(user.getSecretId()),
		 * postData); if (!genSignature.equals(formData.getSignature())) { throw new
		 * ValidationExceptions(SIGNATURE_VERIFICATION_FAILED,
		 * FormValidationExceptionEnums.SIGNATURE_VERIFICATION_FAILED); }
		 */
		returnUrl = apiCustomerReturnUrl;
		return ResponseEntity.ok().body(paymentMerchantService.merchantCreateApiForCustomer(uuid, user, custName,
				custPhone, custEmail, custAmount, linkExpiry, orderNote, returnUrl, orderId, source));

	}

	@PostMapping(value = "/merchantApi/v2/mobileapi")
	public ResponseEntity<?> merchantMobileApiForCustomerTo(@ModelAttribute CrLinkdto formData) throws UserException,
			NoSuchAlgorithmException, JsonProcessingException, InvalidKeyException, UnsupportedEncodingException,
			ParseException, ValidationExceptions, IllegalArgumentException, IllegalAccessException {
		LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>();
		String appid = formData.getAppid();
		MerchantDetails user = paymentMerchantService.getMerchantFromAppId(appid);
		String uuid = user.getUuid();
		String custName = formData.getCustName();
		String custPhone = formData.getCustPhone();
		String custEmail = formData.getCustEmail();
		String custAmount = formData.getCustAmount();
		String orderId = formData.getOrderId();
		String orderNote = formData.getOrderNote();
		String secret = formData.getSecret();
		String source = "MOBILE";
		if (!Validator.isValidName(custName)) {
			throw new ValidationExceptions(DATA_INVALID + " custName", FormValidationExceptionEnums.DATA_INVALID);
		}
		if (!Validator.isValidPhoneNumber(custPhone)) {
			throw new ValidationExceptions(DATA_INVALID + " custPhone", FormValidationExceptionEnums.DATA_INVALID);
		}
		if (!Validator.isValidEmail(custEmail)) {
			throw new ValidationExceptions(DATA_INVALID + " custEmail", FormValidationExceptionEnums.DATA_INVALID);
		}
		if (!Validator.isNumeric(custAmount)) {
			throw new ValidationExceptions(DATA_INVALID + " Amount", FormValidationExceptionEnums.DATA_INVALID);
		}

		postData.put("appid", appid);
		postData.put("custAmount", custAmount);
		postData.put("custPhone", custPhone);
		postData.put("custName", custName);
		postData.put("custEmail", custEmail);
		postData.put("orderId", orderId);

		if (orderNote != null) {
			postData.put("orderNote", orderNote);
		} else {
			orderNote = "";
		}
		if (source != null) {
			postData.put("source", source);
		}
		int linkExpiry = 5;
		if (formData.getLinkExpiry() != null) {
			linkExpiry = Integer.parseInt(formData.getLinkExpiry());
			postData.put("linkExpiry", formData.getLinkExpiry());
		}

		String storedSignature = Encryption.decryptCardNumberOrExpOrCvv(user.getSecretId());
		if (!storedSignature.equals(secret)) {
			throw new ValidationExceptions(SIGNATURE_VERIFICATION_FAILED,
					FormValidationExceptionEnums.SIGNATURE_VERIFICATION_FAILED);
		}

		SuccessResponseDto t = paymentMerchantService.merchantCreateApiForCustomer(uuid, user, custName,
				custPhone, custEmail, custAmount, linkExpiry, orderNote, source);
		// t.setLinkCustomer("http://192.168.1.7:8080/mobresp");
		return ResponseEntity.ok().body(t);

	}

	@PutMapping(value = "/merchantApi/resendEmailToCustomer")
	public ResponseEntity<?> sendResendLink(@RequestParam("appId") String appId, @RequestParam("secret") String secret,
			@RequestParam("orderId") String orderId)
			throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException {
		logger.info(appId);
		MerchantDetails merchantDetails = paymentMerchantService.getMerchantFromAppId(appId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
				.body(paymentMerchantService.merchantReEmailSend(orderId, merchantDetails));
	}

	@PutMapping(value = "/merchantApi/resendSmsToCustomer")
	public ResponseEntity<?> sendSmsResendLink(@RequestParam("appId") String appId,
			@RequestParam("secret") String secret,
			@RequestParam("orderId") String orderId)
			throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException, UserException {
		logger.info(appId);
		MerchantDetails merchantDetails = paymentMerchantService.getMerchantFromAppId(appId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
				.body(paymentMerchantService.merchantReSmsSend(orderId, merchantDetails));
	}

	@PutMapping(value = "/merchantApi/resendSmsAndEmailToCustomer")
	public ResponseEntity<?> sendSmsAndEmailResendLink(@RequestParam("appId") String appId,
			@RequestParam("secret") String secret,
			@RequestParam("orderId") String orderId)
			throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException, UserException {
		logger.info(appId);
		MerchantDetails merchantDetails = paymentMerchantService.getMerchantFromAppId(appId);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json")
				.body(paymentMerchantService.merchantReSmsAndEmailSend(orderId, merchantDetails));
	}

	@Autowired
	UpdateTransactionStatus updateTransactionStatus;

	@PostMapping(value = "/merchantApi/transactionStatusWithOrderId")
	public ResponseEntity<?> merchantCreateApiForCustomer(@RequestParam("appId") String appId,
			@RequestParam("secret") String secret, @RequestParam("orderId") String orderId) throws Exception {

		logger.info("Inside /merchantApi/transactionStatusWithOrderId :: ");
		MerchantTransaction merchantTransaction = serviceMerchantApiExposer.getTransactionDetailsUsingOrderId(appId,
				secret, orderId);
		// updateTransactionStatus.updateTransaction(orderId,
		// 		merchantTransaction.getListMerchantTransactionResponse().get(0).getStatus(),
		// 		merchantTransaction.getListMerchantTransactionResponse().get(0).getTxtMsg(),
		// 		merchantTransaction.getListMerchantTransactionResponse().get(0).getMerchantId());

		return ResponseEntity.ok().header("Authorization", merchantTransaction.getHeader())
				.body(merchantTransaction.getListMerchantTransactionResponse());
	}
	@RequestMapping(value = "/refundRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<?> refundRequest(@RequestParam("appId") String appId,
	@RequestParam("secret") String secret, @RequestParam("orderId") String orderId)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions, NoSuchAlgorithmException, JsonProcessingException {	
		
		MerchantRefundResponse merchantRefundResponse = serviceMerchantApiExposer.generateRefundRequest(appId, secret, orderId);		
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundRequest", merchantRefundResponse.getRefundDetails());
		return ResponseEntity.ok().header("Authorization",merchantRefundResponse.getHeader() ).body(sdto);
	}
	
	/*
	 * @PostMapping(value="/captchaVerify") public ResponseEntity<?>
	 * captchaToken(@RequestParam("token") String token) throws IOException{
	 * GoogleCaptchaAssement.verifyToken(token); return
	 * ResponseEntity.ok().body(null); }
	 */
}
