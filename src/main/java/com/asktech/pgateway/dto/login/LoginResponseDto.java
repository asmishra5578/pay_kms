package com.asktech.pgateway.dto.login;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(Include.NON_NULL)
public class LoginResponseDto {
	
	private String uuid;
	private String jwtToken;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String dob;
	private String address;
	private String companyName;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private int sessionStatus;
	private String sessionToken;
	private Date sessionExpiryDate;
	private String userAgent;
	private String ipAddress;	
	private int emailVerified;
	private String userType;
	private String payoutFlag;
	private String merchantId;
}
