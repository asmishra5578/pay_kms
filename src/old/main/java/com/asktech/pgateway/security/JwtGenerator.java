package com.asktech.pgateway.security;

import java.sql.Date;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.seam.UserRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtGenerator {
	
	private long EXPIRATION_TIME=60*24;

	private String secret="XZUMS4pa2Wus+2LC+VdjM6oJoZawVcvUvc1X9Ovx1mA=";
	
	public String generate(LoginRequestDto jwtUser) {
		
		Claims claims=Jwts.claims().setSubject(jwtUser.getUserNameOrEmail());
		claims.put("UserAgent", jwtUser.getUserAgent());
		claims.put("ipAddress", jwtUser.getIpAddress());
		ZonedDateTime expirationTime=ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME,ChronoUnit.MINUTES);
		//Log4jLogger.saveLog("jwt generation success==> ");
		return Jwts.builder().setClaims(claims).setExpiration(Date.from(expirationTime.toInstant())).signWith(SignatureAlgorithm.HS512,secret).compact();
	}
	
	public String generate(String ipAddress , String mobileNo , String userAgent) {
		
		Claims claims=Jwts.claims().setSubject(mobileNo);
		claims.put("UserAgent", userAgent);
		claims.put("ipAddress", ipAddress);
		ZonedDateTime expirationTime=ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME,ChronoUnit.MINUTES);
		//Log4jLogger.saveLog("jwt generation success==> ");
		return Jwts.builder().setClaims(claims).setExpiration(Date.from(expirationTime.toInstant())).signWith(SignatureAlgorithm.HS512,secret).compact();
	}
	
	public String generate(UserRequest userRequest) {
		
		Claims claims=Jwts.claims().setSubject(userRequest.getUserEmail());
		claims.put("UserName", userRequest.getUserName());
		claims.put("UserPhone", userRequest.getUserPhone());
		ZonedDateTime expirationTime=ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME,ChronoUnit.MINUTES);
		//Log4jLogger.saveLog("jwt generation success==> ");
		return Jwts.builder().setClaims(claims).setExpiration(Date.from(expirationTime.toInstant())).signWith(SignatureAlgorithm.HS512,secret).compact();
	}

}
