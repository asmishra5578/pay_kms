package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.EaseBuzzTransactionDetails;

public interface EaseBuzzTransactionDetailsRepository extends JpaRepository<EaseBuzzTransactionDetails, String> {

		EaseBuzzTransactionDetails findByMerchantOrderIdAndOrderId(String merchantOrderId, String orderID);

}
