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
public class NSDLAuthenticationEncRequest {

	@JsonProperty("BankId")
	private String bankId;
	@JsonProperty("MerchantId")
	private String merchantId;
	@JsonProperty("TerminalId")
	private String terminalId;
	@JsonProperty("OrderId")
	private String orderId;
	@JsonProperty("MCC")
	private String mCC;
	@JsonProperty("AccessCode")
	private String accessCode;
	@JsonProperty("Command")
	private String command;
	@JsonProperty("Currency")
	private Integer currency;
	@JsonProperty("Amount")
	private Integer amount;	
	@JsonProperty("PaymentOption")
	private String paymentOption;
	@JsonProperty("IpAddress")
	private String ipAddress;
	@JsonProperty("BrowserDetails")
	private String browserDetails;
	@JsonProperty("AcceptHeader")
	private String acceptHeader;
	@JsonProperty("UserAgent")
	private String userAgent;
	@JsonProperty("CardNumber")
	private String cardNumber;
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	@JsonProperty("CVV")
	private String cVV;
	@JsonProperty("AuthenticationResponseURL")
	private String authenticationResponseURL;
	@JsonProperty("chFirstName")
	private String chFirstName;
	@JsonProperty("chLastName")
	private String chLastName;
	@JsonProperty("chEmail")
	private String chEmail;
	@JsonProperty("chPhone")
	private String chPhone;
	@JsonProperty("SecureHash")
	private String secureHash;
	@JsonProperty("IssuerBankCode")
	private String issuerBankCode;
	@JsonProperty("VPA")
	private String vpa;
}
