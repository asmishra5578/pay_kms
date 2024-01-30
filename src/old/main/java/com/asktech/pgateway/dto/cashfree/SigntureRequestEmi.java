package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SigntureRequestEmi extends SignatureRequestParent {
	private String paymentOption;
	private String emiPlan;
	private String paymentCode;
}
