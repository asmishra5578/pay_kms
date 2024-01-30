package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum UtilityType {

CARD_BIN_CHECK("CARD_BIN_CHECK");
	
	private static Map<String, Object> map = new HashMap<>();
	private String apStatus;
	static {
		for (UtilityType apStatus : UtilityType.values()) {
			map.put(apStatus.apStatus, apStatus);
		}
	}

	public String getValue() {
		return apStatus;
	}


	private UtilityType(String k) {
		this.apStatus = k;
	}

	public String getStatus() {
		return apStatus;
	}
}
