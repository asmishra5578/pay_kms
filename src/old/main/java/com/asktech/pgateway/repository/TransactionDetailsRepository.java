package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.model.TransactionDetails;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, String>{

	TransactionDetails findByOrderIDAndStatus(String string, String string2);
	List<TransactionDetails> findAllByMerchantId(long id);

	@Query(value = "select tr.id,tr.created,tr.updated,tr.amount,tr.merchant_id,tr.orderid,tr.payment_option,tr.pg_orderid,tr.pg_type,tr.userid,tr.status,tr.payment_mode,tr.txt_msg,tr.txtpgtime"
			+ " from transaction_details tr where merchant_id= :merchant_id and created >=DATE_ADD(CURDATE(), INTERVAL -3 DAY)",
			nativeQuery = true)
	public List<TransactionDetails> findLast3DaysTransaction(@Param("merchant_id") String merchant_id);

}
