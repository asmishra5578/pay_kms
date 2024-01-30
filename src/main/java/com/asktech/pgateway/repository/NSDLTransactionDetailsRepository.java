package com.asktech.pgateway.repository;

import com.asktech.pgateway.model.NSDLTransactionDetails;

import org.springframework.data.jpa.repository.JpaRepository;


public interface NSDLTransactionDetailsRepository extends JpaRepository<NSDLTransactionDetails, String> {

	NSDLTransactionDetails findByOrderId(String orderId);
}
