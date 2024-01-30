package com.asktech.pgateway.dto.login;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String token;
	
	public JwtAuthenticationToken(String token) {
		super(null, null);
		this.token=token;
	}

	public void setToken(String token) {
		this.token = token;
	}


	public String getToken() {
		
		return token;
	}

	/*public Object getCreditials() {
		return null;
	}*/
	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}
}
