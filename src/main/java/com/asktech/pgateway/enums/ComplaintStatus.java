package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum ComplaintStatus {
	
ACTIVE("ACTIVE"),BLOCKED("BLOCKED");
	
	private static Map<String, Object> map = new HashMap<>();
	private String complaintStatus;
	static {
		for (ComplaintStatus complaintStatus : ComplaintStatus.values()) {
			map.put(complaintStatus.complaintStatus, complaintStatus);
		}
	}

	public String getValue() {
		return complaintStatus;
	}


	private ComplaintStatus(String k) {
		this.complaintStatus = k;
	}

	public String getStatus() {
		return complaintStatus;
	}


}
