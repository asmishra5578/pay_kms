package com.asktech.pgateway.dto.cashfree;

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
public class TransactionResponse {

	private String orderStatus;
	private String reason;
    private String orderAmount;
    private String status;
    private String txStatus;
    private String txTime;
    private String txMsg;
    private String referenceId;
    private String paymentMode;
    private String orderCurrency;
    private PaymentDetails paymentDetails;

}
