package com.asktech.pgateway.schedular;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.model.LetzpayTransactionDetails;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.LetzpayTransactionDetailsRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.util.ChecksumUtils;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class LetzPayTransactionStatusSchedular {

	@Autowired
	LetzpayTransactionDetailsRepository letzpayTransactionDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
	
	public void updateTransactionStatus() throws NoSuchAlgorithmException, JsonProcessingException {
		
		List<LetzpayTransactionDetails> listLetzpayTransactionDetails = letzpayTransactionDetailsRepository.getAllRecords();
		for(LetzpayTransactionDetails letzpayTransactionDetails : listLetzpayTransactionDetails) {
			List<TransactionDetails> listTransactionDetails = transactionDetailsRepository.findByPgOrderID(letzpayTransactionDetails.getTxtId());
			for(TransactionDetails transactionDetails : listTransactionDetails) {
				if(transactionDetails.getPgType().contains("LETZPAY")) {
					PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository.findByPgName(transactionDetails.getPgType());
					System.out.println("PG Secret :: "+pgConfigurationDetails.getPgSaltKey());
					System.out.println("JSON Print :: "+Utility.convertDTO2JsonString(							
							createValueMap(letzpayTransactionDetails,pgConfigurationDetails.getPgSaltKey())));
				}
			}
		}
	}
	
	
	public Map<String, String> createValueMap(LetzpayTransactionDetails letzpayTransactionDetails, String secretKey) throws NoSuchAlgorithmException, JsonProcessingException{
		
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();		

		parameters.put("PAY_ID", letzpayTransactionDetails.getPayId());
		parameters.put("ORDER_ID", letzpayTransactionDetails.getOrderId());
		parameters.put("AMOUNT", letzpayTransactionDetails.getAmount());	
		//parameters.put("TXNTYPE", letzpayTransactionDetails.getTxtType());
		parameters.put("TXNTYPE", "STATUS");
		parameters.put("CURRENCY_CODE",letzpayTransactionDetails.getCurrencyCode());
					
		parameters.put("HASH", ChecksumUtils.generateCheckSumWOSecret(parameters, secretKey));
		
		return parameters;
	}
}
