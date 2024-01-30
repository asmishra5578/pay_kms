package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.IMerchantList;
import com.asktech.pgateway.customInterface.IMerchantStatus;
import com.asktech.pgateway.customInterface.IMerchantTransaction;
import com.asktech.pgateway.model.MerchantDetails;

public interface MerchantDetailsAddRepository extends JpaRepository<MerchantDetails, String>{

	@Query(value = "select ms.user_status as status,ms.created_by as adminUUid,count(ms.user_status) as merchantCount from merchant_details ms "
			+ "where created_by= :uuid "
			+ "group by user_status,created_by", nativeQuery = true)
	List<IMerchantStatus> countTotalMerchantDetailsByUserStatusAndCreatedBy(@Param("uuid") String uuid );
	
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate()-1 "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getYesterdayTrDetails(@Param("uuid") String uuid  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and date(a.created) = curdate() "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getTodayTrDetails(@Param("uuid") String uuid  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount  "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getLastMonthTrDetails(@Param("uuid") String uuid  );
	
	@Query(value = "select a.merchant_id as merchantId,a.pg_type as pgType ,status as status,sum(a.amount) as amount "
			+ "from transaction_details a, merchant_details b "
			+ "where b.merchantid = a.merchant_id "
			+ "and MONTH(a.created) = MONTH(CURRENT_DATE()) "
			+ "and YEAR(a.created) = YEAR(CURRENT_DATE()) "
			+ "and b.created_by= :uuid "
			+ "group by a.merchant_id,a.pg_type,a.status", nativeQuery = true)
	List<IMerchantTransaction> getCurrMonthTrDetails(@Param("uuid") String uuid  );
	
	@Query(value="SELECT a.merchantemail, a.merchantid, a.merchant_name, a.phone_number, a.user_status, a.kyc_status FROM merchant_details a where a.created_by = :created_by", nativeQuery = true)
	List<IMerchantList> getCompleteMerchantList(@Param("created_by") String created_by);
}
