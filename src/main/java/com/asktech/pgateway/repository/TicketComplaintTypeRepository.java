package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.model.TicketComplaintType;

public interface TicketComplaintTypeRepository extends JpaRepository<TicketComplaintType, String>{

	TicketComplaintType findByCommType(String complaintType);
	
	@Query(value = "select comm_type "
			+ "from ticket_complaint_type",
			nativeQuery = true)
	List<String> getComCategory();
	
}
