package com.asktech.pgateway.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import com.asktech.pgateway.confg.CashfreeConfig;
import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.cashfree.SignatureRequestAll;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.CardPaymentDetails;
import com.asktech.pgateway.model.GPAYPaymentDetails;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.NBPaymentDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.TransactionDetailsAll;
import com.asktech.pgateway.model.UPIPaymentDetails;
import com.asktech.pgateway.model.UserDetails;
import com.asktech.pgateway.repository.CardPaymentDetailsRepository;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGServicesRepository;
import com.asktech.pgateway.repository.TransactionDetailsAllRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.repository.UserDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.FormValidations;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PGGatewayServiceCashFree implements CashFreeFields, ErrorValues {

	@Autowired
	PaymentVerification service;

	@Autowired
	CardPaymentDetailsRepository cardPaymentDetailsRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	MerchantPGServicesRepository merchantPGServicesRepository;
	@Autowired
	MerchantPGDetailsRepository merchantPGDetailsRepository;
	@Autowired
	UserDetailsRepository userDetailsRepository;

	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	TransactionDetailsAllRepository transactionDetailsAllRepository;

	static Logger logger = LoggerFactory.getLogger(PGGatewayServiceCashFree.class);

	public Model getRequestProcess(MultiValueMap<String, String> formData, Model model)
			throws ValidationExceptions, InvalidKeyException, NoSuchAlgorithmException, JsonProcessingException {

		String sign = null;

		/* Checking for Merchant Details */

		if (!FormValidations.AllFieldsValueBlank(formData)) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}

		if (!FormValidations.checkAllAvailableFields(formData)) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		logger.info("Before Merchant Selection");
		
		MerchantDetails merhantDetails = getMerchantFromAppId(formData.get(APPID).get(0));
		logger.info("After Merchant Selection");
		
		String pgID = getPGIDFromMerchantIdandMerchantid(merhantDetails.getMerchantID(),
				formData.get(PAYMENT_OPTION).get(0));
		logger.info("pgID :: "+pgID);
		MerchantPGDetails merchantPGDetails = getPGDetailsWithMerchantId(merhantDetails.getMerchantID(),
				Integer.parseInt(pgID));
		
		logger.info("merchantPGDetails :: "+Utility.convertDTO2JsonString(merchantPGDetails));

		if (!FormValidations.isVerifySignature(formData,
				Encryption.decryptCardNumberOrExpOrCvv(merhantDetails.getSecretId()))) {

			logger.info("Going to validate the Signature");
			throw new ValidationExceptions(SIGNATURE_MISMATCH,
					FormValidationExceptionEnums.SIGNATURE_VERIFICATION_FAILED);
		}

		inputValidator(formData);

		/* Create the User Details on the go */

		// Blank Check as per input Received from Merchant

		String orderId = SecurityUtils.getOrderNumber();
		String merchantSecret = merchantPGDetails.getMerchantPGSecret();
		SignatureRequestAll signatureRequest = new SignatureRequestAll();

		signatureRequest.setAppId(merchantPGDetails.getMerchantPGAppId());
		signatureRequest.setOrderId(orderId);
		signatureRequest.setOrderAmount(formData.get(ORDERAMOUNT).get(0));
		signatureRequest.setOrderCurrency(formData.get(ORDERCURRENCY).get(0));
		signatureRequest.setOrderNote(formData.get(ORDERNOTE).get(0));
		signatureRequest.setCustomerEmail(formData.get(CUSTOMEREMAIL).get(0));
		signatureRequest.setCustomerName(formData.get(CUSOMERNAME).get(0));
		signatureRequest.setCustomerPhone(formData.get(CUSTOMERPHONE).get(0));
		signatureRequest.setReturnUrl(CashfreeConfig.returnUrl);
		signatureRequest.setNotifyUrl(CashfreeConfig.notifyUrl);
		signatureRequest.setPaymentOption(formData.get(PAYMENT_OPTION).get(0));
		

		if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("card")) {
			signatureRequest.setCard_number(formData.get(CARD_NUMBER).get(0));
			signatureRequest.setCard_holder(formData.get(CARD_HOLDER).get(0));
			signatureRequest.setCard_expiryMonth(formData.get(CARD_EXPMONTH).get(0));
			signatureRequest.setCard_cvv(formData.get(CARD_CVV).get(0));
			signatureRequest.setCard_expiryYear(formData.get(CARD_EXPYEAR).get(0));

			sign = service.createSignature(signatureRequest,merchantSecret);
			model = setCardDetails(signatureRequest, orderId, model, sign );

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("nb")) {
			signatureRequest.setPaymentCode(formData.get(PAYMENTCODE).get(0));

			sign = service.createSignature(signatureRequest,merchantSecret);
			model = setNBDetails(signatureRequest, orderId, model, sign);
		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("wallet")) {
			signatureRequest.setPaymentCode(formData.get(PAYMENTCODE).get(0));
			sign = service.createSignature(signatureRequest,merchantSecret);
			model = setWalletDetails(signatureRequest, orderId, model, sign);

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("upi")) {
			if (formData.get(UPI_VPI) != null) {
				logger.info("Inside UPI_VPI");
				signatureRequest.setUpi_vpa(formData.get(UPI_VPI).get(0));
				sign = service.createSignature(signatureRequest,merchantSecret);
				model = setUPIDetails(signatureRequest, orderId, model, sign);

			} else if (formData.get(UPIMODE).get(0).equalsIgnoreCase("gpay")) {
				logger.info("Inside UPIMODE");
				signatureRequest.setUpiMode(formData.get(UPIMODE).get(0));
				sign = service.createSignature(signatureRequest,merchantSecret);
				model = setGPAYDetails(signatureRequest, orderId, model, sign);
			}
		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("emi")) {

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("paylater")) {

		} else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("paypal")) {

		}

		// Populate UserDetails as equired o save the data

		UserDetails userDetails = populateUser(signatureRequest, merhantDetails);
		populateTransactionDetails(userDetails, merhantDetails, signatureRequest, merchantPGDetails);

		return model;

	}

	private void inputValidator(MultiValueMap<String, String> formData) throws ValidationExceptions {

		if (!Validator.isValidEmail(formData.get(CUSTOMEREMAIL).get(0))) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (!Validator.isValidName(formData.get(CUSOMERNAME).get(0))) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (!Validator.isValidPhoneNumber(formData.get(CUSTOMERPHONE).get(0))) {
			throw new ValidationExceptions(PHONE_VAIDATION_FILED, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

	}

	public Model setGPAYDetails(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		GPAYPaymentDetails gpayPaymentDetails = new GPAYPaymentDetails();

		gpayPaymentDetails.setOrderId(orderId);
		gpayPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		gpayPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		gpayPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		gpayPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		gpayPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		gpayPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		gpayPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		gpayPaymentDetails.setUpiMode(signatureRequest.getUpiMode());

		model.addAttribute("custDetails", gpayPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		return model;
	}

	public Model setUPIDetails(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		UPIPaymentDetails upiPaymentDetails = new UPIPaymentDetails();

		upiPaymentDetails.setOrderId(orderId);
		upiPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		upiPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		upiPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		upiPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		upiPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		upiPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		upiPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		upiPaymentDetails.setUpi_vpa(signatureRequest.getUpi_vpa());

		model.addAttribute("custDetails", upiPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		return model;
	}

	public Model setCardDetails(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		CardPaymentDetails cardPaymentDetails = new CardPaymentDetails();

		cardPaymentDetails.setOrderId(orderId);
		cardPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		cardPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		cardPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		cardPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		cardPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		cardPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		cardPaymentDetails.setCardNumber(signatureRequest.getCard_number());
		cardPaymentDetails.setCardCvv(signatureRequest.getCard_cvv());
		cardPaymentDetails.setCardExpiryMonth(signatureRequest.getCard_expiryMonth());
		cardPaymentDetails.setCardExpiryYear(signatureRequest.getCard_expiryYear());
		cardPaymentDetails.setCardHolder(signatureRequest.getCard_holder());
		cardPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());

		model.addAttribute("custDetails", cardPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		cardPaymentDetailsRepository.save(cardPaymentDetails);

		return model;
	}

	public Model setNBDetails(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		NBPaymentDetails nbPaymentDetails = new NBPaymentDetails();

		nbPaymentDetails.setOrderId(orderId);
		nbPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		nbPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		nbPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		nbPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		nbPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		nbPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		nbPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		nbPaymentDetails.setPaymentCode(signatureRequest.getPaymentCode());

		model.addAttribute("custDetails", nbPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		return model;
	}

	public Model setWalletDetails(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		NBPaymentDetails nbPaymentDetails = new NBPaymentDetails();

		nbPaymentDetails.setOrderId(orderId);
		nbPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		nbPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		nbPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		nbPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		nbPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		nbPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		nbPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		nbPaymentDetails.setPaymentCode(signatureRequest.getPaymentCode());

		model.addAttribute("custDetails", nbPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		return model;
	}

	public MerchantDetails getMerchantFromAppId(String appId) throws JsonProcessingException, ValidationExceptions {

		MerchantDetails merhantDetails = merchantDetailsRepository
				.findByAppID(Encryption.encryptCardNumberOrExpOrCvv(appId));
		logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merhantDetails));

		if (merhantDetails == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND + appId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		return merhantDetails;
	}

	public String getPGIDFromMerchantIdandMerchantid(String merchantId, String service)
			throws JsonProcessingException, ValidationExceptions {

		MerchantPGServices merchantPGServices = merchantPGServicesRepository.findByMerchantIDAndService(merchantId,
				service);
		logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merchantPGServices));

		if (merchantPGServices == null) {
			throw new ValidationExceptions(MERCHANT_PG_SERVICE_NO_MAPPED,
					FormValidationExceptionEnums.PG_SERVICE_NOT_MAPPED_WITH_MERCHANT);
		}

		return merchantPGServices.getPgID();
	}

	public MerchantPGDetails getPGDetailsWithMerchantId(String merchantId, long pgId)
			throws JsonProcessingException, ValidationExceptions {

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndId(merchantId, pgId);
		logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merchantPGDetails));

		if (merchantPGDetails == null) {
			throw new ValidationExceptions(MERCHANT_PG_CONFIG_NOT_FOUND,
					FormValidationExceptionEnums.PG_SERVICE_NOT_MAPPED_WITH_MERCHANT);
		}

		return merchantPGDetails;
	}

	public UserDetails populateUser(SignatureRequestAll signatureRequest, MerchantDetails merhantDetails) {

		UserDetails userDetails = new UserDetails();

		if (signatureRequest.getCard_number() == null) {
			userDetails = new UserDetails();
			userDetails.setCardNumber(signatureRequest.getCard_number());
			userDetails.setEmailId(signatureRequest.getCustomerEmail());
			userDetails.setMerchantId(merhantDetails.getMerchantID());
			userDetails.setPhoneNumber(signatureRequest.getCustomerPhone());
			userDetails.setCustomerName(signatureRequest.getCustomerName());
			userDetailsRepository.save(userDetails);
		} else {
			userDetails = userDetailsRepository.findAllByEmailIdAndPhoneNumberAndCardNumberAndMerchantId(
					signatureRequest.getCustomerEmail(), signatureRequest.getCustomerPhone(),
					signatureRequest.getCard_number(), merhantDetails.getMerchantID());
			if (userDetails == null) {
				userDetails = new UserDetails();
				userDetails.setCardNumber(signatureRequest.getCard_number());
				userDetails.setEmailId(signatureRequest.getCustomerEmail());
				userDetails.setMerchantId(merhantDetails.getMerchantID());
				userDetails.setPhoneNumber(signatureRequest.getCustomerPhone());
				userDetailsRepository.save(userDetails);

			}
		}

		userDetails = userDetailsRepository.findAllByEmailIdAndPhoneNumberAndCardNumberAndMerchantId(
				signatureRequest.getCustomerEmail(), signatureRequest.getCustomerPhone(),
				signatureRequest.getCard_number(), merhantDetails.getMerchantID());

		return userDetails;
	}

	public void populateTransactionDetails(UserDetails userDetails, MerchantDetails merhantDetails,
			SignatureRequestAll signatureRequest, MerchantPGDetails merchantPGDetails) {

		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setAmount(Integer.parseInt(signatureRequest.getOrderAmount()));
		transactionDetails.setMerchantId(merhantDetails.getMerchantID());
		transactionDetails.setOrderID(signatureRequest.getOrderId());
		transactionDetails.setPaymentOption(signatureRequest.getPaymentOption());
		transactionDetails.setPgType(merchantPGDetails.getMerchantPGNme());
		transactionDetails.setStatus("PENDING");
		transactionDetails.setUserID(userDetails.getId());
		transactionDetails.setPgId(String.valueOf(merchantPGDetails.getId()));
		transactionDetailsRepository.save(transactionDetails);
	}

	public void updateTransactionStatus(MultiValueMap<String, String> responseFormData) {
		
		TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderIDAndStatus(responseFormData.get("orderId").get(0),"PENDING");
		if(transactionDetails != null ) {
		
			//transactionDetails.setStatus(responseFormData.get("txStatus").get(0));
			transactionDetails.setStatus("SUCCESS");
			transactionDetails.setPgOrderID(responseFormData.get("referenceId").get(0));
			transactionDetails.setPaymentMode(responseFormData.get("paymentMode").get(0));
			transactionDetails.setTxtMsg(responseFormData.get("txMsg").get(0));		
			transactionDetails.setTxtPGTime(responseFormData.get("txTime").get(0));
			
			transactionDetailsRepository.save(transactionDetails);
		}else {
			TransactionDetailsAll transactionDetailsAll = new TransactionDetailsAll();
			transactionDetailsAll.setOrderID(responseFormData.get("orderId").get(0));
			transactionDetailsAll.setStatus(responseFormData.get("txStatus").get(0));
			transactionDetailsAll.setPgOrderID(responseFormData.get("referenceId").get(0));
			transactionDetailsAll.setPaymentMode(responseFormData.get("paymentMode").get(0));
			transactionDetailsAll.setTxtMsg(responseFormData.get("txMsg").get(0));		
			transactionDetailsAll.setTxtPGTime(responseFormData.get("txTime").get(0));
			
			transactionDetailsAllRepository.save(transactionDetailsAll);
		}
	}
	
}
