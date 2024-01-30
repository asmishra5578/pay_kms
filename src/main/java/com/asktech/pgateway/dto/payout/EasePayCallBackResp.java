package com.asktech.pgateway.dto.payout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class EasePayCallBackResp {

	@JsonProperty("RESP_CODE")
	private String respCode;
	@JsonProperty("RESPONSE")
	private String response;
	@JsonProperty("RESP_MSG")
	private String respMsg;
	
}
