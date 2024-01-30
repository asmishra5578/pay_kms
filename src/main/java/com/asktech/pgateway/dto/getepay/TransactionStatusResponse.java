package com.asktech.pgateway.dto.getepay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TransactionStatusResponse {

	private String paymentMode;
	private String udf5;
	private String mid;
	private String description;
	private String udf3;
	private String udf4;
	private String udf1;
	private String udf2;
	private String txnAmount;
	private String merchantOrderNo;
	private String txnStatus;
	private String commission;
	private String requeryStatus;
	private String requeryMessage;
	private String getepayTxnId;

}
