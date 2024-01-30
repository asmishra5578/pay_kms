package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.dto.merchant.MerchantDashBoardBalance;
import com.asktech.pgateway.model.MerchantBalanceSheet;

public interface MerchantBalanceSheetRepository extends JpaRepository<MerchantBalanceSheet, String>{

	@Query(value = "select bs.*"
			+ " from merchant_balance_sheet bs where merchant_id= :merchant_id and settlement_status='COMPLETED' and created >=DATE_ADD(CURDATE(), INTERVAL -7 DAY)",
			nativeQuery = true)
	public List<MerchantBalanceSheet> findLast7DaysSettleTransaction(@Param("merchant_id") String merchant_id);

	public List<MerchantBalanceSheet> findAllByMerchantIdAndSettlementStatus(String merchantID2, String string);

	@Query(value = "select  merchant_id,settlement_status,sum(amount) amount "
			+ "from merchant_balance_sheet where merchant_id=:merchant_id "
			+ "group by merchant_id,settlement_status",
			nativeQuery = true)
	public List<MerchantDashBoardBalance> getDashboardStauts(@Param("merchant_id") String merchant_id );
}
