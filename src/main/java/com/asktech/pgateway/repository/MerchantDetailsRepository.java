package com.asktech.pgateway.repository;

import com.asktech.pgateway.model.MerchantDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MerchantDetailsRepository extends JpaRepository<MerchantDetails, String>{

	MerchantDetails findByMerchantName(String merchantName);

	MerchantDetails findByAppID(String encryptCardNumberOrExpOrCvv);
	
	MerchantDetails findByAppIDAndSecretId(String encryptCardNumberOrExpOrCvv, String secretkey);

	MerchantDetails findByMerchantEMail(String userNameOrEmail);

	MerchantDetails findByuuid(String uuid);	

	MerchantDetails findByMerchantID(String merchantId);

	MerchantDetails findByPhoneNumber(String phoneNumber);
	
	@Query(value = "select * from merchant_details  where merchantid= :merchantId and user_status = 'BLOCKED' and date_add(updated, interval 10 minute)<date_add(current_timestamp(), interval 330 minute) "
			+ "", nativeQuery= true)
	MerchantDetails getBlockedUserActive(String merchantId);
}
