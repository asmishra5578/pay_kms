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
public class NSDLStatusResponseDecode {

	@JsonProperty("BankId")
	private String bankId;
	@JsonProperty("MerchantId")
	private String merchantID;
	@JsonProperty("TerminalId")
	private String terminalID;
	@JsonProperty("OrderId")
	private String orderID;
	@JsonProperty("AccessCode")
	private String accessCode;
	@JsonProperty("ResponseCode")
	private String responseCode;
	@JsonProperty("ResponseMessage")
	private String responseMessage;
	@JsonProperty("RetRefNo")
	private String retRefNo;
	@JsonProperty("ApprovalCode")
	private String approvalCode;
	@JsonProperty("SecureHash")
	private String secureHash;
	@JsonProperty("Amount")
	private String amount;
	private String status;
}
