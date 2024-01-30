package com.asktech.pgateway.dto.getepay;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequeryRequest {

	private String login;
	private String mid;
	private String password;
	private String merchantReferenceNo;
}
