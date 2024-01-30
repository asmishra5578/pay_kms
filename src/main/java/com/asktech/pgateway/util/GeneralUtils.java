package com.asktech.pgateway.util;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GeneralUtils {
	private final static String LOCALHOST_IPV4 = "127.0.0.1";
	private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

	public static boolean isValid(Object obj) {
		return obj != null;
	}

	public static void printl(String val) {
		System.out.println(val);
	}

	@SuppressWarnings("deprecation")
	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
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

	public static String MultiValueMaptoJson(MultiValueMap<String, String> m) throws JsonProcessingException {
		return Utility.convertDTO2JsonString(convertMultiToRegularMap(m));
	}

	public static Cookie setCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		return cookie;
	}

	public static String getTrxId() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}

	public static String getClientIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}

		if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}

		if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
				try {
					InetAddress inetAddress = InetAddress.getLocalHost();
					ipAddress = inetAddress.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		if (ipAddress != null) {
			if (!StringUtils.hasLength(ipAddress) && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}

		return ipAddress;
	}
		
}
