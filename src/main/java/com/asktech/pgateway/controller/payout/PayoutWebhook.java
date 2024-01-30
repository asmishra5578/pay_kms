package com.asktech.pgateway.controller.payout;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asktech.pgateway.constants.payout.Payout;
import com.asktech.pgateway.dto.NeoCallBackDto;
// import com.asktech.pgateway.dto.paytmwebhook.VanWebhookReq;
import com.asktech.pgateway.service.payout.PayoutWebhooks;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class PayoutWebhook implements Payout {
	@Autowired
	PayoutWebhooks payoutWebhooks;

	static Logger logger = LoggerFactory.getLogger(PayoutWebhook.class);

	@RequestMapping(value = "/cashfree/payout", method = RequestMethod.POST)
	public ResponseEntity<?> process() {

		return ResponseEntity.ok().body("");
	}

	@RequestMapping(value = "/callback/payout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> callbackNeocred(@RequestBody NeoCallBackDto bodydata, HttpServletRequest request) throws JsonProcessingException {

		logger.info("NeoCred return callback data::" + Utility.convertDTO2JsonString(bodydata));
		HttpServletRequest req = (HttpServletRequest) request;
		String ipaddress = Utility.getClientIp(req);
		logger.info("CallBack IP address::" + ipaddress);
		String whiteips = "13.234.228.182, 35.154.144.67";
		String res = payoutWebhooks.neoCredWebhook(bodydata);
		return ResponseEntity.ok().body(res);
		// if (whiteips.contains(ipaddress)) {
		// String res = payoutWebhooks.neoCredWebhook(bodydata);
		// return ResponseEntity.ok().body(res);
		// } else {
		// return ResponseEntity.ok().body(new EasePayCallBackResp("302", "FAILED",
		// "Invalid Request"));
		// }

	}

}
