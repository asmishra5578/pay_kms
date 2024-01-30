package com.asktech.pgateway.dto.merchant;

import com.asktech.pgateway.model.RefundDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRefundResponse {

	private RefundDetails refundDetails;
	private String header; 
}
