package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.ICustomerPgTransaction;
import com.asktech.pgateway.customInterface.IMerchantTransaction;
import com.asktech.pgateway.customInterface.ITransactionCustomerWise;
import com.asktech.pgateway.model.TransactionDetails;

public interface  TransactionDetailsRepository extends JpaRepository<TransactionDetails, String>{

	TransactionDetails findByOrderIDAndStatus(String string, String string2);
	
	List<TransactionDetails> findAllByMerchantId(String id);
	
	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where merchant_id= :merchant_id order by tr.id desc limit 100",
			nativeQuery = true)
	List<TransactionDetails> findAllTopByMerchantId(@Param("merchant_id") String merchant_id);

	@Query(value = "select tr.id, tr.created, tr.updated, tr.amount, tr.card_number, tr.cust_order_id, tr.merchant_id, tr.merchant_order_id, tr.merchant_returnurl, tr.orderid, tr.payment_code, tr.payment_mode, tr.payment_option, tr.pg_id, tr.pg_orderid, tr.pg_type, tr.status, tr.txt_msg, tr.txtpgtime, tr.userid, tr.vpaupi"
			+ " from transaction_details tr where merchant_id= :merchant_id and created >=DATE_ADD(CURDATE(), INTERVAL -3 DAY) limit 40000",
			nativeQuery = true)
	public List<TransactionDetails> findLast3DaysTransaction(@Param("merchant_id") String merchant_id);
	
	
	@Query(value = "select tr.* "
			+ "from transaction_details tr "
			+ "where tr.merchant_id= :merchant_id "
			+ "and date(tr.created) between :dateFrom and :dateTo order by id desc limit 50000",
			nativeQuery = true)
	public List<TransactionDetails> getTransactionDateRange(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo );

	@Query(value = "select tr.* "
			+ "from transaction_details tr "
			+ "where tr.merchant_id= :merchant_id "
			+ "and date(tr.created) = :dateFrom  order by id desc limit 50000",
			nativeQuery = true)
	public List<TransactionDetails> getTransactionDate(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom  );

	
	@Query(value = "select tr.* "
			+ "from transaction_details tr "
			+ "where tr.merchant_id= :merchant_id "
			+ "and date(tr.created) = :dateFrom  and status = :status order by id desc limit 50000",
			nativeQuery = true)
	public List<TransactionDetails> getTransactionDateAndStatus(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom, @Param("status") String status  );
	
	@Query(value = "select tr.* "
			+ "from transaction_details tr "
			+ "where tr.merchant_id= :merchant_id "
			+ "and date(tr.created) between :dateFrom and :dateTo and status = :status order by id desc limit 50000",
			nativeQuery = true)
	public List<TransactionDetails> getTransactionDateRangeAndStatus(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo, @Param("status") String status );

	
	TransactionDetails findByOrderID(String string);
	
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate()-1 "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id,a.pg_type,a.status limit 50000", nativeQuery = true)
	List<IMerchantTransaction> getYesterdayTrDetails(@Param("merchant_id") String merchant_id  );
	
	@Query(value = "select a.merchant_id as merchantId, status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate() "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id, a.status", nativeQuery = true)
	List<IMerchantTransaction> getTodayTrDetails(@Param("merchant_id") String merchant_id  );
	
	@Query(value = "select sum(a.amount)/100 amt from transaction_details a where date(a.created) = date(curdate()) and a.merchant_id= :merchantid", nativeQuery = true)
	String getTodayTr(@Param("merchantid") String merchantid );
	
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount  "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getLastMonthTrDetails(@Param("merchant_id") String merchant_id  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE()) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE()) "
			+ "and b.merchantid= :merchant_id "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getCurrMonthTrDetails(@Param("merchant_id") String merchant_id  );
	List<TransactionDetails> findAllByMerchantOrderId(String string);
	List<TransactionDetails> findAllByMerchantOrderIdAndMerchantId(String string, String merchantID);

	List<TransactionDetails> findAllByMerchantOrderIdAndMerchantIdAndStatus(String string, String merchantID, String status);

	List<TransactionDetails> findByPgOrderID(String txtId);

	Page<TransactionDetails> findByMerchantIdContaining(String merchantID, Pageable paging);

	Page<TransactionDetails> findByMerchantIdAndStatusContaining(String merchantID, String status, Pageable paging);

	List<TransactionDetails> findByMerchantIdAndStatus(String merchantID, String status);
	
	@Query(value = "SELECT a.created created, a.amount amount, a.merchant_order_id merchant_order_id, a.orderid orderid, a.payment_mode payment_mode, a.payment_option payment_option, a.status status, a.txt_msg txt_msg, a.userid userid,  b.customer_name customer_name, b.email_id email_id, b.phone_number phone_number"
			+ " FROM transaction_details a, user_details b WHERE a.userid = b.id and date(a.created) between :fromDate and :toDate and a.merchant_id = :merchant_id", nativeQuery = true)
	List<ITransactionCustomerWise> getTransactionDetailsWithCustomer(@Param("merchant_id") String merchant_id, @Param("toDate") String todate, @Param("fromDate") String fromDate );


	@Query(value ="update transaction_details set status = 'refunded' where merchant_id= :merchantId and merchant_order_id= :orderId ", nativeQuery = true)
    public void changeTransactionStatusForRefund(String merchantId,
			@Param("orderId") String orderId);
	
    @Query(value = "SELECT tab1.created trx_initiation, tab1.amount, tab1.order_id, \r\n"
    		+ "tab1.user_email, tab1.user_name, tab1.user_phone, tab1.status initial_status,\r\n"
    		+ "tab1.devicetype, tab1.ipaddress ,\r\n"
    		+ "tab2.created submit_time, tab2.orderid pgOrderId, tab2.payment_option, tab2.status trx_status, \r\n"
    		+ "tab2.txt_msg, tab2.payment_code, tab2.merchant_alerturl, tab2.order_note \r\n"
    		+ "FROM pgdbkmsn.customer_request tab1 \r\n"
    		+ "LEFT JOIN pgdbkmsn.transaction_details tab2 \r\n"
    		+ "ON tab1.order_id = tab2.merchant_order_id \r\n"
    		+ "where tab1.merchant_id= :merchantId and date(tab1.created) between :dateTo and :dateFrom order by tab1.id desc limit 5000", nativeQuery = true)
	 List<ICustomerPgTransaction> customerPgTransaction(String merchantId, String dateTo, String dateFrom);
	
}
