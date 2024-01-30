package com.asktech.pgateway.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MerchantTransactionDetails {

	private String merchantId;
	private String pgType;
	private String status;
	private Integer amount;
	private String date;
	
}
