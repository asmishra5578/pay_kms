package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.WalletPaymentDetails;

public interface WalletPaymentDetailsRepository extends JpaRepository<WalletPaymentDetails, String>{

}
