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
public class LoginOPTResponseDto {

	private String userId;
	private String responseText;
	private String responseCode;
}
