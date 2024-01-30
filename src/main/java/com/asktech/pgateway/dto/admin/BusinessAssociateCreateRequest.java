package com.asktech.pgateway.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAssociateCreateRequest {

	private String name;
	private String phoneNumber;
	private String emailId;
	private String bankName;
	private String accountNumber;
	private String ifscCode;
	private String micrCode;
	private String address;
	private String merchantId;
	
}
