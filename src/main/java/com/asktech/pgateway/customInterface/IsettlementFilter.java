package com.asktech.pgateway.customInterface;

public interface IsettlementFilter {
	String getMerchant_id();
	String getMerchant_order_id();
	String getAmount();
	String getTransaction_date();
	String getOrder_id();
	String getPg_status();
	String getSettlement_status();
	String getService_charge();
	String getTax();
	String getSettled_amt();
	String getSettlement_date();
	String getTr_type();	
}
