package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.IMerchantSettlement;
import com.asktech.pgateway.customInterface.IMerchantWiseDateWiseSettlement;
import com.asktech.pgateway.customInterface.ISettlementBalanceReport;
import com.asktech.pgateway.customInterface.IsettlementFilter;
import com.asktech.pgateway.dto.merchant.MerchantDashBoardBalance;
import com.asktech.pgateway.model.MerchantBalanceSheet;

public interface MerchantBalanceSheetRepository extends JpaRepository<MerchantBalanceSheet, String> {

	@Query(value = "select bs.*"
			+ " from merchant_balance_sheet bs where merchant_id= :merchant_id and settlement_status='COMPLETED' and created >=DATE_ADD(CURDATE(), INTERVAL -7 DAY)", nativeQuery = true)
	public List<MerchantBalanceSheet> findLast7DaysSettleTransaction(@Param("merchant_id") String merchant_id);

	public List<MerchantBalanceSheet> findAllByMerchantIdAndSettlementStatus(String merchantID2, String string);

	@Query(value = "select created, amount, settle_amount_to_merchant,  merchant_id, merchant_order_id, settlement_status,tr_type, card_number, payment_code, "
			+ "vpaupi from merchant_balance_sheet where settlement_status = :settlement_status and  merchant_id = :merchant_id and pg_status in ('SUCCESS', 'Captured') limit 50000", nativeQuery = true)
	public List<ISettlementBalanceReport> getSettlementBalanceSheet(
			@Param("settlement_status") String settlement_status, @Param("merchant_id") String merchant_id);

	@Query(value = "SELECT sum(amount)/100 amt FROM merchant_balance_sheet where merchant_id = :merchant_id and settlement_status = 'PENDING' and pg_status in ('SUCCESS', 'Captured')", nativeQuery = true)
	public String getPendingSettlementTotal(@Param("merchant_id") String merchant_id);
	
	@Query(value = "SELECT (SUM(settle_amount_to_merchant))/100 amt FROM merchant_balance_sheet where merchant_id = :merchant_id and settlement_status = 'SETTLED' group by date(created) order by date(created) desc limit 1", nativeQuery = true)
	public String getSettledTotal(@Param("merchant_id") String merchant_id);

	
	@Query(value = "SELECT tr.* FROM merchant_balance_sheet tr where tr.merchant_id = :merchant_id and settlement_status = 'SETTLED' and pg_status in ('SUCCESS', 'Captured') and date(tr.created) between :dateFrom and :dateTo  order by id desc limit 50000;",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementDateRange(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo );	
	
	
	@Query(value = "SELECT tr.merchant_id, tr.merchant_order_id, ROUND(tr.amount/100, 2) amount, tr.created transaction_date, tr.order_id, tr.pg_status, ifnull(tr.settlement_status, 'PENDING') settlement_status, ifnull(ROUND(tr.ask_commission/118, 2),0) service_charge, ifnull(ROUND(ROUND(tr.ask_commission/118, 2) * 0.18, 2),0) tax, ifnull(round(tr.settle_amount_to_merchant/100,2),0) settled_amt, tr.settlement_date, tr.tr_type FROM merchant_balance_sheet tr where tr.merchant_id = :merchant_id and settlement_status = 'SETTLED' and  date(tr.settlement_date) between :dateFrom and :dateTo  limit 50000;",
			nativeQuery = true)
	public List<IsettlementFilter> getSettlementDateRangeCalc(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo );	
	
	
	@Query(value = "SELECT tr.* FROM merchant_balance_sheet tr where tr.merchant_id = :merchant_id and settlement_status = 'SETTLED' and pg_status in ('SUCCESS', 'Captured') and date(tr.created) = :dateFrom order by id desc limit 50000;",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementFrom(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom);	
	
	
	@Query(value = "SELECT tr.merchant_id, tr.merchant_order_id, ROUND(tr.amount/100, 2) amount, tr.created transaction_date, tr.order_id, tr.pg_status, ifnull(tr.settlement_status, 'PENDING') settlement_status, ifnull(ROUND(tr.ask_commission/118, 2),0) service_charge, ifnull(ROUND(ROUND(tr.ask_commission/118, 2) * 0.18, 2),0) tax, ifnull(round(tr.settle_amount_to_merchant/100,2),0) settled_amt, tr.settlement_date, tr.tr_type FROM merchant_balance_sheet tr where tr.merchant_id = :merchant_id and settlement_status = 'SETTLED' and date(tr.settlement_date) = :dateFrom ;",
			nativeQuery = true)
	public List<IsettlementFilter> getSettlementFromCalc(@Param("merchant_id") String merchant_id , @Param("dateFrom") String dateFrom);	
	
	
	@Query(value = "select  merchant_id,settlement_status,sum(amount) amount "
			+ "from merchant_balance_sheet where merchant_id=:merchant_id "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	public List<MerchantDashBoardBalance> getDashboardStauts(@Param("merchant_id") String merchant_id);

	public MerchantBalanceSheet findByOrderId(String orderid);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) = curdate()-1 "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLastDaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) = curdate() "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getCurrDaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) >= curdate() -7 "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLast7DaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' "
			+ "and MONTH(created) = MONTH(CURRENT_DATE()) " + "and YEAR(created) = YEAR(CURRENT_DATE()) "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getCurrMonthSettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' "
			+ "and MONTH(created) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "and YEAR(created) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLastMonthSettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select merchant_id merchantId,settlement_status status,sum(settle_amount_to_merchant) amount from merchant_balance_sheet "
			+ "where merchant_id = :merchant_id " + "and pg_status = 'SUCCESS' " + "and date(created) >= curdate() -90 "
			+ "group by merchant_id,settlement_status", nativeQuery = true)
	List<IMerchantSettlement> getLast90DaySettlement(@Param("merchant_id") String merchant_id);

