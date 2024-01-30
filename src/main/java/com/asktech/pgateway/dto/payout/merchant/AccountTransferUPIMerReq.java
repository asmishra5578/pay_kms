package com.asktech.pgateway.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransferUPIMerReq {
	private String orderid;
	private String phonenumber;
	private String amount;	
	private String purpose;
	private String beneficiaryVPA;
	private String beneficiaryName;
	private String requestType;
}
