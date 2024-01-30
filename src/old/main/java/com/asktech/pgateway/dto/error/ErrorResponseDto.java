package com.asktech.pgateway.dto.error;

import java.util.ArrayList;
import java.util.List;

import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDto {

	@JsonProperty("msg")	
	private List<String> msg = new ArrayList<>();
	@JsonProperty("status")
	private boolean status = false;
	@JsonProperty("exception")
	private FormValidationExceptionEnums exceptionEnum;
	@JsonProperty("statusCode")
	private int statusCode = 404;
	
	
}
