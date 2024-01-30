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
public class TransactionData {

	@JsonProperty("Wallet")
	private Wallet Wallet;
	@JsonProperty("PaymentType")
	private String PaymentType;
	@JsonProperty("Netbanking")
	private Netbanking Netbanking;
	@JsonProperty("Upi")
	private Upi Upi;
}
