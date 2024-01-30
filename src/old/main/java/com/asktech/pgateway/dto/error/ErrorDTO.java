package com.asktech.pgateway.dto.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDTO {

	private String errorType;
	private String errorMessage;
	private String exceptionDetails;
	private String additionalDetails;
}
