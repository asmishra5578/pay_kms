package com.asktech.pgateway.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminUserRequest {

	private String emailId;
	private String phoneNumber;
	private String userName;
	private String kycStatus;
	private String address1;
	private String address2;
	private String address3;
	private String pincode;
	private String city;
	private String country;
	private String compantName;
}
