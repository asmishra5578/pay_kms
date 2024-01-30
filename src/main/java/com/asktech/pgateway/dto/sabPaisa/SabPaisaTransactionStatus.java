package com.asktech.pgateway.dto.sabPaisa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SabPaisaTransactionStatus {

	private String clientTxnId;
	private String spTxnId;
	private float payeeAmount;
	private float paidAmount;
	private String sabPaisaRespCode;
	private String txnStatus;
	private String paymentMode;
	private String transCompleteDate;
	private String timestamp;
	private String status;
	private String error;
	private String message;
	private String path;
}
