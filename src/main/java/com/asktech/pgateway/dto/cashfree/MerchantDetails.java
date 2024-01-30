package com.asktech.pgateway.dto.cashfree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantDetails {

	private String merchantId;
	private String merchantPGName;
	private String merchantPGAppId;
	private String merchantPGSecret;
}
