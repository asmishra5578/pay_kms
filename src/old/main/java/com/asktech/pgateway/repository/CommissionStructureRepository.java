package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.model.CommissionStructure;

public interface CommissionStructureRepository extends JpaRepository<CommissionStructure, String>{

	CommissionStructure findByMerchantIdAndPgIdAndServiceType(String merchantID, String id, String merchantService);

	CommissionStructure findByPgIdAndServiceType(String valueOf, String merchantService);
	
	@Query(value = "select tr.* from commission_structure tr "
			+ "where tr.pg_id =:pg_id "
			+ "and tr.service_type =:service_type and tr.merchant_id is NULL",
			nativeQuery = true)
	public CommissionStructure checkCommissionAskTech(@Param("pg_id") String pg_id,@Param("service_type") String service_type );

	List<CommissionStructure>  findByServiceType(String paymentOption);

}
