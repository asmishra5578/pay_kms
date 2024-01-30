package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantKycDocuments;


public interface MerchantKycDocRepo extends JpaRepository<MerchantKycDocuments, String>{
	
}
