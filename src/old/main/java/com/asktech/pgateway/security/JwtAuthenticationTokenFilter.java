package com.asktech.pgateway.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.asktech.pgateway.dto.login.JwtAuthenticationToken;
import com.asktech.pgateway.enums.AskTechGateway;
import com.asktech.pgateway.exception.JwtMissingException;

public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

	public JwtAuthenticationTokenFilter() {
		super("/api/**");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		JwtAuthenticationToken token = null;
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Token ")) {
			// Log4jLogger.saveLog("Jwt token is missing==> ");
			throw new JwtMissingException("Jwt token is missing " + AskTechGateway.JWT_MISSING, null);
		}
		String authenticationToken = header.substring(6);
		token = new JwtAuthenticationToken(authenticationToken);
		return getAuthenticationManager().authenticate(token);

	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
	}

}
