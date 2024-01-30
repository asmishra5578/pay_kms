package com.asktech.pgateway.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserTypes {
	
ADMIN("ADMIN"),RETAILER("RETAILER"),USER("USER"),SUPER("SUPER");
	
	private static Map<String, Object> map = new HashMap<>();
	private String userType;
	static {
		for (UserTypes userType : UserTypes.values()) {
			map.put(userType.userType, userType);
		}
	}

	public String getValue() {
		return userType;
	}


	private UserTypes(String k) {
		this.userType = k;
	}

	public String getStatus() {
		return userType;
	}


}
