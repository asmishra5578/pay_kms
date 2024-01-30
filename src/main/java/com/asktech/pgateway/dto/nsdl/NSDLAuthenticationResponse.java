package com.asktech.pgateway.dto.nsdl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NSDLAuthenticationResponse {

	@JsonProperty("bankId")
	private String bankId;
	@JsonProperty("merchantId")
	private String merchantId;
	@JsonProperty("orderId")
	private String orderId;
	@JsonProperty("pgId")
	private String pgId;
	@JsonProperty("errorMessage")
	private String errorMessage;
	@JsonProperty("encData")
	private String encData;
	@JsonProperty("errorCode")
	private String errorCode;
	@JsonProperty("terminalId")
	private String terminalId;
}
