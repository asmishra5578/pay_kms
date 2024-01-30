package com.asktech.pgateway.dto.merchant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class MerchantResponse {

	private String merchantName;
	private String merchantPhone;
	private String merchantEmail;
	private String merchantAppId;
	private String merchantSecret;
	private String merchantKyc;
	private String merchantOtpStatus;
}
