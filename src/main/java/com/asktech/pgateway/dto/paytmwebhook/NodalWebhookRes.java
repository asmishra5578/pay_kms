package com.asktech.pgateway.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NodalWebhookRes {

	private String event_tracking_id;
	private String response_code;
	private NodalWebhookResData Data;
	
}
