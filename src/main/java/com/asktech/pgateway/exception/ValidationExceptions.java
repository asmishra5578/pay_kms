package com.asktech.pgateway.exception;

import com.asktech.pgateway.enums.FormValidationExceptionEnums;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ValidationExceptions extends Exception{

	private static final long serialVersionUID = 7028569999712319723L;

	private FormValidationExceptionEnums exception;

	public ValidationExceptions(String msg, FormValidationExceptionEnums exception) {
		super(msg);
		this.exception = exception;
		
	}
	
}
