package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.TicketComplaintDetails;

public interface TicketComplaintDetailsRepository extends JpaRepository<TicketComplaintDetails, String>{

	TicketComplaintDetails findByComplaintId(String complaintId);

    List<TicketComplaintDetails> findBycreatedBy(String uuid);
	
	
}
