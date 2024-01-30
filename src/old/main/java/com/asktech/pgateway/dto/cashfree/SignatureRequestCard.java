package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignatureRequestCard extends SignatureRequestParent{

	private String paymentOption;
	private String card_number;
	private String card_holder;
	private String card_expiryMonth;
	private String card_expiryYear;
	private String card_cvv;

}
