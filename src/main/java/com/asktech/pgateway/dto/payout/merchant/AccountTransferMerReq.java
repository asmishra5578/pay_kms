package com.asktech.pgateway.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountTransferMerReq {
	private String orderid;
	private String phonenumber;
	private String amount;
	private String bankaccount;
	private String ifsc;
	private String purpose;
	private String beneficiaryName;
	private String requestType;
}
