package com.asktech.pgateway.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantSettlement {
	private String created;
	private String amount;
	private String settledAmount;
	private String merchant_id;
	private String merchant_order_id;
	private String settlement_status;
	private String tr_type;
	private String card_number;
	private String WalletOrBankCode;
	private String vpaupi;

}
