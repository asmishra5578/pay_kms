package com.asktech.pgateway.dto.admin;

import java.util.List;

import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantCreateRequest {

	private String merchantName;
	private String phoneNumber;
	private String emailId;
	private String kycStatus;
}
