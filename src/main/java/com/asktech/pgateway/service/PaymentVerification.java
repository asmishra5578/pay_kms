package com.asktech.pgateway.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asktech.pgateway.dto.cashfree.SignatureRequestAll;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PaymentVerification {

	public String createSignature(SignatureRequestAll request, String merchantSecret) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("appId", request.getAppId());
		postData.put("orderId", request.getOrderId());
		postData.put("orderAmount", request.getOrderAmount());
		postData.put("orderCurrency", request.getOrderCurrency());
		postData.put("orderNote", request.getOrderNote());
		postData.put("customerName", request.getCustomerName());
		postData.put("customerEmail", request.getCustomerEmail());
		postData.put("customerPhone", request.getCustomerPhone());
		postData.put("returnUrl", request.getReturnUrl());
		postData.put("notifyUrl", request.getNotifyUrl());
		
		
		if (!request.getPaymentOption().isEmpty()) {

			
				if(request.getPaymentOption().equalsIgnoreCase("card")) {
					System.out.println("PaymentOption :: " +request.getPaymentOption() );
					postData.put("paymentOption", request.getPaymentOption());
					postData.put("card_number", request.getCard_number());
					postData.put("card_holder", request.getCard_holder());
					postData.put("card_expiryMonth", request.getCard_expiryMonth());
					postData.put("card_expiryYear", request.getCard_expiryYear());
					postData.put("card_cvv", request.getCard_cvv());
				}
				else if (request.getPaymentOption().equalsIgnoreCase("nb")) {
					System.out.println("PaymentOption :: " +request.getPaymentOption() );
					postData.put("paymentOption", request.getPaymentOption());
					postData.put("paymentCode", request.getPaymentCode());
					
					System.out.println(Utility.convertDTO2JsonString(postData));
				}
				
				
				else if (request.getPaymentOption().equalsIgnoreCase("wallet")) {
					postData.put("paymentOption", request.getPaymentOption());
					postData.put("paymentCode", request.getPaymentCode());
				}
					
				else if (request.getPaymentOption().equalsIgnoreCase("upi")) {					
					if(!StringUtils.isEmpty(request.getUpi_vpa())){
						System.out.println(request.getUpi_vpa());
						postData.put("paymentOption", request.getPaymentOption());
						postData.put("upi_vpa", request.getUpi_vpa());
					}else if(request.getUpiMode().equalsIgnoreCase("gpay")) {
						
						postData.put("paymentOption", request.getPaymentOption());
						postData.put("upiMode", request.getUpiMode());
					}
				}
				
				else if (request.getPaymentOption().equalsIgnoreCase("emi")) {
					postData.put("paymentOption", request.getPaymentOption());
					postData.put("emiPlan", request.getEmiPlan());
					postData.put("paymentCode", request.getPaymentCode());
				}
				
				else if (request.getPaymentOption().equalsIgnoreCase("paylater")) {
					postData.put("paymentOption", request.getPaymentOption());
					postData.put("paymentCode", request.getPaymentCode());					
				}
					
				else if (request.getPaymentOption().equalsIgnoreCase("paypal")) {
					postData.put("paymentOption", request.getPaymentOption());
				}
				

		}
		System.out.println("----Create Signature-----");
		System.out.println(postData);
		return createSignature(postData,merchantSecret);
	}

	public String createSignature(Map<String, String> postData, String merchantSecret) throws NoSuchAlgorithmException, InvalidKeyException {
		String data = "";
		SortedSet<String> keys = new TreeSet<String>(postData.keySet());
		for (String key : keys) {
			data = data + key + postData.get(key);
		}
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key_spec = new SecretKeySpec(merchantSecret.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key_spec);
		String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
		return signature;
	}

}
