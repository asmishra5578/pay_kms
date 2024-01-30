package com.asktech.pgateway.dto.onePay;

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
public class OnePayTransactionResponse {

	@JsonProperty("payment_mode")
	private String payment_mode;
	@JsonProperty("resp_message")
	private String resp_message;
	@JsonProperty("udf5")
	private String udf5;
	@JsonProperty("cust_email_id")
	private String cust_email_id;
	@JsonProperty("udf3")
	private String udf3;
	@JsonProperty("merchant_id")
	private String merchant_id;
	@JsonProperty("txn_amount")
	private String txn_amount;
	@JsonProperty("udf4")
	private String udf4;
	@JsonProperty("udf1")
	private String udf1;
	@JsonProperty("udf2")
	private String udf2;
	@JsonProperty("pg_ref_id")
	private String pg_ref_id;
	@JsonProperty("txn_id")
	private String txn_id;
	@JsonProperty("resp_date_time")
	private String resp_date_time;
	@JsonProperty("bank_ref_id")
	private String bank_ref_id;
	@JsonProperty("resp_code")
	private String resp_code;
	@JsonProperty("return_url")
	private String return_url;
	@JsonProperty("txn_date_time")
	private String txn_date_time;
	@JsonProperty("trans_status")
	private String trans_status;
	@JsonProperty("cust_mobile_no")
	private String cust_mobile_no;
}
