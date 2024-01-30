package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SigntureRequestUpi  extends SignatureRequestParent {
	private String paymentOption;
	private String upi_vpa;
	private String upiMode;
}
