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
public class NSDLCardDecryptAuthoResp {

	@JsonProperty("ResponseCode")
	private String responseCode;
	@JsonProperty("MVEnrolled")
	private String mvEnrolled;
	@JsonProperty("Amount")
	private Integer amount;
	@JsonProperty("AccessCode")
	private String accessCode;
	@JsonProperty("MaskedCardNumber")
	private String maskedCardNumber;
	@JsonProperty("MerchantId")
	private String merchantId;
	@JsonProperty("BrowserDetails")
	private String browserDetails;
	@JsonProperty("ResponseMessage")
	private String responseMessage;
	@JsonProperty("OrderId")
	private String orderId;
	@JsonProperty("MCC")
	private String mCC;
	@JsonProperty("AuthenticationResponseURL")
	private String authenticationResponseURL;
	@JsonProperty("MVAuthStatus")
	private String mvAuthStatus;
	@JsonProperty("SecureHash")
	private String secureHash;
	@JsonProperty("Currency")
	private String currency;
	@JsonProperty("AcceptHeader")
	private String acceptHeader;
	@JsonProperty("UserAgent")
	private String userAgent;
	@JsonProperty("TerminalId")
	private String terminalId;
	@JsonProperty("PaymentOption")
	private String paymentOption;
	@JsonProperty("IpAddress")
	private String ipAddress;
	@JsonProperty("BankId")
	private String bankId;
}
