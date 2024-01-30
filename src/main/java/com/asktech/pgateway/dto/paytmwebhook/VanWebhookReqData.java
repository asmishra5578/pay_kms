package com.asktech.pgateway.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanWebhookReqData {

	private String status;
	private String amount;
	private String vanNumber;
	private String beneficiaryAccountNumber;
	private String beneficiaryIfsc;
	private String remitterAccountNumber;
	private String remitterIfsc;
	private String remitterName;
	private String bankTxnIdentifier;
	private String transactionRequestId;
	private String transferMode;
	private String responseCode;
	private String transactionDate;
	private String transactionType;
	private String parentUtr;
	private VanWebhookReqDataMeta meta;
	
}
