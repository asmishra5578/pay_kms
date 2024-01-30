package com.asktech.pgateway.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VanWebhookReq {
	private String event_tracking_id;
	private String ca_id;
	private VanWebhookReqData data;
}
