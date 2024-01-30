package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.PGServiceThresoldCalculation;

public interface PGServiceThresoldCalculationRepository extends JpaRepository<PGServiceThresoldCalculation, String>{

	PGServiceThresoldCalculation findByPgIdAndServiceType(String pgUuid, String pgServices);

}
