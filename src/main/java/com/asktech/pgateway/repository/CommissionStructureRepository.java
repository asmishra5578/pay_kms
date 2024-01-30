package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.model.CommissionStructure;

public interface CommissionStructureRepository extends JpaRepository<CommissionStructure, String>{

	CommissionStructure findByMerchantIdAndPgIdAndServiceType(String merchantID, String id, String merchantService);

	List<CommissionStructure>  findByPgIdAndServiceTypeAndMerchantType(String valueOf, String merchantService, String merchantType);
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type and tr.merchant_id is NULL",
			nativeQuery = true)
	public CommissionStructure checkCommissionAskTech(@Param("pg_id") String pg_id,@Param("service_type") String service_type );

	List<CommissionStructure>  findByServiceType(String paymentOption);
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type "
			+ "and tr.merchant_id =:merchant_id "			
			+ "and :card_series between card_series_start and card_series_end "
			+ "and tr.merchant_type =:merchant_type "
			+ "and tr.status =:status order by card_series_end asc limit 1",
			nativeQuery = true)
	public CommissionStructure checkCommissionDetailsWithBin(@Param("pg_id") String pg_id,
													  @Param("service_type") String service_type, 
													  @Param("merchant_id") String merchant_id,													  
													  @Param("card_series") String card_series ,													 
													  @Param("status") String status ,
													  @Param("merchant_type") String merchant_type) ;

	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pgId "			
			+ "and tr.merchant_id =:merchant_id "	
			+ "and tr.service_type =:paymentOption "
			+ "order by wallet_type,bank_name , card_series_start , card_type,card_maker ",
			nativeQuery = true)
	public List<CommissionStructure> findByPgIdAndServiceTypeAndMerchantIdOrderByWalletTypeAndBankName(
																			  @Param("paymentOption") String paymentOption,
																			  @Param("pgId") String pgId, 
																			  @Param("merchant_id") String merchantId);
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pgId "			
			+ "and tr.merchant_type =:merchant_type "
			+ "and tr.service_type =:paymentOption "
			+ "order by wallet_type,bank_name , card_series_start , card_type,card_maker ",
			nativeQuery = true)
	public List<CommissionStructure> findByPgIdAndServiceTypeAndMerchantTypeOrderByWalletTypeAndBankName(
																			@Param("paymentOption") String paymentOption,
																			@Param("pgId") String pgId,   
																			@Param("merchant_type") String merchant_type);

	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type "
			+ "and tr.merchant_id =:merchant_id "			
			+ "and tr.card_type =:card_type "
			+ "and tr.card_maker =:card_maker "
			+ "and tr.merchant_type =:merchant_type "
			+ "and tr.status =:status order by card_series_end asc limit 1",
			nativeQuery = true)
	public CommissionStructure checkCommissionDetailsWithCardTypeAndCardMaker(@Param("pg_id") String pg_id,
																			  @Param("service_type") String service_type, 
																			  @Param("merchant_id") String merchant_id,													  
																			  @Param("card_type") String card_type ,		
																			  @Param("card_maker") String card_maker ,	
																			  @Param("status") String status ,
																			  @Param("merchant_type") String merchant_type);
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type "
			+ "and tr.merchant_id =:merchant_id "		
			+ "and tr.card_maker =:card_maker "
			+ "and tr.status =:status "
			+ "and tr.merchant_type =:merchant_type ",
			nativeQuery = true)
	public CommissionStructure checkCommissionDetailsWithCardMaker(@Param("pg_id") String pg_id,
																  @Param("service_type") String service_type, 
																  @Param("merchant_id") String merchant_id,
																  @Param("card_maker") String card_maker ,	
																  @Param("status") String status, 
																  @Param("merchant_type") String merchant_type );
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type "
			+ "and tr.merchant_id =:merchant_id "		
			+ "and tr.card_type =:card_type "
			+ "and tr.status =:status "
			+ "and tr.merchant_type =:merchant_type ",
			nativeQuery = true)
	public CommissionStructure checkCommissionDetailsWithCardType(@Param("pg_id") String pg_id,
																  @Param("service_type") String service_type, 
																  @Param("merchant_id") String merchant_id,
																  @Param("card_type") String card_type ,	
																  @Param("status") String status,
																  @Param("merchant_type") String merchant_type);

	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type "
			+ "and tr.merchant_id =:merchant_id "		
			+ "and tr.bank_name =:payment_code "
			+ "and tr.status =:status "
			+ "and tr.merchant_type =:merchant_type ",
			nativeQuery = true)
	public CommissionStructure checkCommissionDetailsWithNB(@Param("pg_id") String pg_id,
																  @Param("service_type") String service_type, 
																  @Param("merchant_id") String merchant_id,
																  @Param("payment_code") String payment_code ,	
																  @Param("status") String status,
																  @Param("merchant_type") String merchant_type);
	
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type "
			+ "and tr.merchant_id =:merchant_id "		
			+ "and tr.wallet_type =:wallet_type "
			+ "and tr.status =:status "
			+ "and tr.merchant_type =:merchant_type ",
			nativeQuery = true)
	public CommissionStructure checkCommissionDetailsWithWallet(@Param("pg_id") String pg_id,
																  @Param("service_type") String service_type, 
																  @Param("merchant_id") String merchant_id,
																  @Param("wallet_type") String wallet_type ,	
																  @Param("status") String status,
																  @Param("merchant_type") String merchant_type);
	
	

}
