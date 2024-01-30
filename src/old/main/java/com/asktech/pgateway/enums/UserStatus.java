package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserStatus {
	
ACTIVE("ACTIVE"),BLOCKED("BLOCKED"),DELETE("DELETE");
	
	private static Map<String, Object> map = new HashMap<>();
	private String userStatus;
	static {
		for (UserStatus userStatus : UserStatus.values()) {
			map.put(userStatus.userStatus, userStatus);
		}
	}

	public String getValue() {
		return userStatus;
	}


	private UserStatus(String k) {
		this.userStatus = k;
	}

	public String getStatus() {
		return userStatus;
	}


}
