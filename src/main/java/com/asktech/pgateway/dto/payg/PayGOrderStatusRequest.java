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
public class PayGOrderStatusRequest {

	@JsonProperty("OrderKeyId")
	private String orderKeyId;
	@JsonProperty("MerchantKeyId")
	private String merchantKeyId;
	@JsonProperty("PaymentType")
	private String paymentType;
		
	
}
