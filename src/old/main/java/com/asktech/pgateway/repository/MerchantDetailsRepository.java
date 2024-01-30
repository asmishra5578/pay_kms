package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantDetails;

public interface MerchantDetailsRepository extends JpaRepository<MerchantDetails, String>{

	MerchantDetails findByMerchantName(String merchantName);

	MerchantDetails findByAppID(String encryptCardNumberOrExpOrCvv);

	MerchantDetails findByMerchantEMail(String userNameOrEmail);

	MerchantDetails findByuuid(String uuid);

}
