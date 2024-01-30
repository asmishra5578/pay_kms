package com.asktech.pgateway.util.letzPay;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.constants.letsPay.LetsPay;
import com.asktech.pgateway.model.CardPaymentDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.NBPaymentDetails;
import com.asktech.pgateway.model.UPIPaymentDetails;
import com.asktech.pgateway.model.WalletPaymentDetails;
import com.asktech.pgateway.repository.CardPaymentDetailsRepository;
import com.asktech.pgateway.repository.NBPaymentDetailsRepository;
import com.asktech.pgateway.repository.UPIPaymentDetailsRepository;
import com.asktech.pgateway.repository.WalletPaymentDetailsRepository;
import com.asktech.pgateway.service.PaymentVerification;
import com.asktech.pgateway.util.LtzPay;
import com.asktech.pgateway.util.SecurityUtils;

@Service
public class LetzpayUtilityClass implements CashFreeFields {

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

	static Logger logger = LoggerFactory.getLogger(LetzpayUtilityClass.class);

	public Model processLetzPayRequest(MultiValueMap<String, String> formData, Model model,
			MerchantPGDetails merchantPGDetails, String orderId) throws NoSuchAlgorithmException {

		logger.info("Inside processLetzPayRequest()");
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put(LetsPay.AMOUNT, formData.get(ORDERAMOUNT).get(0));
		parameters.put(LetsPay.CURRENCY_CODE, "356");
		parameters.put(LetsPay.CUST_EMAIL, formData.get(CUSTOMEREMAIL).get(0));
		parameters.put(LetsPay.CUST_PHONE, formData.get(CUSTOMERPHONE).get(0));
		parameters.put(LetsPay.ORDER_ID, orderId);

		parameters.put(LetsPay.PAY_ID, merchantPGDetails.getMerchantPGAppId());
		parameters.put(LetsPay.RETURN_URL, LetsPay.RETURN_URL_LETSPAY);

		if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("card")) {

			parameters.put(LetsPay.CVV, formData.get(CARD_CVV).get(0));
			parameters.put(LetsPay.CARD_EXP_DT, formData.get(CARD_EXPMONTH).get(0) + formData.get(CARD_EXPYEAR).get(0));
			parameters.put(LetsPay.CARD_HOLDER_NAME, formData.get(CARD_HOLDER).get(0));
			parameters.put(LetsPay.CARD_NUMBER_LET, formData.get(CARD_NUMBER).get(0));
			parameters.put(LetsPay.PAYMENT_TYPE, formData.get(PAYMENT_OPTION).get(0).toUpperCase());
			model = setCardDetailsLetspay(parameters, orderId, model, merchantPGDetails);

		} else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("NB")) {
			logger.info("Inside processCashFreeRequest() into NB process");

			parameters.put(LetsPay.PAYMENT_CODE, formData.get(PAYMENTCODE).get(0));
			parameters.put(LetsPay.CUST_NAME, formData.get(CUSOMERNAME).get(0));
			parameters.put(LetsPay.PAYMENT_TYPE, formData.get(PAYMENT_OPTION).get(0).toUpperCase());
			model = setNBLetspay(parameters, orderId, model, merchantPGDetails);
		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("wallet")) {

			logger.info("Inside processCashFreeRequest() into UP process");
			parameters.put(LetsPay.PAYMENT_CODE, formData.get(PAYMENTCODE).get(0));
			parameters.put(LetsPay.CUST_NAME, formData.get(CUSOMERNAME).get(0));
			parameters.put(LetsPay.PAYMENT_TYPE, LetsPay.PAYMENT_TYPE_WL);

			model = setWalletLetspay(parameters, orderId, model, merchantPGDetails);

		}

		else if (formData.get(PAYMENT_OPTION).get(0).equalsIgnoreCase("upi")) {

			logger.info("Inside processCashFreeRequest() into NB process");
			parameters.put(LetsPay.PAYER_ADDRESS, formData.get(UPI_VPI).get(0));
			parameters.put(LetsPay.PAYER_NAME, formData.get(CUSOMERNAME).get(0));
			parameters.put(LetsPay.PAYMENT_TYPE, LetsPay.PAYMENT_TYPE_UPI);
			model = setUPILetspay(parameters, orderId, model, merchantPGDetails);

		}
		logger.info("End processLetzPayRequest()");
		return model;

	}

	public Model setCardDetailsLetspay(Map<String, String> parameters, String orderId, Model model,
			MerchantPGDetails merchantPGDetails) throws NoSuchAlgorithmException {

		CardPaymentDetails cardPaymentDetails = new CardPaymentDetails();
		logger.info("Inside setCardDetailsLetspay()");
		cardPaymentDetails.setOrderId(orderId);
		cardPaymentDetails.setOrderAmount(parameters.get(LetsPay.AMOUNT));
		cardPaymentDetails.setOrderCurrency(parameters.get(LetsPay.CURRENCY_CODE));
		cardPaymentDetails.setCustomerName(parameters.get(LetsPay.CARD_HOLDER_NAME));
		cardPaymentDetails.setCustomerEmail(parameters.get(LetsPay.CUST_EMAIL));
		cardPaymentDetails.setCustomerPhone(parameters.get(LetsPay.CUST_PHONE));
		//cardPaymentDetails.setCardNumber(SecurityUtils.encryptSaveData(parameters.get(LetsPay.CARD_NUMBER_LET)));
		//cardPaymentDetails.setCardCvv(SecurityUtils.encryptSaveData(parameters.get(LetsPay.CVV)));
		//cardPaymentDetails.setCardExpiryYear(SecurityUtils.encryptSaveData(parameters.get(LetsPay.CARD_EXP_DT)));
		cardPaymentDetails.setCardHolder(parameters.get(LetsPay.CARD_HOLDER_NAME));
		cardPaymentDetails.setPaymentOption(parameters.get(LetsPay.PAYMENT_TYPE));

		logger.info("Input Parameters :: " + parameters.toString());

		String enCryption = LtzPay.generateEncryption(merchantPGDetails, parameters);

		model.addAttribute("PAY_ID", merchantPGDetails.getMerchantPGAppId());
		model.addAttribute("ENCDATA", enCryption);

		cardPaymentDetailsRepository.save(cardPaymentDetails);
		logger.info("End setCardDetailsLetspay()");

		return model;
	}

	public Model setNBLetspay(Map<String, String> parameters, String orderId, Model model,
			MerchantPGDetails merchantPGDetails) throws NoSuchAlgorithmException {

		logger.info("Inside setNBDetailsCashFree()123");
		NBPaymentDetails nbPaymentDetails = new NBPaymentDetails();

		nbPaymentDetails.setOrderId(orderId);
		nbPaymentDetails.setOrderAmount(parameters.get(LetsPay.AMOUNT));
		nbPaymentDetails.setOrderCurrency(parameters.get(LetsPay.CURRENCY_CODE));
		nbPaymentDetails.setCustomerName(parameters.get(LetsPay.CARD_HOLDER_NAME));
		nbPaymentDetails.setCustomerEmail(parameters.get(LetsPay.CUST_EMAIL));
		nbPaymentDetails.setCustomerPhone(parameters.get(LetsPay.CUST_PHONE));
		nbPaymentDetails.setPaymentOption(parameters.get(LetsPay.PAYMENT_TYPE));
		//nbPaymentDetails.setPaymentCode(SecurityUtils.encryptSaveData(parameters.get(LetsPay.PAYMENT_CODE)));
		nbPaymentDetails.setPaymentCode(parameters.get(LetsPay.PAYMENT_CODE));

		logger.info("Input Parameters :: " + parameters.toString());

		String enCryption = LtzPay.generateEncryption(merchantPGDetails, parameters);

		model.addAttribute("PAY_ID", merchantPGDetails.getMerchantPGAppId());
		model.addAttribute("ENCDATA", enCryption);

		nBPaymentDetailsRepository.save(nbPaymentDetails);
		logger.info("End setCardDetailsLetspay()");

		return model;
	}

	public Model setWalletLetspay(Map<String, String> parameters, String orderId, Model model,
			MerchantPGDetails merchantPGDetails) throws NoSuchAlgorithmException {

		logger.info("Inside setWalletDetailsCashFree()123");
		WalletPaymentDetails walletPaymentDetails = new WalletPaymentDetails();

		walletPaymentDetails.setOrderId(orderId);
		walletPaymentDetails.setOrderAmount(parameters.get(LetsPay.AMOUNT));
		walletPaymentDetails.setOrderCurrency(parameters.get(LetsPay.CURRENCY_CODE));
		walletPaymentDetails.setCustomerName(parameters.get(LetsPay.CARD_HOLDER_NAME));
		walletPaymentDetails.setCustomerEmail(parameters.get(LetsPay.CUST_EMAIL));
		walletPaymentDetails.setCustomerPhone(parameters.get(LetsPay.CUST_PHONE));
		walletPaymentDetails.setPaymentOption(parameters.get(LetsPay.PAYMENT_TYPE));
		//walletPaymentDetails.setPaymentCode(SecurityUtils.encryptSaveData(parameters.get(LetsPay.PAYMENT_CODE)));
		walletPaymentDetails.setPaymentCode(parameters.get(LetsPay.PAYMENT_CODE));

		logger.info("Input Parameters :: " + parameters.toString());

		String enCryption = LtzPay.generateEncryption(merchantPGDetails, parameters);

		model.addAttribute("PAY_ID", merchantPGDetails.getMerchantPGAppId());
		model.addAttribute("ENCDATA", enCryption);

		walletPaymentDetailsRepository.save(walletPaymentDetails);
		logger.info("End setWalletDetailsCashFree()");

		return model;
	}

	public Model setUPILetspay(Map<String, String> parameters, String orderId, Model model,
			MerchantPGDetails merchantPGDetails) throws NoSuchAlgorithmException {

		logger.info("Inside setUPILetspay()");
		UPIPaymentDetails upiPaymentDetails = new UPIPaymentDetails();

		upiPaymentDetails.setOrderId(orderId);
		upiPaymentDetails.setOrderAmount(parameters.get(LetsPay.AMOUNT));
		upiPaymentDetails.setOrderCurrency(parameters.get(LetsPay.CURRENCY_CODE));
		upiPaymentDetails.setCustomerName(parameters.get(LetsPay.CARD_HOLDER_NAME));
		upiPaymentDetails.setCustomerEmail(parameters.get(LetsPay.CUST_EMAIL));
		upiPaymentDetails.setCustomerPhone(parameters.get(LetsPay.CUST_PHONE));
		upiPaymentDetails.setPaymentOption(parameters.get(LetsPay.PAYMENT_TYPE_UPI));
		upiPaymentDetails.setUpi_vpa(SecurityUtils.encryptSaveData(parameters.get(LetsPay.PAYER_ADDRESS)));

		logger.info("Input Parameters :: " + parameters.toString());

		String enCryption = LtzPay.generateEncryption(merchantPGDetails, parameters);

		model.addAttribute("PAY_ID", merchantPGDetails.getMerchantPGAppId());
		model.addAttribute("ENCDATA", enCryption);

		upiPaymentDetailsRepository.save(upiPaymentDetails);
		logger.info("End setUPILetspay()");

		return model;
	}
}
