package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureRequestNb  extends SignatureRequestParent{
	private String paymentOption;
	private String paymentCode;
}
