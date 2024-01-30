package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum OtpStatus {
	ENABLE("ENABLE"),DISABLE("DISABLE");
	
	private static Map<String, Object> map = new HashMap<>();
	private String otpStatus;
	static {
		for (OtpStatus OtpStatus : OtpStatus.values()) {
			map.put(OtpStatus.otpStatus, OtpStatus);
		}
	}

	public String getValue() {
		return otpStatus;
	}


	private OtpStatus(String k) {
		this.otpStatus = k;
	}

	public String getStatus() {
		return otpStatus;
	}
}
