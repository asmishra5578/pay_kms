package com.asktech.pgateway.customInterface;

public interface ISettlementBalanceReport {
	String getCreated();

	String getAmount();

	String getMerchant_id();

	String getMerchant_order_id();

	String getTr_type();

	String getCard_number();

	String getPayment_code();

	String getVpaupi();

	String getSettlement_status();

	String getSettle_amount_to_merchant();

}
