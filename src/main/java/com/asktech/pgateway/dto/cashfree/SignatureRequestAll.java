package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureRequestAll {

	private String appId;
	private String orderId;
	private String orderAmount;
	private String orderCurrency;
	private String orderNote;
	private String customerName;
	private String customerEmail;
	private String customerPhone;
	private String returnUrl;
	private String notifyUrl;
	private String secretKey;
	
	private String paymentOption;
	private String card_number;
	private String card_holder;
	private String card_expiryMonth;
	private String card_expiryYear;
	private String card_cvv;
	
	
	private String paymentCode;
	
	private String upi_vpa;
	private String upiMode;
	
	private String emiPlan;

	
}
