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
public class PayGErrorResponse {

	@JsonProperty("ResponseCode")
	private Integer responseCode;
	@JsonProperty("MoreInfoUrl")
	private String moreInfoUrl;
	@JsonProperty("Message")
	private String message;
	@JsonProperty("Code")
	private String code;
	@JsonProperty("FieldName")
	private String fieldName;
	@JsonProperty("RequestUniqueId")
	private String requestUniqueId;
	@JsonProperty("DeveloperMessage")
	private String developerMessage;
}
