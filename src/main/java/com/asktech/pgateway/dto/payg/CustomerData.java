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
public class CustomerData {

	@JsonProperty("MobileNo")
	private String MobileNo;
	@JsonProperty("Email")
	private String Email;
	@JsonProperty("BillingCity")
	private String BillingCity;
	@JsonProperty("FirstName")
	private String FirstName;
	@JsonProperty("ShippingZipCode")
	private String ShippingZipCode;
	@JsonProperty("ShippingMobileNo")
	private String ShippingMobileNo;
	@JsonProperty("CustomerId")
	private String CustomerId;
	@JsonProperty("CustomerNotes")
	private String CustomerNotes;
	@JsonProperty("BillingAddress")
	private String BillingAddress;
	@JsonProperty("BillingZipCode")
	private String BillingZipCode;
	@JsonProperty("BillingCountry")
	private String BillingCountry;
	@JsonProperty("ShippingFirstName")
	private String ShippingFirstName;
	@JsonProperty("ShippingAddress")
	private String ShippingAddress;
	@JsonProperty("ShippingCountry")
	private String ShippingCountry;
	@JsonProperty("ShippingCity")
	private String ShippingCity;
	@JsonProperty("ShippingState")
	private String ShippingState;
	@JsonProperty("LastName")
	private String LastName;
	@JsonProperty("EmailReceipt")
	private Boolean EmailReceipt;
	@JsonProperty("BillingState")
	private String BillingState;
	@JsonProperty("ShippingLastName")
	private String ShippingLastName;

}
