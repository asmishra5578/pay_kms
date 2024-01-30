package com.asktech.pgateway.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Signature {
	private final static String separator = "~";
	private final static String equator = "=";
	
	public static String encryptSignature(String secretKey, Map<String, String> postData) throws NoSuchAlgorithmException, InvalidKeyException 
	{	
		String data = "";
		SortedSet<String> keys = new TreeSet<String>(postData.keySet());
		for (String key : keys) {
			data = data + key + postData.get(key);
		}
		//System.out.println(data);
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key_spec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key_spec);
		String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
		//postData.put("signature", signature.replace("=", ""));
		//System.out.println(signature.replace("=", ""));
		//return getString(postData);
		return signature.replace("=", "").trim();
	}
	
	private static  String getString(Map<String, String> parameters) {
		Map<String, String> treeMap = new TreeMap<String, String>(parameters);

		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(separator);
			allFields.append(key);
			allFields.append(equator);
			allFields.append(treeMap.get(key));
		}

		allFields.deleteCharAt(0);
		return allFields.toString();
	}
	
	
}
