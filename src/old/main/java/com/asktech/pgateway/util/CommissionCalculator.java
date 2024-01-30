package com.asktech.pgateway.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.controller.SeamCustomerRequestController;
import com.asktech.pgateway.model.CommissionStructure;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.CommissionStructureRepository;

@Service
public class CommissionCalculator {
	@Autowired
	CommissionStructureRepository csommissionStructureRepository;
	
	
	static Logger logger = LoggerFactory.getLogger(CommissionCalculator.class);

	public void getCommissionId(TransactionDetails transactionDetails) {
		
		List<CommissionStructure> commissionStructureOption = csommissionStructureRepository.findByServiceType(transactionDetails.getPaymentOption());
		if(commissionStructureOption == null ) {
			logger.info("No Commission is defined for Option :: "+transactionDetails.getPaymentOption());
		}
		for(CommissionStructure commissionStructure: commissionStructureOption) {
			
			
		}
		
	} 
	
	
	public String getCardMakerType(String cardNumber) {
		
		return null;
	}
	
}
