package com.asktech.pgateway.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.asktech.pgateway.dto.merchant.MerchantDashBoardBalance;

@Repository
@Transactional
public interface MerchantDashBoardBalanceRepository extends PagingAndSortingRepository<MerchantDashBoardBalance, String>{

	@Query(value = "select tr.*"
			+ " from merchant_dashboard_details tr where merchant_id= :merchant_id ",
			nativeQuery = true)
	public List<MerchantDashBoardBalance> findDashBoardDetails(@Param("merchant_id") String merchant_id);

}
