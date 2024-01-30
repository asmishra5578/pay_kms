package com.asktech.pgateway.dto.payg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PayGOrderRequest {

	@JsonProperty("UniqueRequestId")
	private String UniqueRequestId;
	@JsonProperty("RequestDateTime")
	private String RequestDateTime;
	@JsonProperty("CustomerData")
	private CustomerData CustomerData;
	@JsonProperty("IntegrationData")
	private IntegrationData IntegrationData;
	@JsonProperty("RedirectUrl")
	private String RedirectUrl;
	@JsonProperty("OrderAmount")
	private String OrderAmount;
	@JsonProperty("TransactionData")
	private TransactionData TransactionData;
	@JsonProperty("Merchantkeyid")
	private String Merchantkeyid;

}
