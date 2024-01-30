package com.asktech.pgateway.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.exception.FormValidationException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.service.PGGatewayServiceCashFree;
import com.asktech.pgateway.service.PaymentVerification;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class PGGatewayControllerCashFree {

	static Logger logger = LoggerFactory.getLogger(PGGatewayControllerCashFree.class);

	@Autowired
	PGGatewayServiceCashFree pgGatewayServiceCashFree;
	@Autowired
	PaymentVerification service;

	@RequestMapping(value = "/collectPayment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String paymentPage(@RequestBody MultiValueMap<String, String> formData, Model model)
			throws FormValidationException, ValidationExceptions, InvalidKeyException, NoSuchAlgorithmException,
			JsonProcessingException {

		model = pgGatewayServiceCashFree.getRequestProcess(formData, model);

		if (formData.get(CashFreeFields.PAYMENT_OPTION).get(0).equalsIgnoreCase("card")) {
			return "paymentcardpage";
		} else if (formData.get(CashFreeFields.PAYMENT_OPTION).get(0).equalsIgnoreCase("nb")) {
			return "paymentnbpage";
		} else if (formData.get(CashFreeFields.PAYMENT_OPTION).get(0).equalsIgnoreCase("wallet")) {
			return "paymentwalletpage";
		} else if (formData.get(CashFreeFields.PAYMENT_OPTION).get(0).equalsIgnoreCase("upi")) {

			if (formData.get(CashFreeFields.UPI_VPI) != null) {
				logger.info("Inside UPI_VPI");
				return "paymentupipage";

			} else if (formData.get(CashFreeFields.UPIMODE).get(0).equalsIgnoreCase("gpay")) {
				logger.info("Inside UPIMODE");
				return "paymentgpaypage";
			}

		}

		return "error";
	}

	@RequestMapping(value = "/notifyurl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<?> webhookRequest(@RequestBody MultiValueMap<String, String> responseFormData)
			throws ParseException, Exception {

		logger.info("Method  webhookRequest()");
		logger.info("webhookRequest Request From UI :: " + responseFormData.toString());
		pgGatewayServiceCashFree.updateTransactionStatus(responseFormData);
		return ResponseEntity.ok().body(responseFormData.toString());
	}
}
