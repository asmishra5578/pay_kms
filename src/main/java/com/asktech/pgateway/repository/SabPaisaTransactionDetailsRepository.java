package com.asktech.pgateway.repository;

import com.asktech.pgateway.model.SabPaisaTransactionDetails;

import org.springframework.data.jpa.repository.JpaRepository;



public interface SabPaisaTransactionDetailsRepository extends JpaRepository<SabPaisaTransactionDetails, String> {

	SabPaisaTransactionDetails findByTxnId(String orderId);

}
