package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureRequestPaypal extends SignatureRequestParent{
	private String paymentOption;
}
