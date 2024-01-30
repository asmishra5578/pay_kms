package com.asktech.pgateway.util.cashfree;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import com.asktech.pgateway.confg.CashfreeConfig;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.cashfree.SignatureRequestAll;
import com.asktech.pgateway.model.CardPaymentDetails;
import com.asktech.pgateway.model.GPAYPaymentDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.NBPaymentDetails;
import com.asktech.pgateway.model.UPIPaymentDetails;
import com.asktech.pgateway.model.WalletPaymentDetails;
import com.asktech.pgateway.repository.CardPaymentDetailsRepository;
import com.asktech.pgateway.repository.GPAYPaymentDetailsRepository;
import com.asktech.pgateway.repository.NBPaymentDetailsRepository;
import com.asktech.pgateway.repository.UPIPaymentDetailsRepository;
import com.asktech.pgateway.repository.WalletPaymentDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.service.PaymentVerification;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class CashFreeUtilityClass implements CashFreeFields {

	@Autowired
	PaymentVerification service;
	@Autowired
	CardPaymentDetailsRepository cardPaymentDetailsRepository;
	@Autowired
	NBPaymentDetailsRepository nBPaymentDetailsRepository;
	@Autowired
	WalletPaymentDetailsRepository walletPaymentDetailsRepository;
	@Autowired
	UPIPaymentDetailsRepository upiPaymentDetailsRepository;

	static Logger logger = LoggerFactory.getLogger(CashFreeUtilityClass.class);

	public Model processCashFreeRequest(MultiValueMap<String, String> formData, Model model,
			MerchantPGDetails merchantPGDetails, String orderId)
			throws InvalidKeyException, NoSuchAlgorithmException, JsonProcessingException {

		logger.info("Inside processCashFreeRequest()");

		String sign = null;
		System.out.println(Utility.convertDTO2JsonString(merchantPGDetails));
		String merchantSecret = Encryption.decryptCardNumberOrExpOrCvv(merchantPGDetails.getMerchantPGSecret());
		logger.info("MERCHANT SECRET:"+merchantSecret);
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

			logger.info("CashFree Signature :: " + merchantSecret);
			sign = service.createSignature(signatureRequest, merchantSecret);
			model = setCardDetailsCashFree(signatureRequest, orderId, model, sign);

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("nb")) {
			logger.info("Inside processCashFreeRequest() into NB process");
			signatureRequest.setPaymentCode(formData.get(PAYMENTCODE).get(0));

			sign = service.createSignature(signatureRequest, merchantSecret);
			model = setNBDetailsCashFree(signatureRequest, orderId, model, sign);
		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("wallet")) {
			signatureRequest.setPaymentCode(formData.get(PAYMENTCODE).get(0));
			sign = service.createSignature(signatureRequest, merchantSecret);
			model = setWalletDetailsCashFree(signatureRequest, orderId, model, sign);

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("upi")) {
			if (formData.get(UPI_VPI) != null) {
				logger.info("Inside UPI_VPI");
				signatureRequest.setUpi_vpa(formData.get(UPI_VPI).get(0));
				sign = service.createSignature(signatureRequest, merchantSecret);
				model = setUPIDetailsCashFree(signatureRequest, orderId, model, sign);

			} else if (formData.get(UPIMODE).get(0).equalsIgnoreCase("gpay")) {
				logger.info("Inside UPIMODE");
				signatureRequest.setUpiMode(formData.get(UPIMODE).get(0));
				sign = service.createSignature(signatureRequest, merchantSecret);
				model = setGPAYDetailsCashFree(signatureRequest, orderId, model, sign);
			}
		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("emi")) {

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("paylater")) {

		} else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("paypal")) {

		}
		
		
		return model;
	}

	public Model setGPAYDetailsCashFree(SignatureRequestAll signatureRequest, String orderId, Model model,
			String sign) {

		GPAYPaymentDetails gpayPaymentDetails = new GPAYPaymentDetails();

		gpayPaymentDetails.setOrderId(orderId);
		gpayPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		gpayPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		gpayPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		gpayPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		gpayPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		gpayPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		gpayPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		gpayPaymentDetails.setUpiMode(SecurityUtils.encryptSaveData(signatureRequest.getUpiMode()));

		model.addAttribute("custDetails", gpayPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		return model;
	}

	public Model setUPIDetailsCashFree(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		model.addAttribute("orderNote", signatureRequest.getOrderNote());
		model.addAttribute("orderCurrency", signatureRequest.getOrderCurrency());
		model.addAttribute("customerName", signatureRequest.getCustomerName());
		model.addAttribute("customerEmail", signatureRequest.getCustomerEmail());
		model.addAttribute("customerPhone", signatureRequest.getCustomerPhone());
		model.addAttribute("orderAmount", signatureRequest.getOrderAmount());
		model.addAttribute("orderId", orderId);
		model.addAttribute("paymentOption", signatureRequest.getPaymentOption());
		model.addAttribute("upi_vpa", signatureRequest.getUpi_vpa());


		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);
		
		UPIPaymentDetails upiPaymentDetails = new UPIPaymentDetails();

		upiPaymentDetails.setOrderId(orderId);
		upiPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		upiPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		upiPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		upiPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		upiPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		upiPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		upiPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		upiPaymentDetails.setUpi_vpa(SecurityUtils.encryptSaveData(signatureRequest.getUpi_vpa()));

		upiPaymentDetailsRepository.save(upiPaymentDetails);

		return model;
	}

	public void populateDBNbDetails(UPIPaymentDetails upiPaymentDetails) {

		upiPaymentDetails.setUpi_vpa(SecurityUtils.encryptSaveData(upiPaymentDetails.getUpi_vpa()));
		upiPaymentDetailsRepository.save(upiPaymentDetails);

	}

	public Model setCardDetailsCashFree(SignatureRequestAll signatureRequest, String orderId, Model model,
			String sign) {

		System.out.println("Inside setCardDetailsCashFree()");
		CardPaymentDetails cardPaymentDetails = new CardPaymentDetails();

		model.addAttribute("cardNumber", signatureRequest.getCard_number());
		model.addAttribute("orderNote", signatureRequest.getOrderNote());
		model.addAttribute("orderCurrency", signatureRequest.getOrderCurrency());
		model.addAttribute("customerName", signatureRequest.getCustomerName());
		model.addAttribute("customerEmail", signatureRequest.getCustomerEmail());
		model.addAttribute("customerPhone", signatureRequest.getCustomerPhone());
		model.addAttribute("orderAmount", signatureRequest.getOrderAmount());
		model.addAttribute("orderId", orderId);
		model.addAttribute("paymentOption", signatureRequest.getPaymentOption());
		model.addAttribute("cardHolder", signatureRequest.getCard_holder());
		model.addAttribute("cardExpiryMonth", signatureRequest.getCard_expiryMonth());
		model.addAttribute("cardExpiryYear", signatureRequest.getCard_expiryYear());
		model.addAttribute("cardCvv", signatureRequest.getCard_cvv());

		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);

		cardPaymentDetails.setOrderId(orderId);
		cardPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		cardPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		cardPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		cardPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		cardPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		cardPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		//cardPaymentDetails.setCardNumber(SecurityUtils.encryptSaveData(signatureRequest.getCard_number()));
		//cardPaymentDetails.setCardCvv(signatureRequest.getCard_cvv());
		//cardPaymentDetails.setCardExpiryMonth(signatureRequest.getCard_expiryMonth());
		//cardPaymentDetails.setCardExpiryYear(signatureRequest.getCard_expiryYear());
		cardPaymentDetails.setCardHolder(signatureRequest.getCard_holder());
		cardPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());

		cardPaymentDetailsRepository.save(cardPaymentDetails);

		return model;
	}

	public Model setNBDetailsCashFree(SignatureRequestAll signatureRequest, String orderId, Model model, String sign) {

		logger.info("Inside setNBDetailsCashFree()");
		NBPaymentDetails nbPaymentDetails = new NBPaymentDetails();

		model.addAttribute("orderNote", signatureRequest.getOrderNote());
		model.addAttribute("orderCurrency", signatureRequest.getOrderCurrency());
		model.addAttribute("customerName", signatureRequest.getCustomerName());
		model.addAttribute("customerEmail", signatureRequest.getCustomerEmail());
		model.addAttribute("customerPhone", signatureRequest.getCustomerPhone());
		model.addAttribute("orderAmount", signatureRequest.getOrderAmount());
		model.addAttribute("orderId", orderId);
		model.addAttribute("paymentOption", signatureRequest.getPaymentOption());
		model.addAttribute("paymentCode", signatureRequest.getPaymentCode());
		model.addAttribute("custDetails", nbPaymentDetails);
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);
		logger.info("End of setNBDetailsCashFree()");
		
		nbPaymentDetails.setOrderId(orderId);
		nbPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		nbPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		nbPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		nbPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		nbPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		nbPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		nbPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		//nbPaymentDetails.setPaymentCode(SecurityUtils.encryptSaveData(signatureRequest.getPaymentCode()));
		nbPaymentDetails.setPaymentCode(signatureRequest.getPaymentCode());
		nBPaymentDetailsRepository.save(nbPaymentDetails);

		return model;
	}

	public Model setWalletDetailsCashFree(SignatureRequestAll signatureRequest, String orderId, Model model,
			String sign) {

		WalletPaymentDetails walletPaymentDetails = new WalletPaymentDetails();
		
		model.addAttribute("orderNote", signatureRequest.getOrderNote());
		model.addAttribute("orderCurrency", signatureRequest.getOrderCurrency());
		model.addAttribute("customerName", signatureRequest.getCustomerName());
		model.addAttribute("customerEmail", signatureRequest.getCustomerEmail());
		model.addAttribute("customerPhone", signatureRequest.getCustomerPhone());
		model.addAttribute("orderAmount", signatureRequest.getOrderAmount());
		model.addAttribute("orderId", orderId);
		model.addAttribute("paymentOption", signatureRequest.getPaymentOption());
		model.addAttribute("paymentCode", signatureRequest.getPaymentCode());		
		model.addAttribute("signature", sign);
		model.addAttribute("returnUrl", CashfreeConfig.returnUrl);
		model.addAttribute("notifyUrl", CashfreeConfig.notifyUrl);
		model.addAttribute("appId", CashfreeConfig.appId);
		
		walletPaymentDetails.setOrderId(orderId);
		walletPaymentDetails.setOrderAmount(signatureRequest.getOrderAmount());
		walletPaymentDetails.setOrderNote(signatureRequest.getOrderNote());
		walletPaymentDetails.setOrderCurrency(signatureRequest.getOrderCurrency());
		walletPaymentDetails.setCustomerName(signatureRequest.getCustomerName());
		walletPaymentDetails.setCustomerEmail(signatureRequest.getCustomerEmail());
		walletPaymentDetails.setCustomerPhone(signatureRequest.getCustomerPhone());
		walletPaymentDetails.setPaymentOption(signatureRequest.getPaymentOption());
		//walletPaymentDetails.setPaymentCode(SecurityUtils.encryptSaveData(signatureRequest.getPaymentCode()));
		walletPaymentDetails.setPaymentCode(signatureRequest.getPaymentCode());

		walletPaymentDetailsRepository.save(walletPaymentDetails);

		return model;
	}

}
