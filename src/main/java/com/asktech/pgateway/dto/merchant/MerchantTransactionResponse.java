package com.asktech.pgateway.dto.merchant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantTransactionResponse {

	private String merchantId;
	private String amount;
	private String paymentOption;
	private String orderID;
	private String status;
	private String paymentMode;
	private String txtMsg;
	private String txtPGTime;
	private String merchantOrderId;
	private String cardNumber;
	private String paymentCode;
	private String vpaUPI;
	
}
