package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.asktech.pgateway.customInterface.IAdminPGAllDetails;
import com.asktech.pgateway.model.PGConfigurationDetails;

public interface PGConfigurationDetailsRepository  extends JpaRepository<PGConfigurationDetails, String>{

	List<PGConfigurationDetails> findAllByPgUuid(String pgUuid);

	PGConfigurationDetails findByPgUuid(String pgUuid);

	PGConfigurationDetails findByPgName(String pgName);

	PGConfigurationDetails findByPgAppId(String pgAppid);
	
	@Query(value = "SELECT  a.created ,a.updated,a.pg_app_id, a.pg_name, a.status pg_status, b.created service_created, b.updated service_updated, b.pg_services, b.status service_status, b.default_service, b.priority, b.thresold_day, b.thresold_month, b.thresold_week, b.thresold_year, b.thresold_3month, b.thresold_6month "
			+ "FROM pgconfiguration_details a, pgservice_details b "
			+ "WHERE a.pg_uuid = b.pg_id;",
			nativeQuery = true)
	List<IAdminPGAllDetails> getAllPgDetails();
}
