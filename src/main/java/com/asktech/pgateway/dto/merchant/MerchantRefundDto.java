package com.asktech.pgateway.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRefundDto {

	private String initiatedBy;
	private String refOrderId;
	private String amount;
	private String merchantId;
	private String paymentOption;
	private String paymentMode;
	private String pgOrderId;
	private String pgStatus;
	private String pgTrTime;
	private String merchantOrderId;
	private String paymentCode;
	private String status;
	private String refundMsg;
	
}
