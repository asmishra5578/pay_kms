package com.asktech.pgateway.dto.payg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class OrderPaymentCustomerData {

	@JsonProperty("MobileNo")
	private String MobileNo;
	@JsonProperty("Email")
	private String Email;
	@JsonProperty("Address")
	private String Address;
	@JsonProperty("FirstName")
	private String FirstName;
	@JsonProperty("State")
	private String State;
	@JsonProperty("ZipCode")
	private String ZipCode;
	@JsonProperty("UserId")
	private String UserId;
	@JsonProperty("Country")
	private String Country;
	@JsonProperty("IpAddress")
	private String IpAddress;
	@JsonProperty("LastName")
	private String LastName;
	@JsonProperty("City")
	private String City;

}