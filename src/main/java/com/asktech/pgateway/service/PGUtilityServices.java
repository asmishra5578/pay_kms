package com.asktech.pgateway.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.dto.utilityServices.CardBinResponse;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.enums.UtilityType;
import com.asktech.pgateway.model.PgUtilityOptionConfiguration;
import com.asktech.pgateway.repository.PgUtilityOptionConfigurationRepository;
import com.asktech.pgateway.security.Encryption;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class PGUtilityServices {

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminService.class);
	
	@Autowired
	PgUtilityOptionConfigurationRepository pgUtilityOptionConfigurationRepository;

	public CardBinResponse getcardBinDetails(String cardBin) throws IOException {
		
		List<PgUtilityOptionConfiguration> pgUtilityOptionConfigurationList =
				pgUtilityOptionConfigurationRepository.findByUtilityTypeAndStatus(UtilityType.CARD_BIN_CHECK.toString(),UserStatus.ACTIVE.toString());
		
		if(pgUtilityOptionConfigurationList.size()>=1) {
			
			return cashfreeCardBinDetails(pgUtilityOptionConfigurationList.get(0).getPgAppId(),
					Encryption.decryptCardNumberOrExpOrCvv(pgUtilityOptionConfigurationList.get(0).getPgSecret()),
					cardBin,
					pgUtilityOptionConfigurationList.get(0).getApiEndPoint());
			
		}
		return new CardBinResponse();
		
	}
	
	private CardBinResponse cashfreeCardBinDetails(String appId, String secretKey, String binValue, String endPoint) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("appId", appId)
				.addFormDataPart("secretKey",secretKey)
				.addFormDataPart("cardBin", binValue).build();
		Request request = new Request.Builder().url(endPoint).method("POST", body).build();
		Response response = client.newCall(request).execute();
		CardBinResponse cardBinResponse = mapper.readValue(response.body().string(),CardBinResponse.class);
		
		return cardBinResponse;
	}
}
