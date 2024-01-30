package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.PayGTransactionDetails;

public interface PayGTransactionDetailsRepository extends JpaRepository<PayGTransactionDetails, String>{

	PayGTransactionDetails findByPayGOrderKeyId(String orderId);

	PayGTransactionDetails findByPaygUniqueId(String orderId);

    PayGTransactionDetails findByOrderId(String orderId);

}
