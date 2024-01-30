package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantPGDetails;

public interface MerchantPGDetailsRepository extends JpaRepository<MerchantPGDetails, String>{

	MerchantPGDetails findByMerchantID(String merchantId);

	MerchantPGDetails findByMerchantIDAndId(String merchantId, long pgId);

	//MerchantPGDetails findByMerchantPGName(String merchantPGNme);

	//MerchantPGDetails findByMerchantIDAndMerchantPGName(String merchantID, String merchantPGNme);

	//List<MerchantPGDetails> findAllByMerchantPGName(String merchantPGNme);

	List<MerchantPGDetails> findAllByMerchantPGId(String pgUuid);

	MerchantPGDetails findByMerchantIDAndMerchantPGId(String merchantID, String pgUuid);

	MerchantPGDetails findByMerchantPGId(String pgUuid);
	

}
