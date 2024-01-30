package com.asktech.pgateway.constants.payg;

public interface PayGConstants {

	String INTEGRATIONTYPE = "1";	
	String SOURCE = "3213";
	//String REDIRECTURL = "http://localhost:8080/returnURLPayG/";
	
	String PAYMENT_TYPE_WALLET = "Wallet";
	String PAYMENT_TYPE_UPI = "UPI";
	String PAYMENT_TYPE_NB = "Netbanking";
	
	String CONTENT_TYPE = "Content-Type";
	String CONTENT_TYPE_VALUE = "application/json";
	String AUTHORIZATION = "Authorization";
	String AUTHORIZATION_VALUE = "Basic ";
	String ACCEPT = "Accept";
	String ACCEPT_VALUE = "application/json";
}
