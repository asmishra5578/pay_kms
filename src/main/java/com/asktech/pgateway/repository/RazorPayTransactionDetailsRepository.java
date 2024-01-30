package com.asktech.pgateway.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.RazorPayTransactionDetails;

public interface RazorPayTransactionDetailsRepository extends JpaRepository<RazorPayTransactionDetails, String> {

	RazorPayTransactionDetails findByRazorPayOrderId(String razorPayOrderId);
	RazorPayTransactionDetails findByOrderId(String orderId);
	//List<RazorPayTransactionDetails> findByOrderIdAndMerchantOrderId(String orderID, String merchantOrderId);

}
