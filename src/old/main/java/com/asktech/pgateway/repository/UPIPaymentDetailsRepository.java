package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.UPIPaymentDetails;

public interface UPIPaymentDetailsRepository extends JpaRepository<UPIPaymentDetails, String>{

}
