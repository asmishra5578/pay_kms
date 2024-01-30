package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantPGDetails;

public interface MerchantPGDetailsRepository extends JpaRepository<MerchantPGDetails, String>{

	MerchantPGDetails findByMerchantID(String merchantId);

	MerchantPGDetails findByMerchantIDAndId(String merchantId, long pgId);

	MerchantPGDetails findByMerchantPGNme(String merchantPGNme);

}
