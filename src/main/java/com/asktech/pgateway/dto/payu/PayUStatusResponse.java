package com.asktech.pgateway.dto.payu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PayUStatusResponse {

	private String status;
	private String orderId;
	private String merchantOrderId;
	private String errorCode;
	private String errorDesc;
	private String message;
	private String pg_orderid;
	private String txtpgtime;
}
