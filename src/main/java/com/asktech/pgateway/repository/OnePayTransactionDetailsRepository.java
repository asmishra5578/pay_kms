package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.OnePayTransactionDetails;

public interface OnePayTransactionDetailsRepository extends JpaRepository<OnePayTransactionDetails, String> {

	OnePayTransactionDetails findByMerchantOrderIdAndOrderId(String merchantOrderId, String orderID);

}
