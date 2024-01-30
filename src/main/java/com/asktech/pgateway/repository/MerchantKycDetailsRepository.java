package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantKycDetails;


public interface MerchantKycDetailsRepository extends JpaRepository<MerchantKycDetails, String>{

	MerchantKycDetails findByMerchantID(String merchantId);
	
}
