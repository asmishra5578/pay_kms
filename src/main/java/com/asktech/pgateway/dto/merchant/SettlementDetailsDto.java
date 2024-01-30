package com.asktech.pgateway.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettlementDetailsDto {
	private String merchantId;
	private String trxamount;
	private String merchant_order_id;
	private String order_id;
	private String pg_status;
	private String settlement_status;
	private String service_charge;
	private String tax_calc;
	private String settle_amount_to_merchant;
	private String settlement_date;
	private String tr_type;
}
