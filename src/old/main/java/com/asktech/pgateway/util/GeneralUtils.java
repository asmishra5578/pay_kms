package com.asktech.pgateway.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.Cookie;

import org.springframework.util.MultiValueMap;

public class GeneralUtils {

	public static boolean isValid(Object obj) {
		return obj != null;
	}

	public static void printl(String val) {
		System.out.println(val);
	}

	public static Map<String, String> convertMultiToRegularMap(MultiValueMap<String, String> m) {
		Map<String, String> map = new HashMap<String, String>();
		if (m == null) {
			return map;
		}
		for (Entry<String, List<String>> entry : m.entrySet()) {
			String qKey = entry.getKey();
			List<String> values = entry.getValue();
			if (values.size() > 1) {
				String val = "";
				int i = 0;
				for (String s : values) {
					if (i > 0) {
						val += ",";
					}
					val += s;
					i++;
				}
				map.put(qKey, val);
			} else {
				map.put(qKey, values.get(0));
			}
		}
		return map;
	}
	
	public static Cookie setCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		return cookie;
	}
	
	public static String getTrxId() {
		int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	    
	    return generatedString;
	}
}
