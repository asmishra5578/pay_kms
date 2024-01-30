package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.model.RefundDetails;

public interface RefundDetailsRepository extends JpaRepository<RefundDetails, String> {

	List<RefundDetails> findByMerchantId(String merchantId); 
	
	RefundDetails findByMerchantIdAndMerchantOrderIdAndStatus(String merchantId, String orderId, String string);

	@Query(value = "select * from refund_details where merchant_order_id= :merchantOrderId ",
			nativeQuery = true)
	RefundDetails getAllRefundByMerchantOrderId(@Param("merchantOrderId") String merchantOrderId);
}
