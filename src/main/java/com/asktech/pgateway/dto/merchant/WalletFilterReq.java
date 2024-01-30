package com.asktech.pgateway.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletFilterReq {
	private String fromDate;
	private String toDate;
	private String merchantId;
	private String transactionId;
	private String creditDebit;
	private String walletId;
	private String status;
	private String transactionType;
}
