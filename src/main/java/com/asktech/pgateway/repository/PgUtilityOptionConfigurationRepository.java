package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.PgUtilityOptionConfiguration;

public interface PgUtilityOptionConfigurationRepository extends JpaRepository<PgUtilityOptionConfiguration, String>{

	List<PgUtilityOptionConfiguration> findByUtilityTypeAndStatus(String string, String string2);

}
