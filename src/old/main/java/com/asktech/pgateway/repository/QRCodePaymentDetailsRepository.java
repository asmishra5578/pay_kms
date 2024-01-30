package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.QRCodePaymentDetails;

public interface QRCodePaymentDetailsRepository extends JpaRepository<QRCodePaymentDetails, String>{

}
