package com.asktech.pgateway.repository.payout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.payout.IMerchantWalletDetails;
import com.asktech.pgateway.model.payout.MerchantBeneficiaryDetails;

public interface MerchantBeneficiaryDetailsRepo extends JpaRepository<MerchantBeneficiaryDetails, String>{

	@Query(value = "select merchantId,vanAccountNumber,walletGuuid from payout.MerchantWalletDetails "			
			+ "where merchantId =:merchantId",
			nativeQuery = true)
	public IMerchantWalletDetails getMerchatWalletDetails(@Param("merchantId") String merchantId) ;

	public MerchantBeneficiaryDetails findByBeneficiaryAccountId(String beneficiaryAccountNo);
	
	public MerchantBeneficiaryDetails findByBeneficiaryAccountIdAndBeneficiaryIFSCCode(String beneficiaryAccountNo,String beneficiaryIFSCCode);

	public MerchantBeneficiaryDetails findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndMerchantId(String beneficiaryAccountNo,
			String beneficiaryIFSCCode, String merchnatId);

	public MerchantBeneficiaryDetails findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndStatus(
			String beneficiaryAccountNo, String beneficiaryIFSCCode, String string);
	
	public MerchantBeneficiaryDetails findByMerchantOrderId(String merchantOrderId);

	public MerchantBeneficiaryDetails findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndMerchantIdAndStatus(
			String beneficiaryAccountNo, String beneficiaryIFSCCode, String merchantID, String string);
	

}
