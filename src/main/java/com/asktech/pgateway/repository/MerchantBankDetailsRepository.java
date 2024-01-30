package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantBankDetails;

public interface MerchantBankDetailsRepository extends JpaRepository<MerchantBankDetails, String>{

	MerchantBankDetails findByMerchantID(String merchantID);

}
