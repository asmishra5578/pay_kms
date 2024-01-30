package com.asktech.pgateway.repository.DistributionManagement;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.DistributionManagement.DistributorMapping;

public interface DistributorMappingRepo extends JpaRepository<DistributorMapping, String>{
    
}
