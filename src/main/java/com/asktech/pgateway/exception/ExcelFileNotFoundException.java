package com.asktech.pgateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.asktech.pgateway.enums.FormValidationExceptionEnums;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExcelFileNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111670771807892872L;

	public ExcelFileNotFoundException(String msg, FormValidationExceptionEnums exception) {
		super(msg);
	}

}
