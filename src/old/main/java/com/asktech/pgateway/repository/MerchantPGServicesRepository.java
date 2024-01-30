package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.MerchantPGServices;

public interface MerchantPGServicesRepository extends JpaRepository<MerchantPGServices, String>{

	MerchantPGServices findByMerchantIDAndService(String merchantId, String service);

}
