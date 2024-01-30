package com.asktech.pgateway.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.seam.UserRequest;
import com.asktech.pgateway.dto.seam.UserResponse;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.FormValidationException;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.seam.CustomerRequest;
import com.asktech.pgateway.security.JwtGenerator;
import com.asktech.pgateway.service.PGGatewayServiceCashFree;
import com.asktech.pgateway.service.PGNonSeamLessService;
import com.asktech.pgateway.service.PaymentVerification;
import com.asktech.pgateway.service.UserRequestSeamService;
import com.asktech.pgateway.util.GeneralUtils;
import com.asktech.pgateway.util.JwtUserValidator;
import com.asktech.pgateway.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.asktech.pgateway.model.seam.CardPaymentDetails;

@Controller
public class SeamCustomerRequestController implements ErrorValues {

	static Logger logger = LoggerFactory.getLogger(SeamCustomerRequestController.class);

	@Autowired
	UserRequestSeamService userRequestSeamService;
	@Autowired
	PGGatewayServiceCashFree pgGatewayServiceCashFree;
	@Autowired
	PaymentVerification service;
	@Autowired
	private JwtGenerator jwtGenerator;
	@Autowired
	JwtUserValidator jwtValidator;
	@Autowired
	PGNonSeamLessService pGNonSeamLessService;

	@RequestMapping(value = "/customerFirstRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<?> customerFirstRequest(@RequestBody UserRequest userRequest) {

		logger.info("Inside SeamCustomerRequestController :: customerFirstRequest()");

		UserResponse userResponse = userRequestSeamService.userRequest(userRequest);
		String jwt = (jwtGenerator.generate(userRequest));
		userResponse.setJwtToken(jwt);

		return ResponseEntity.ok().body(userResponse);
	}

	@RequestMapping(value = "/custreq", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String custreq(@RequestBody MultiValueMap<String, String> formData, Model model,
			HttpServletResponse response)
			throws FormValidationException, InvalidKeyException, NoSuchAlgorithmException, JsonProcessingException {
		logger.info("Inside SeamCustomerRequestController :: custreq()");

		String sessionToken = "";
		try {
			sessionToken = pGNonSeamLessService.getRequestProcess(formData, model);
		} catch (JsonProcessingException | ValidationExceptions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!sessionToken.equals("")) {
			Cookie cookie = GeneralUtils.setCookie("token", sessionToken);
			response.addCookie(cookie);
			model.addAttribute("cardnumber", "None");
			//model.addAttribute("CardPaymentDetails", new CardPaymentDetails());
		/*	
			try {
				SecurityUtils.RSAKeyPairGenerator();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			return "paymentpage";
		} else {
			return "error";
		}
	}

	@RequestMapping(value = "/paymentPageSubmit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String paymentPageSubmit(@RequestBody MultiValueMap<String, String> formData, Model model, HttpServletRequest request) throws ValidationExceptions, SessionExpiredException {
		String sessionToken  = "";
		Cookie[] cookies = request.getCookies();
		 if (cookies == null) {
			 logger.info("Cookie not found");
			 throw new ValidationExceptions(INVALID_COOKIE, FormValidationExceptionEnums.INVALID_COOKIE_EXCEPTION);
		 }		
		 if(cookies[0].getName().equals("token")) {
			 sessionToken = cookies[0].getValue();
		 }else {
			 logger.info("Cookie key not found");
			 System.out.println(cookies[0].getName());
			 throw new ValidationExceptions(INVALID_COOKIE, FormValidationExceptionEnums.INVALID_COOKIE_EXCEPTION);		
		 }
		 pGNonSeamLessService.getSubmitPayment(formData, model, sessionToken);
		return "paymentencryptpage";
	}

	@RequestMapping(value = "/api/collectPaymentCustomer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String paymentPage(@RequestBody MultiValueMap<String, String> formData, @RequestParam String uuid,
			Model model) throws FormValidationException, ValidationExceptions, InvalidKeyException,
			NoSuchAlgorithmException, JsonProcessingException, UserException, JWTException, SessionExpiredException {

		CustomerRequest user = jwtValidator.validatebyJwtUserPhone(uuid);

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

}
