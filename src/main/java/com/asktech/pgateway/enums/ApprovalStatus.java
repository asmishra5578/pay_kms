package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum ApprovalStatus {
	
NEW("NEW"),APPROVE("APPROVE"),REJECT("REJECT");
	
	private static Map<String, Object> map = new HashMap<>();
	private String apStatus;
	static {
		for (ApprovalStatus apStatus : ApprovalStatus.values()) {
			map.put(apStatus.apStatus, apStatus);
		}
	}

	public String getValue() {
		return apStatus;
	}


	private ApprovalStatus(String k) {
		this.apStatus = k;
	}

	public String getStatus() {
		return apStatus;
	}

}
