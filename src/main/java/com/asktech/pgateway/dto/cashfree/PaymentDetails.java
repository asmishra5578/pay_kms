package com.asktech.pgateway.dto.cashfree;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDetails {

	private String paymentMode;
	private String cardNumber;
	private String cardCountry;
	private String cardScheme;
	private String authIdCode;
}
