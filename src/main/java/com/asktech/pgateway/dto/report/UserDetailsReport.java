package com.asktech.pgateway.dto.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsReport {

	private String customerName;
	private String emailId;
	private String phoneNumber;
	private String cardNumber;
	private String vpaUpi;
	private String paymentCode;
}
