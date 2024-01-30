package com.asktech.pgateway.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePGDetailsRequest {

	private String pgName;
	private String pgAppId;
	private String pgSecretKey;
	private String pgSaltKey;
	
}
