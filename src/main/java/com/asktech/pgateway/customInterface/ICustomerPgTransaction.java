package com.asktech.pgateway.customInterface;

public interface ICustomerPgTransaction {

	String getTrx_initiation();
	String getAmount();
	String getOrder_id();
	String getUser_email();
	String getUser_name();
	String getUser_phone();
	String getInitial_status();
	String getDevicetype();
	String getIpaddress();
	String getSubmit_time();
	String getPgOrderId();
	String getPayment_option();
	String getTrx_status();
	String getTxt_msg();
	String getPayment_code();
	String getMerchant_alerturl();
	String getOrder_note();
	
}
