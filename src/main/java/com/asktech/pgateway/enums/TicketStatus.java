package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum TicketStatus {

OPEN("OPEN"),CLOSED("CLOSED") , TRANSFER("TRANSFER") , REOPEN("REOPEN") , INPROGESS("INPROGRESS");
	
	private static Map<String, Object> map = new HashMap<>();
	private String ticketStatus;
	static {
		for (TicketStatus ticketStatus : TicketStatus.values()) {
			map.put(ticketStatus.ticketStatus, ticketStatus);
		}
	}

	public String getValue() {
		return ticketStatus;
	}


	private TicketStatus(String k) {
		this.ticketStatus = k;
	}

	public String getStatus() {
		return ticketStatus;
	}


}
