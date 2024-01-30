package com.asktech.pgateway.dto.nsdl;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NSDLAuthenticationRequest {

	private String merchantId;
	private String terminalId;
	private String orderId;
	private String bankId;
	private String encData;
}
