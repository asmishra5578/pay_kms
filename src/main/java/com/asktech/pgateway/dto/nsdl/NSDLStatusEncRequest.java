package com.asktech.pgateway.dto.nsdl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NSDLStatusEncRequest {

	@JsonProperty("BankId")
	private String bankId;
	@JsonProperty("MerchantId")
	private String merchantId;
	@JsonProperty("TerminalId")
	private String terminalId;
	@JsonProperty("OrderId")
	private String orderId;
	@JsonProperty("AccessCode")
	private String accessCode;
	@JsonProperty("SecureHash")
	private String secureHash;
	@JsonProperty("VPA")
	private String vpa;
	@JsonProperty("TxnType")
	private String txnType;
	
}
