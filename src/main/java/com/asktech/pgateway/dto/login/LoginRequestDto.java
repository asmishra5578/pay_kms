package com.asktech.pgateway.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequestDto {

	private String userNameOrEmail;
	private String password;
	private String userAgent;
	private String ipAddress;	
	private String captchaToken;
}
