package com.asktech.pgateway.dto.letspay;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureRequestLetsPay {

	private String payId;
	private String orderId;
	private String orderAmount;
	private String orderCurrency;
	private String customerName;
	private String customerEmail;
	private String customerPhone;
	private String returnUrl;
	
	private String paymentType;
	private String card_number;
	private String card_holder;
	private String card_exp_dt;
	private String card_cvv;
	
	private String paymentCode;
	
	private String upi_vpa;
	private String upiMode;
	
	private String emiPlan;
	
}
