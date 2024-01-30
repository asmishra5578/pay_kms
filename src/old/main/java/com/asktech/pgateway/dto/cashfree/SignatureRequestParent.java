package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureRequestParent {
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
}
