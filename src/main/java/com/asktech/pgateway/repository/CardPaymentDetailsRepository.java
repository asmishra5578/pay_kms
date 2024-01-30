package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.CardPaymentDetails;

public interface CardPaymentDetailsRepository extends JpaRepository<CardPaymentDetails, String>{

}
