package com.asktech.pgateway.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanWebhookRes {

	private String event_tracking_id;
	private String response_code;
	private VanWebhookResData data;
}
