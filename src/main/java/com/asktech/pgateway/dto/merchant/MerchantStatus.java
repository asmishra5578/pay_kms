package com.asktech.pgateway.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MerchantStatus {

	private String status;	
	private String adminUUid;
	private Integer merchantCount;
}
