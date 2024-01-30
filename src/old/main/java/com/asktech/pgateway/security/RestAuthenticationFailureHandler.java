package com.asktech.pgateway.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.dto.utility.ErrorResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.JwtIllegalArgumentException;
import com.asktech.pgateway.exception.JwtMalformedJwtException;
import com.asktech.pgateway.exception.JwtMissingException;
import com.asktech.pgateway.exception.JwtSignatureException;
import com.asktech.pgateway.exception.JwtTokenExpiredException;
import com.asktech.pgateway.exception.JwtUnsupportedException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		response.setStatus(HttpStatus.OK.value());
		ErrorResponseDto er = new ErrorResponseDto();
		er.getExtraData().put("timestamp", new Date());
		er.getExtraData().put("status", HttpStatus.OK.value());
		er.getExtraData().put("path", request.getRequestURL().toString());

		if (exception instanceof JwtTokenExpiredException) {
			er.setException(FormValidationExceptionEnums.JWT_EXPIRED);
			er.getMsg().add("Jwt Token is Expired");
			//Log4jLogger.saveLog("Jwt Token is Expired==> ");
		}
		if (exception instanceof JwtMissingException) {
			er.setException(FormValidationExceptionEnums.JWT_MISSING);
			er.getMsg().add("Jwt Token is Missing");
			//Log4jLogger.saveLog("Jwt Token is Missing==> ");
		}
		if (exception instanceof JwtSignatureException) {
			er.setException(FormValidationExceptionEnums.JWT_SIGNATURE_MISSING);
			er.getMsg().add("JWT signature is missing");
			//Log4jLogger.saveLog("JWT signature is missing==> ");
		}
		if (exception instanceof JwtMalformedJwtException) {
			er.setException(FormValidationExceptionEnums.JWT_FORMATE_INVALID);
			er.getMsg().add("JWT content is missing");
			//Log4jLogger.saveLog("JWT content is missing==> ");
		}
		if (exception instanceof JwtUnsupportedException) {
			er.setException(FormValidationExceptionEnums.JWT_UNSUPPORTED);
			er.getMsg().add("JWT is Unsupported");
			//Log4jLogger.saveLog("JWT is Unsupported==> ");
		}
		if (exception instanceof JwtIllegalArgumentException) {
			er.setException(FormValidationExceptionEnums.JWT_ILLEGAL_ARGUMENT);
			er.getMsg().add("JWT is IllegalArgument");
			//Log4jLogger.saveLog("JWT is IllegalArgument==> ");
		}
		if (exception instanceof SessionAuthenticationException) {
			er.setException(FormValidationExceptionEnums.SESSION_DEAD);
			er.getMsg().add("session is dead please Login Again!!");
			//Log4jLogger.saveLog("session is dead please Login Again!!==> ");
		}

		OutputStream out = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(out, er);
		out.flush();

	}

}