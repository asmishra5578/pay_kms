package com.asktech.pgateway.exception;

import org.springframework.security.core.AuthenticationException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtSignatureException extends AuthenticationException {
	private static final long serialVersionUID = 1L;
	//private RmeException exception;
	public JwtSignatureException(String msg, Throwable exception) {
		super(msg,exception);
	}
}