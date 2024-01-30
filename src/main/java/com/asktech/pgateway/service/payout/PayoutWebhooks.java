package com.asktech.pgateway.service.payout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.constants.payout.Payout;
import com.asktech.pgateway.dto.NeoCallBackDto;
import com.asktech.pgateway.dto.paytmwebhook.VanWebhookReq;

import kong.unirest.Unirest;

@Service
public class PayoutWebhooks implements Payout{
	
	@Value("${apiPayoutEndPoint.payoutBaseUrl}")
	String payoutBaseUrl;
	public String vanWebhook(VanWebhookReq dto) {
		System.out.println("Webhook Request");
		String res =  Unirest.post(payoutBaseUrl+"van/webhook/")
				.header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		return res;
	}
	public String nodalWebhook(VanWebhookReq dto) {
		String res =  Unirest.post(payoutBaseUrl+"nodal/webhook/")
				.header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		return res;
	}

	public String neoCredWebhook(NeoCallBackDto dto) {



		String res =  Unirest.post(payoutBaseUrl+"/web/neoCred")
				.header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		return res;
	}
}
