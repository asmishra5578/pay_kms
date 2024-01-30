package com.asktech.pgateway.dto.grezpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrezPayTransactionStatus {

	private String dateTime;
	private String orderId;
	private String txnType;
	private String message;
	private String custPhone;
	private String transactionId;
	private String responseCode;
	private String acqId;
	private String paymentType;
	private String cardmask;
	private String appId;
	private String custEmail;
	private String moptype;
	private String orginTxnId;
	private String currency;
	private String approvedAmount;
	private String pgRefNum;
	private String status;
}
