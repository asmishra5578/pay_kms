package com.asktech.pgateway.dto.letzpay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)

public class LetzTransactionStatusResponse {

	@JsonProperty("RESPONSE_DATE_TIME")
	private String responseDateTime;
	@JsonProperty("RESPONSE_CODE")
	private String responseCode;
	@JsonProperty("AUTH_CODE")
	private String authCode;
	@JsonProperty("TXN_ID")
	private String txnId;
	@JsonProperty("CUST_PHONE")
	private String custPhone;
	@JsonProperty("IS_STATUS_FINAL")
	private String isStatusFinal;
	@JsonProperty("MOP_TYPE")
	private String mopYype;
	@JsonProperty("ACQ_ID")
	private String acqId;
	@JsonProperty("TXNTYPE")
	private String txnType;
	@JsonProperty("CURRENCY_CODE")
	private String currencyCode;
	@JsonProperty("RRN")
	private String rrn;
	@JsonProperty("HASH")
	private String hash;
	@JsonProperty("PAYMENT_TYPE")
	private String paymentType;
	@JsonProperty("PG_TXN_MESSAGE")
	private String pgTxnMessage;
	@JsonProperty("STATUS")
	private String status;
	@JsonProperty("CREATE_DATE")
	private String createDate;
	@JsonProperty("PG_REF_NUM")
	private String pgRefNum;
	@JsonProperty("PAY_ID")
	private String payId;
	@JsonProperty("ORDER_ID")
	private String orderId;
	@JsonProperty("AMOUNT")
	private String amount;
	@JsonProperty("RESPONSE_MESSAGE")
	private String responseMessage;
	@JsonProperty("ORIG_TXN_ID")
	private String origTxnId;
	@JsonProperty("CUST_EMAIL")
	private String custEmail;
	@JsonProperty("TOTAL_AMOUNT")
	private String totalAmount;
}
