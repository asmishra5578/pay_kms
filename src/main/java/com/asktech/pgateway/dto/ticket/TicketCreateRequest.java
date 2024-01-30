package com.asktech.pgateway.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateRequest {

	private String complaintType;
	private String complaintSubType;
	private String complaintText;
	
}
