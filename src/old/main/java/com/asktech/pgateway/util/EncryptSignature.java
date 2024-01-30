package com.asktech.pgateway.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final public class EncryptSignature {
	
	public static String encryptSignature(String secretKey, Map<String, String> postData) throws NoSuchAlgorithmException, InvalidKeyException 
	{	
		String data = "";
		SortedSet<String> keys = new TreeSet<String>(postData.keySet());
		for (String key : keys) {
			data = data + key + postData.get(key);
		}
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key_spec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key_spec);
		String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
		//System.out.println(signature);
		return signature;
	}
}

