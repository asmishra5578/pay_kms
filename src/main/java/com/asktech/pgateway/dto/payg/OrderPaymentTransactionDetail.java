package com.asktech.pgateway.dto.payg;

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
@JsonInclude(Include.NON_NULL)
public class OrderPaymentTransactionDetail {

	private Integer ResponseCode;
	private String ProcessorRequestId;
	private String ResponseText;
	private String UpdatedDateTime;
	private long Id;
	private String OrderKeyId;
	private long OrderId;
	private String IPAddress;
	private Integer MerchantKeyId;
	private long TransactionId;
}
