package com.asktech.pgateway.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class EncryptSignature {
	
	static Logger logger = LoggerFactory.getLogger(EncryptSignature.class);
	public static String encryptSignature(String secretKey, Map<String, String> postData) throws NoSuchAlgorithmException, InvalidKeyException 
	{	
		//logger.info("secretKey :: ---"+secretKey);
		String data = "";
		SortedSet<String> keys = new TreeSet<String>(postData.keySet());
		for (String key : keys) {
			data = data + key + postData.get(key);
		}
		logger.info("----"+data);
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key_spec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key_spec);
		String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes()));
		//System.out.println(signature);
		return signature.replace("=", "");
	}
	
	public static String getSecretHash(String appId, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key_spec = new SecretKeySpec(appId.substring(8, 17).getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key_spec);
		String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(secret.getBytes()));
		//System.out.println(signature);
		return signature.replace("=", "");
	}
}