	@Query(value = "select * from merchant_balance_sheet "
			+ "where pg_status = 'SUCCESS' "
			+ "and ask_commission is null and pg_commission is null and settle_amount_to_merchant is null  ", nativeQuery = true)
	List<MerchantBalanceSheet> findByPGStatusAndCommission(@Param("pg_status") String pg_status  );
	
	@Query(value = "select * from merchant_balance_sheet "
			+ "where merchant_id in(select merchant_id from merchant_details where created_by  =:adminuuid ) order by id , merchant_id"
			, nativeQuery = true)
	List<MerchantBalanceSheet> findByAdminCommDetailsTotal(@Param("adminuuid") String adminuuid  );
	
	@Query(value = "select * from merchant_balance_sheet "
			+ "where settlement_status ='PENDING' and merchant_id in(select merchant_id from merchant_details where created_by  =:adminuuid ) order by id , merchant_id"
			, nativeQuery = true)
	List<MerchantBalanceSheet> findByAdminMerchantCommissionPendindSettlement(@Param("adminuuid") String adminuuid  );
	
	@Query(value = "select merchant_id merchantId, settlement_status settlementStatus, "
			+ "date(updated) settleMentDate,round(sum(settle_amount_to_merchant)/100,2) settledAmount, "
			+ "date(min(created)) transactionDateStart,date(max(created)) transactionDateEnd "
			+ "from merchant_balance_sheet "
			+ "where date(updated) between :dateFrom and :dateTo  "
			+ "and merchant_id =:merchantId and settlement_status = 'SETTLED' "
			+ "group by merchant_id, settlement_status,date(updated) "
			+ "order by merchant_id,date(updated),settlement_status"
			, nativeQuery = true)
	List<IMerchantWiseDateWiseSettlement> getMerchantWiseSettlementDateWise(@Param("merchantId") String merchantId,
																			@Param("dateFrom") String dateFrom,
																			@Param("dateTo") String dateTo);	

	MerchantBalanceSheet findByOrderIdAndSettlementStatus(String orderId, String string);

	MerchantBalanceSheet findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(String merchantID,
			String merchantOrderId, String settlementStatus);

	Page<MerchantBalanceSheet> findByMerchantIdContaining(String merchantID, Pageable paging);
	Page<MerchantBalanceSheet> findByMerchantIdAndSettlementStatusContaining(String merchantID,String status, Pageable paging);
	
	@Query(value = "select * from merchant_balance_sheet tr"
			+ " where tr.settlement_status =:status and tr.merchant_id =:merchantID and date(tr.settlement_date) between :dateFrom and :dateTo "
			, nativeQuery = true)
	public List<MerchantBalanceSheet> findByMerchantIdAndSettlementStatusWithDateRange(String merchantID,String status, @Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo );
	@Query(value = "select * from merchant_balance_sheet tr"
			+ " where tr.merchant_id =:merchantID and tr.merchant_order_id =:orderId "
			, nativeQuery = true)
	public List<MerchantBalanceSheet> findByMerchantIdAndMerchantOrderId(String merchantID, String orderId );
	@Query(value = "select * from merchant_balance_sheet tr"
			+ " where tr.settlement_status =:status and tr.merchant_id =:merchantID and tr.merchant_order_id =:orderId "
			, nativeQuery = true)
	List<MerchantBalanceSheet> findByMerchantIdAndMerchantOrderIdAndSettlementStatus(String merchantID, String orderId, String status);

	@Query(value = "select * from merchant_balance_sheet tr where tr.settlement_status = 'SETTLED' and  date(tr.settlement_date) between :dateFrom and :dateTo",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementDateDetails(@Param("dateFrom") String dateFrom ,@Param("dateTo") String dateTo );	
	
	@Query(value = "select * from merchant_balance_sheet tr where tr.merchant_id =:merchantID and tr.settlement_status = 'SETTLED' and  date(tr.settlement_date) >= :dateFrom",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementDateFromDetails(String merchantID, @Param("dateFrom") String dateFrom);	
	
	@Query(value = "select * from merchant_balance_sheet tr where tr.merchant_id =:merchantID and tr.settlement_status = 'SETTLED' and  date(tr.settlement_date) <= :dateTo",
			nativeQuery = true)
	public List<MerchantBalanceSheet> getSettlementDateToDetails(String merchantID, @Param("dateTo") String dateTo );	
	

	@Query(value ="update merchant_balance_sheet set settlement_status = 'refunded' where merchant_id= :merchantId and merchant_order_id= :orderId ", nativeQuery = true)
    public void changeSettlementStatusForRefund(String merchantId,
			@Param("orderId") String orderId);
}
