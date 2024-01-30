package com.asktech.pgateway.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NodalWebhookResData {

	private String transactionType;
	private String clientRequestId;
	private String transactionRequestId;
}
