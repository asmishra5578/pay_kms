package com.asktech.pgateway.util;

import java.util.Objects;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.payout.beneficiary.AssociateBankDetails;
import com.asktech.pgateway.dto.payout.beneficiary.CreateBeneficiaryRequest;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.ValidationExceptions;

public class ValidationUtils implements ErrorValues{

	/*
	 * public boolean validateMerchantCreateRequest(MerchantCreateRequest
	 * merchantCreateRequest) {
	 * 
	 * 
	 * 
	 * return false; }
	 */
	
	public static boolean validateBeneficiaryAddRequest(CreateBeneficiaryRequest createBeneficiaryRequest) throws ValidationExceptions {
		
		if(Objects.isNull(createBeneficiaryRequest.getBeneficiaryAccountNo())
				|| Objects.isNull(createBeneficiaryRequest.getBeneficiaryBankName()) 
				|| Objects.isNull(createBeneficiaryRequest.getBeneficiaryIFSCCode())
				|| Objects.isNull(createBeneficiaryRequest.getBeneficiaryName())
				|| Objects.isNull(createBeneficiaryRequest.getMerchantOrderId())) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED_E0201, FormValidationExceptionEnums.E0201);
		}
		
		if(!Utility.validateIFSCCode(createBeneficiaryRequest.getBeneficiaryIFSCCode())){
			throw new ValidationExceptions(IFSC_CODE_VALIDATION_FAILED_E0202, FormValidationExceptionEnums.E0202);
		}
		if(!Utility.checkNumericValue(createBeneficiaryRequest.getBeneficiaryAccountNo())) {
			throw new ValidationExceptions(BANK_ACCOUNT_VALIDATION_FAILED_E0203, FormValidationExceptionEnums.E0203);
		}
		
		return true;
	}
	
	public static boolean validateBeneficiaryAssocRequest(AssociateBankDetails associateBankDetails) throws ValidationExceptions {
		
		if(Objects.isNull(associateBankDetails.getBeneficiaryAccountNo())				
				|| Objects.isNull(associateBankDetails.getBeneficiaryIFSCCode())				
				|| Objects.isNull(associateBankDetails.getMerchantOrderId())) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED_E0201, FormValidationExceptionEnums.E0201);
		}
		if(!Utility.validateIFSCCode(associateBankDetails.getBeneficiaryIFSCCode())){
			throw new ValidationExceptions(IFSC_CODE_VALIDATION_FAILED_E0202, FormValidationExceptionEnums.E0202);
		}
		if(!Utility.checkNumericValue(associateBankDetails.getBeneficiaryAccountNo())) {
			throw new ValidationExceptions(BANK_ACCOUNT_VALIDATION_FAILED_E0203, FormValidationExceptionEnums.E0203);
		}
		return true;
	}
}
