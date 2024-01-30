package com.asktech.pgateway.dto.payout.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletTransferMerRes {
	private String orderid;
	private String status;
	private String message;
}
