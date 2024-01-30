package com.asktech.pgateway.util.payout;

import java.text.ParseException;

import org.springframework.stereotype.Service;

import com.asktech.pgateway.dto.payout.beneficiary.AssociateBankDetails;
import com.asktech.pgateway.dto.payout.beneficiary.CreateBeneficiaryRequest;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.payout.MerchantBeneficiaryDetails;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PayoutWalletUtilityServices {

	public MerchantBeneficiaryDetails populateMerchnatBeneficiary(MerchantDetails user, 
			CreateBeneficiaryRequest createBeneficiaryRequest, String walletId) throws JsonProcessingException, ParseException {

		MerchantBeneficiaryDetails merchantBeneficiaryDetails = new MerchantBeneficiaryDetails();
		merchantBeneficiaryDetails.setAccountValidationFlag("F");
		merchantBeneficiaryDetails.setBeneficiaryAccountId(createBeneficiaryRequest.getBeneficiaryAccountNo());
		merchantBeneficiaryDetails.setBeneficiaryBankName(createBeneficiaryRequest.getBeneficiaryBankName());
		merchantBeneficiaryDetails.setBeneficiaryIFSCCode(createBeneficiaryRequest.getBeneficiaryIFSCCode());
		merchantBeneficiaryDetails.setBeneficiaryMICRCode(createBeneficiaryRequest.getBeneficiaryMICRCode());
		merchantBeneficiaryDetails.setBeneficiaryName(createBeneficiaryRequest.getBeneficiaryName());
		merchantBeneficiaryDetails.setDisBruseMentWalletId(walletId);
		merchantBeneficiaryDetails.setMerchantId(user.getMerchantID());
		merchantBeneficiaryDetails.setCreatedBy(user.getUuid());
		merchantBeneficiaryDetails.setStatus(UserStatus.ACTIVE.toString());
		merchantBeneficiaryDetails.setRequestData(Utility.convertDTO2JsonString(createBeneficiaryRequest));
		merchantBeneficiaryDetails.setOrderId(Utility.getRandomId());
		merchantBeneficiaryDetails.setMerchantOrderId(createBeneficiaryRequest.getMerchantOrderId());
		
		return merchantBeneficiaryDetails;
	}
	
	public MerchantBeneficiaryDetails associateMerchnatBeneficiary(MerchantDetails user,AssociateBankDetails associateBankDetails,
			MerchantBeneficiaryDetails merchantBeneficiaryDetails, String walletId) throws ParseException, JsonProcessingException {
		
		MerchantBeneficiaryDetails merchantBeneficiaryDetailsAssoc = new MerchantBeneficiaryDetails();
		merchantBeneficiaryDetailsAssoc.setAccountValidationFlag(merchantBeneficiaryDetails.getAccountValidationFlag());
		merchantBeneficiaryDetailsAssoc.setBeneficiaryAccountId(merchantBeneficiaryDetails.getBeneficiaryAccountId());
		merchantBeneficiaryDetailsAssoc.setBeneficiaryBankName(merchantBeneficiaryDetails.getBeneficiaryBankName());
		merchantBeneficiaryDetailsAssoc.setBeneficiaryIFSCCode(merchantBeneficiaryDetails.getBeneficiaryIFSCCode());
		merchantBeneficiaryDetailsAssoc.setBeneficiaryMICRCode(merchantBeneficiaryDetails.getBeneficiaryMICRCode());
		merchantBeneficiaryDetailsAssoc.setBeneficiaryName(merchantBeneficiaryDetails.getBeneficiaryName());
		merchantBeneficiaryDetailsAssoc.setCreatedBy(user.getUuid());
		merchantBeneficiaryDetailsAssoc.setDisBruseMentWalletId(walletId);
		merchantBeneficiaryDetailsAssoc.setMerchantId(user.getMerchantID());
		merchantBeneficiaryDetailsAssoc.setMerchantOrderId(associateBankDetails.getMerchantOrderId());
		merchantBeneficiaryDetailsAssoc.setOrderId(Utility.getRandomId());
		merchantBeneficiaryDetailsAssoc.setRequestData(Utility.convertDTO2JsonString(associateBankDetails));
		merchantBeneficiaryDetailsAssoc.setStatus(UserStatus.ACTIVE.toString());
		return merchantBeneficiaryDetailsAssoc;
	}
}
