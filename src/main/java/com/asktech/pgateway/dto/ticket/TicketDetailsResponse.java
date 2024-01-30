package com.asktech.pgateway.dto.ticket;

import java.util.List;

import com.asktech.pgateway.model.TicketTransactionDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailsResponse {
	
	private long id;
	private String complaintId;
	private String complaintTest;
	private String commType;
	private String commSubType;
	private String raisedOn;
	private String updatedBy;
	private String status;
	private Integer commCounter;
	private List<TicketTransactionDetails> listTicketTransactionDetails;
}
