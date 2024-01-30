package com.asktech.pgateway.constants.letsPay;

public interface LetsPay {

	String PAY_ID 			= "PAY_ID";
	String ORDER_ID 		= "ORDER_ID";
	String AMOUNT 			= "AMOUNT";
	String CURRENCY_CODE 	= "CURRENCY_CODE";
	String CUST_EMAIL	    = "CUST_EMAIL";
	String CUST_PHONE 		= "CUST_PHONE";
	String CUST_NAME		= "CUST_NAME";
	String RETURN_URL 		= "RETURN_URL";	
	
	String CARD_NUMBER_LET	= "CARD_NUMBER";
	String CARD_EXP_DT		= "CARD_EXP_DT";
	String CVV 				= "CVV";
	String CARD_HOLDER_NAME	= "CARD_HOLDER_NAME";	
	String PAYMENT_TYPE		= "PAYMENT_TYPE";
	String PAYMENT_TYPE_UPI = "UP";
	String PAYER_NAME		= "PAYER_NAME";
	
	String PAYMENT_TYPE_WL	="WL";
	
	String PAYMENT_CODE 	= "MOP_TYPE";
	String PAYER_ADDRESS	= "PAYER_ADDRESS";
	
	String RETURN_URL_LETSPAY		= "https://development.eazypaymentz.com/pguat/notifyurlLetsPay";
	
	
}
