package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;


public enum KycStatus {
	
YES("YES"),NO("NO"),PENDING("PENDING"),APPROVED("APPROVED"),REJECTED("REJECTED");
	
	private static Map<String, Object> map = new HashMap<>();
	private String kycStatus;
	static {
		for (KycStatus kycStatus : KycStatus.values()) {
			map.put(kycStatus.kycStatus, kycStatus);
		}
	}

	public String getValue() {
		return kycStatus;
	}


	private KycStatus(String k) {
		this.kycStatus = k;
	}

	public String getStatus() {
		return kycStatus;
	}
	
}
