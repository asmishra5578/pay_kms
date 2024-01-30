package com.asktech.pgateway.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDetailsDto {
	private String merchantId;
	private String amount;
	private String paymentOption;
	private String orderID;
	private String status;
	private String paymentMode;
	private String txtMsg;
	private String transactionTime;
	private String merchantOrderId;
	private String merchantReturnURL;
	private String cardNumber;
	private String walletOrBankCode;
	private String vpaUPI;
	private String orderNote;
}
