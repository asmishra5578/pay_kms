package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.TicketTransactionDetails;

public interface TicketTransactionDetailsRepository extends JpaRepository<TicketTransactionDetails, String>{

	List<TicketTransactionDetails> findAllByComplaintIdOrderByIdAsc(String complaintId);

}
