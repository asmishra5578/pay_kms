package com.asktech.pgateway.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.enums.PGServices;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.BusinessAssociate;
import com.asktech.pgateway.model.BusinessAssociateCommissionDetails;
import com.asktech.pgateway.model.CommissionStructure;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.repository.BusinessAssociateCommissionDetailsRepo;
import com.asktech.pgateway.repository.BusinessAssociateRepository;
import com.asktech.pgateway.repository.CommissionStructureRepository;
import com.asktech.pgateway.repository.MerchantBalanceSheetRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class CommissionCalculator {
	@Autowired
	CommissionStructureRepository commissionStructureRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantBalanceSheetRepository merchantBalanceSheetRepository;
	@Autowired
	BusinessAssociateCommissionDetailsRepo businessAssociateCommissionDetailsRepo;
	@Autowired
	BusinessAssociateRepository businessAssociateRepository;


	static Logger logger = LoggerFactory.getLogger(CommissionCalculator.class);
	
	public void scheduleProcessingForCommission() throws JsonProcessingException {
		
		logger.info("Inside scheduleProcessingForCommission()");
		
		List<MerchantBalanceSheet> listMerchantBalanceSheet = merchantBalanceSheetRepository.findByPGStatusAndCommission("SUCCESS");
		
		for(MerchantBalanceSheet merchantBalanceSheet :listMerchantBalanceSheet) {
			logger.info(" Details for Pending process :: "+Utility.convertDTO2JsonString(merchantBalanceSheet));
			getCommissionProcessed(merchantBalanceSheet);
		}
		
	}

	public String getCommissionProcessed(MerchantBalanceSheet merchantBalanceSheet) throws JsonProcessingException {

		CommissionStructure calCommissionStructure = new CommissionStructure();
		
		logger.info("TransactionDetails :: " + Utility.convertDTO2JsonString(merchantBalanceSheet));

		List<CommissionStructure> listCommStructure = commissionStructureRepository
				.findByPgIdAndServiceTypeAndMerchantIdOrderByWalletTypeAndBankName(
						merchantBalanceSheet.getTrType().toUpperCase(),
						merchantBalanceSheet.getPgId(),
						merchantBalanceSheet.getMerchantId());

		logger.info("Before :: transactionDetails.getPaymentOption() :: " + merchantBalanceSheet.getTrType().toUpperCase()
				+ " ,transactionDetails.getPgId() :: " + merchantBalanceSheet.getPgId());
		if (listCommStructure.size() == 0) {

			logger.info("Calculate the Commission as Merchant not mapped...");
			logger.info("Info :: "+merchantBalanceSheet.getTrType().toUpperCase() + " , "+merchantBalanceSheet.getPgId()+" , "+merchantBalanceSheet.getMerchantType());
			listCommStructure = commissionStructureRepository.findByPgIdAndServiceTypeAndMerchantTypeOrderByWalletTypeAndBankName(
					merchantBalanceSheet.getTrType().toUpperCase(), 
					merchantBalanceSheet.getPgId(),					
					merchantBalanceSheet.getMerchantType());
		}

		if (listCommStructure.size() == 0) {
			logger.error("No Commission details found for the mentioned Transaction");
		}

		for (CommissionStructure commissionStructure : listCommStructure) {
			logger.info("Inside Card Loop :: " + Utility.convertDTO2JsonString(commissionStructure));
			
			if(commissionStructure.getServiceType().equalsIgnoreCase(PGServices.CARD.toString())) {
				calCommissionStructure = getCommissionIdforCard(
						cardOpsType(merchantBalanceSheet.getTrType().toUpperCase()),
						SecurityUtils.decryptSaveData(merchantBalanceSheet.getCardNumber()).trim(),
						getCardMakerType(SecurityUtils.decryptSaveData(merchantBalanceSheet.getCardNumber()).trim()),
						commissionStructure.getMerchantId(), commissionStructure.getPgId(), merchantBalanceSheet.getMerchantType());
				
				
			}
			if(commissionStructure.getServiceType().equalsIgnoreCase(PGServices.NB.toString())){
				calCommissionStructure = getCommissionIdforNB(
						SecurityUtils.decryptSaveData(merchantBalanceSheet.getPaymentCode()).trim(),											
						commissionStructure.getMerchantId(), 
						commissionStructure.getPgId(), 
						merchantBalanceSheet.getMerchantType());	
			}
			
			if(commissionStructure.getServiceType().equalsIgnoreCase(PGServices.WALLET.toString())){
				calCommissionStructure = getCommissionIdforWallet(
						SecurityUtils.decryptSaveData(merchantBalanceSheet.getPaymentCode().trim()),											
						commissionStructure.getMerchantId(), 
						commissionStructure.getPgId(), 
						merchantBalanceSheet.getMerchantType());
			}
			
			if(calCommissionStructure != null ) {
				
				MerchantBalanceSheet merchantBalanceSheetCal = calculateCommission(calCommissionStructure, merchantBalanceSheet);
				calculateBusinessAssociateCommission(merchantBalanceSheetCal);
				return Utility.convertDTO2JsonString(calCommissionStructure);
			}
		}
		return null;
	}
	
	public MerchantBalanceSheet calculateCommission(CommissionStructure commissionStructure, MerchantBalanceSheet merchantBalanceSheet ) {
		int commissionValuePg = 0 ;
		int commissionValueCust = 0 ;
		
		
		if(commissionStructure.getPgCommissionType().equalsIgnoreCase("FLOATING")) {
			commissionValuePg = ((merchantBalanceSheet.getAmount() * commissionStructure.getPgAmount()) / 100 );
		}
		
		if(commissionStructure.getPgCommissionType().equalsIgnoreCase("FIXED")) {
			commissionValuePg = commissionStructure.getPgAmount();
		}
		
		if(commissionStructure.getAskCommissionType().equalsIgnoreCase("FLOATING")) {
			commissionValueCust = ((merchantBalanceSheet.getAmount() * commissionStructure.getAskAmount()) / 100 );
		}
		
		if(commissionStructure.getAskCommissionType().equalsIgnoreCase("FIXED")) {
			commissionValueCust = commissionStructure.getAskAmount();
		}
		
		merchantBalanceSheet.setPgCommission(commissionValuePg);	
		merchantBalanceSheet.setAskCommission(commissionValueCust);		
		merchantBalanceSheet.setSettleAmountToMerchant(merchantBalanceSheet.getAmount()-(commissionValuePg+commissionValueCust));
		merchantBalanceSheet.setProcessedBy("SYSTEM");
		//merchantBalanceSheetRepository.save(merchantBalanceSheet);
		return merchantBalanceSheet;
		
	}
	
	public String calculateBusinessAssociateCommission(MerchantBalanceSheet merchantBalanceSheet ) throws JsonProcessingException {
		
		int associateComm= 0;
		
		BusinessAssociateCommissionDetails businessAssociateCommissionDetails = new BusinessAssociateCommissionDetails();
		
		BusinessAssociate businessAssociate = businessAssociateRepository.findByMerchantID(merchantBalanceSheet.getMerchantId());
		if(businessAssociate==null) {
			logger.info("No Association found between Merchant and Business Associate");
			
			merchantBalanceSheetRepository.save(merchantBalanceSheet);
			return null;
		}
		logger.info("merchantBalanceSheet :: "+Utility.convertDTO2JsonString(merchantBalanceSheet));
		logger.info("businessAssociate :: "+Utility.convertDTO2JsonString(businessAssociate));
		if(merchantBalanceSheet.getTrType().equalsIgnoreCase(PGServices.CARD.toString())) {
			logger.info(businessAssociate.getUuid() +" , "+ businessAssociate.getMerchantID()+" , "+ merchantBalanceSheet.getTrType().toUpperCase()+" , "+cardOpsType(merchantBalanceSheet.getPaymentMode())+" , "+ UserStatus.ACTIVE.toString());
			businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo.findByUuidAndMerchantIDAndPaymentTypeAndPaymentSubTypeAndStatus(
					businessAssociate.getUuid(), businessAssociate.getMerchantID(), merchantBalanceSheet.getTrType().toUpperCase(), cardOpsType(merchantBalanceSheet.getPaymentMode()), UserStatus.ACTIVE.toString());
		}else {
		 businessAssociateCommissionDetails = businessAssociateCommissionDetailsRepo.findByUuidAndMerchantIDAndPaymentTypeAndStatus(
				 businessAssociate.getUuid(), businessAssociate.getMerchantID(), merchantBalanceSheet.getTrType().toUpperCase(),UserStatus.ACTIVE.toString());
		}
		
		if(businessAssociateCommissionDetails == null) {
			
			logger.info("No Commission define between Merchant and Associate ...");
			return null;
		}
		
		if(businessAssociateCommissionDetails.getCommissionType().equalsIgnoreCase("FLOATING")) {
			associateComm = (int) ((merchantBalanceSheet.getAmount()*businessAssociateCommissionDetails.getCommissionAmount())/100);			
		}else {
			associateComm = (int) (businessAssociateCommissionDetails.getCommissionAmount());		
		}
		
		merchantBalanceSheet.setAssociateCommission(associateComm);
		merchantBalanceSheet.setSettleAmountToMerchant(merchantBalanceSheet.getSettleAmountToMerchant()-associateComm);
		
		merchantBalanceSheetRepository.save(merchantBalanceSheet);
		
		return null;
	}

	public String cardOpsType(String opsType) {

		if (opsType.contains("DEBIT") || opsType.contains("DC")) {
			return "DEBIT";
		} else if (opsType.contains("CREDIT") || opsType.contains("CC")) {
			return "CREDIT";
		}

		return null;
	}

	public String getCardMakerType(String cardNumber) {

		List<String> cards = new ArrayList<String>();
		cards.add(cardNumber);

		String regex = "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" + "(?<mastercard>5[1-5][0-9]{14})|"
				+ "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" + "(?<amex>3[47][0-9]{13})|"
				+ "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" + "(?<rupay>6(?!52[12])(?:011|5[0-9][0-9])[0-9]{12})|"
				+ "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";

		Pattern pattern = Pattern.compile(regex);

		for (String card : cards) {
			card = card.replaceAll("-", "");

			Matcher matcher = pattern.matcher(card);

			if (matcher.matches()) {
				//logger.info("Input Card No :: " + card);
				if (matcher.group("mastercard") != null) {
					//logger.info("Inside MASTER Block:: " + card);
					return "MASTER";
				} else if (matcher.group("visa") != null) {
					//logger.info("Inside VISA Block:: " + card);
					return "VISA";

				} else if (matcher.group("discover") != null) {
					//logger.info("Inside DISCOVER Block:: " + card);
					return "DISCOVER";
				} else if (matcher.group("amex") != null) {
					//logger.info("Inside AMEX Block:: " + card);
					return "AMEX";
				} else if (matcher.group("diners") != null) {
					//logger.info("Inside DINERS Block:: " + card);
					return "DINERS";
				} else if (matcher.group("rupay") != null) {
					//logger.info("Inside RUPAY Block:: " + card);
					return "RUPAY";
				}
			}
		}
		return null;
	}

	public CommissionStructure getCommissionIdforCard(String cardType, String cardSeries, String cardMaker,
			String merchantId, String pgId, String merchant_type) throws JsonProcessingException {

		CommissionStructure commissionStructureOption = new CommissionStructure();

		if (cardType == null || cardType.length()==0 || cardType.trim().length()==0) {
			cardType = "NA";
		}
		if (cardSeries == null || cardSeries.length()==0 || cardSeries.trim().length()==0) {
			cardSeries = "NA";
		}
		if (cardMaker == null || cardMaker.length()==0 || cardMaker.trim().length()==0) {
			cardMaker = "NA";
		}
		if (merchantId == null ) {
			merchantId = "NA";
		}
		if(merchant_type == null ) {
			merchant_type ="NA";
		}
		
		logger.info(
				"pgId :: " + pgId + ",ServiceType :: " + "CARD" + ",merchantId :: " + merchantId + ", cardSeries :: "
						+ cardSeries + ", cardType :: " + cardType + ", cardMaker :: " + cardMaker + ",ACTIVE");

		commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithBin(pgId, PGServices.CARD.toString(),
					merchantId, cardSeries, "ACTIVE",merchant_type);
		

		logger.info("After checkCommissionDetailsWithBin() :: " + Utility.convertDTO2JsonString(commissionStructureOption));

		if (commissionStructureOption == null) {
			commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithCardTypeAndCardMaker(
					pgId, "CARD", merchantId, cardType, cardMaker, "ACTIVE",merchant_type);
		}
		logger.info("After checkCommissionDetailsWithCardTypeAndCardMaker() :: "
				+ Utility.convertDTO2JsonString(commissionStructureOption));

		if (commissionStructureOption == null) {
			commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithCardMaker(pgId, PGServices.CARD.toString(),
					merchantId, cardMaker, "ACTIVE",merchant_type);
		}
		logger.info("After checkCommissionDetailsWithCardMaker() :: "
				+ Utility.convertDTO2JsonString(commissionStructureOption));

		if (commissionStructureOption == null) {
			commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithCardType(pgId, PGServices.CARD.toString(),
					merchantId, cardType, "ACTIVE",merchant_type);
		}
		logger.info("After checkCommissionDetailsWithCardMaker() :: "
				+ Utility.convertDTO2JsonString(commissionStructureOption));

		return commissionStructureOption;
	}
	
	public CommissionStructure getCommissionIdforNB(String paymentCode,	String merchantId, String pgId, String merchant_type) throws JsonProcessingException {
		
		CommissionStructure commissionStructureOption = new CommissionStructure();

		if (paymentCode == null || paymentCode.length()==0 || paymentCode.trim().length()==0) {
			paymentCode = "NA";
		}
		if (merchantId == null ) {
			merchantId = "NA";
		}
		if(merchant_type == null ) {
			merchant_type ="NA";
		}
		
		logger.info("pgId :: " + pgId +  ",merchantId :: " + merchantId + ", merchant_type :: " +merchant_type+ " ,Payment Code :: "+paymentCode);

		commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithNB(pgId, PGServices.NB.toString(),merchantId, paymentCode, UserStatus.ACTIVE.toString(),merchant_type);
		logger.info("After checkCommissionDetailsWithNB() :: " + Utility.convertDTO2JsonString(commissionStructureOption));
		if(commissionStructureOption == null) {
			commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithNB(pgId, PGServices.NB.toString(),merchantId,"NA", UserStatus.ACTIVE.toString(),merchant_type);
		}
		
		return commissionStructureOption;
	}
	
	public CommissionStructure getCommissionIdforWallet(String paymentCode,	String merchantId, String pgId, String merchant_type) throws JsonProcessingException {
		
		CommissionStructure commissionStructureOption = new CommissionStructure();

		if (paymentCode == null || paymentCode.length()==0 || paymentCode.trim().length()==0) {
			paymentCode = "NA";
		}
		if (merchantId == null ) {
			merchantId = "NA";
		}
		if(merchant_type == null ) {
			merchant_type ="NA";
		}
		
		logger.info("pgId :: " + pgId +  ",merchantId :: " + merchantId + " ,Payment Code :: "+paymentCode +" , merchant_type :: "+merchant_type + " , ");

		commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithWallet(pgId, PGServices.WALLET.toString(),merchantId, paymentCode.trim(), UserStatus.ACTIVE.toString(),merchant_type);
		logger.info("After getCommissionIdforWallet() :: " + Utility.convertDTO2JsonString(commissionStructureOption));
		if(commissionStructureOption == null) {
			commissionStructureOption = commissionStructureRepository.checkCommissionDetailsWithWallet(pgId, PGServices.WALLET.toString(),merchantId,"NA", UserStatus.ACTIVE.toString(),merchant_type);
			logger.info("After getCommissionIdforWallet() without PaymentCode :: " + Utility.convertDTO2JsonString(commissionStructureOption));
		}
		
		return commissionStructureOption;
	}

}
