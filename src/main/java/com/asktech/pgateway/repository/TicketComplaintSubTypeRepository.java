package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.asktech.pgateway.model.TicketComplaintSubType;

public interface TicketComplaintSubTypeRepository extends JpaRepository<TicketComplaintSubType, String>{

	List<TicketComplaintSubType> findAllByCommType(String complaintType);

	TicketComplaintSubType findAllByCommTypeAndCommSubType(String complaintType, String subType);
    List<TicketComplaintSubType> findBycommType(String commType);

    TicketComplaintSubType findByCommTypeAndCommSubTypeAndStatus(String complaintType, String complaintSubType,
            String string);

}
