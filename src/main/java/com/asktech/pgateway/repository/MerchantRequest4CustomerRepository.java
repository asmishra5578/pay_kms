package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.ICustomerAPIReport;
import com.asktech.pgateway.model.MerchantRequest4Customer;

public interface MerchantRequest4CustomerRepository extends JpaRepository<MerchantRequest4Customer, String> {

	
	@Query(value = "select a.orderid,a.created,a.amount,a.cust_email custEmail,a.cust_name custName,a.cust_phone custPhone, "
			+ "a.link_customer linkCustomer,a.link_expiry_time linkExpiryTime,a.ordernote,a.return_url returnUrl,a.status status, "
			+ "b.status transactionStatus from merchant_request4customer a "
			+ "left OUTER JOIN transaction_details b "
			+ "on a.orderid = b.merchant_order_id where a.merchant_id=:merchantId "
			+ "and a.source = :source "
			+ "order by a.id",
			nativeQuery = true)
	public List<ICustomerAPIReport> getCustomerRequestReport(@Param("merchantId") String merchantId,@Param("source") String source) ;

	public MerchantRequest4Customer findByOrderIdAndMerchantId(String orderId, String merchantID);

}
