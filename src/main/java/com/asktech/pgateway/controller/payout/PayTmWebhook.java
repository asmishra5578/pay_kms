package com.asktech.pgateway.controller.payout;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import com.asktech.pgateway.constants.payout.Payout;
import com.asktech.pgateway.dto.paytmwebhook.VanWebhookReq;
import com.asktech.pgateway.dto.paytmwebhook.VanWebhookRes;
import com.asktech.pgateway.dto.paytmwebhook.VanWebhookResData;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.service.payout.PayoutWebhooks;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;

@Controller
public class PayTmWebhook implements Payout {

	@Autowired
	PayoutWebhooks payoutWebhooks;

	@Value("${payTmCredentials.van.secretId}")
	String payoutVanSecret;

	@RequestMapping(value = "/paytm/van", method = RequestMethod.POST)
	public ResponseEntity<?> process(@RequestHeader Map<String, String> headers, @RequestBody VanWebhookReq bodydata)
			throws Exception {
		SuccessResponseDto sdto = new SuccessResponseDto();

		if (paytmJwtDecoder(headers)) {
			System.out.println(Utility.convertDTO2JsonString(bodydata));
			VanWebhookRes vanWebhookRes = new VanWebhookRes();
			vanWebhookRes.setEvent_tracking_id(bodydata.getEvent_tracking_id());
			vanWebhookRes.setResponse_code("CL_2000");
			VanWebhookResData vanWebhookResData = new VanWebhookResData();
			if (bodydata.getData() != null) {
				vanWebhookResData.setBankTxnIdentifier(bodydata.getData().getBankTxnIdentifier());
				vanWebhookResData.setStatus(bodydata.getData().getStatus());
				vanWebhookResData.setRemitterIfsc(bodydata.getData().getRemitterIfsc());

				vanWebhookRes.setData(vanWebhookResData);
			}
			String res = payoutWebhooks.vanWebhook(bodydata);
			HttpHeaders head = new HttpHeaders();
			head.add(HttpHeaders.CACHE_CONTROL, "no-cache");
			String secretKey = payoutVanSecret;
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.valueToTree(vanWebhookRes);
			System.out.println(createJwtToken(bodydata.getCa_id(), secretKey, node));
			head.add("authorization", createJwtToken(bodydata.getCa_id(), secretKey, node));
			head.add("Content-Type", "application/json");

			sdto.getMsg().add("Request Processed Successfully!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("paytmData", res);
			return ResponseEntity.ok().headers(head).body(sdto);
		} else {
			VanWebhookRes vanWebhookRes = new VanWebhookRes();
			vanWebhookRes.setEvent_tracking_id(bodydata.getEvent_tracking_id());
			vanWebhookRes.setResponse_code("CL_2000");
			HttpHeaders head = new HttpHeaders();
			head.add(HttpHeaders.CACHE_CONTROL, "no-cache");
			String secretKey = payoutVanSecret;
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.valueToTree(vanWebhookRes);
			head.add("authorization", createJwtToken(bodydata.getCa_id(), secretKey, node));
			head.add("Content-Type", "application/json");

			sdto.getMsg().add("Request Processed Successfully!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("paytmData", vanWebhookRes);
			return ResponseEntity.ok().headers(head).body(sdto);
		}

	}

	private boolean paytmJwtDecoder(Map<String, String> headers) throws Exception {
		String secretKey = payoutVanSecret;
		System.out.println(secretKey);
		if (headers.get("authorization") != null) {
			String[] chunks = headers.get("authorization").split("\\.");
			Base64.Decoder decoder = Base64.getDecoder();
			String header = new String(decoder.decode(chunks[0]));
			String payload = new String(decoder.decode(chunks[1]));

			System.out.println(header);
			System.out.println(payload);

			SignatureAlgorithm sa = HS256;
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), sa.getJcaName());
			DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);
			String tokenWithoutSignature = chunks[0] + "." + chunks[1];
			String signature = chunks[2];
			if (!validator.isValid(tokenWithoutSignature, signature)) {
				throw new Exception("Could not verify JWT token integrity!");
			} else {
				System.out.println("Valid Token");
			}
		} else {
			throw new JWTException("JWT Missing", FormValidationExceptionEnums.JWT_MISSING);
		}
		return true;
		/*
		 * String data = "HEADER:"+payload + "|BODY:"+new JSONObject(bodydata)+"\n";
		 * File file = new File("LoadData.txt"); FileWriter fr = new FileWriter(file,
		 * true); fr.write(data); fr.close();
		 */
	}

	public String createJwtToken(String caId, String secret, JsonNode data) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("iss", "PAYTMBANK");
		claims.put("ca_id", caId);
		claims.put("data", data);
		return constructJwtTokenString("PAYTMBANK", secret, claims);
	}

	private String constructJwtTokenString(String issuer, String secret, Map<String, Object> claims) {
		
		try {
			if (StringUtils.isEmpty(secret)) {

				return null;
			}
			String payloadJson = String.valueOf(claims);
			if (!StringUtils.isEmpty(issuer)) {
				String jwtToken = Jwts.builder().setPayload(payloadJson)
						.signWith(SignatureAlgorithm.HS256, secret.getBytes()).compact();
				return jwtToken;
			} else {

				return null;
			}
		} catch (Exception e) {
			// LOGGER.error("Exception: {} while generating jwt token :",
			e.getMessage();
			return null;
		}
	}
}
