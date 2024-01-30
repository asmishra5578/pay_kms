package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asktech.pgateway.model.LetzpayTransactionDetails;

public interface LetzpayTransactionDetailsRepository extends JpaRepository<LetzpayTransactionDetails, String>{

	@Query(value = "select * "
			+ " from letzpay_transaction_details tr "
			+ " where created between (NOW() - INTERVAL 40000 MINUTE) and (NOW() - INTERVAL 5 MINUTE) order by id ",
			nativeQuery = true)
	List<LetzpayTransactionDetails> getAllRecords();

	List<LetzpayTransactionDetails> findByOrderId(String orderID);

}
