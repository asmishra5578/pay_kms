package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.BusinessAssociate;

public interface BusinessAssociateRepository extends JpaRepository<BusinessAssociate, String>{

	BusinessAssociate findByMerchantID(String merchantId);

	BusinessAssociate findByUuid(String busiAssociateuuid);

	BusinessAssociate findByUuidAndMerchantID(String busiAssociateuuid, String merchantId);
	
}
