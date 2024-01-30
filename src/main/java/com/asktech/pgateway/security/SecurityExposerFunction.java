package com.asktech.pgateway.security;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class SecurityExposerFunction {
	
	private final static String separator = "~";
	private final static String equator = "=";
	private final static String hashingAlgo = "SHA-256";
	private static Stack<MessageDigest> stack = new Stack<MessageDigest>();

	public String createJWT(Map<String, Object> claims, Map<String, Object> header, String SECRET_KEY) {

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;	

		byte[] apiKeySecretBytes = SECRET_KEY.getBytes();

		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		JwtBuilder builder = Jwts.builder().setHeader(header).setClaims(claims).signWith(signatureAlgorithm,signingKey);
		return builder.compact();
	}
	
	public Claims decodeJWTwithSignature(String jwt,String SECRET_KEY) {
	    System.out.println("SECRET:"+SECRET_KEY);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		byte[] apiKeySecretBytes = SECRET_KEY.getBytes();
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
		
	    Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt).getBody();
	    return claims;
	}
	
	public Claims decodeJWT(String jwt) {
	    
		int i = jwt.lastIndexOf('.');
		String withoutSignature = jwt.substring(0, i+1);
		Jwt<Header,Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
		
		return untrusted.getBody();
	}
	
	public Map<String, Object>  createHeader(){
		Map<String, Object> header = new HashMap<>();

		header.put("typ", "JWT");
		header.put("alg", "HS256");
		
		return header;
	}
	
	public Map<String, Object>  createPayLoad(String appId, String requestId){
		Map<String, Object> payLoad = new HashMap<>();
		
		payLoad.put("iss", "ASKTECH");
		payLoad.put("timestamp", System.currentTimeMillis());
		payLoad.put("appId", appId);
		payLoad.put("requestReferenceId", requestId);
		
		return payLoad;
	}
	
	public static String generateCheckSum(Map<String, String> parameters,
			String secretKey) throws NoSuchAlgorithmException {
		Map<String, String> treeMap = new TreeMap<String, String>(parameters);

		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(separator);
			allFields.append(key);
			allFields.append(equator);
			allFields.append(treeMap.get(key));
		}

		allFields.deleteCharAt(0); 
		allFields.append(secretKey);
		return getHash(allFields.toString());
	}
	
	public static String generateCheckSum(String parameters,
			String secretKey) throws NoSuchAlgorithmException {
		

		StringBuilder allFields = new StringBuilder();
		allFields.append(parameters);
		allFields.append(secretKey);
		return getHash(allFields.toString());
	}
	
	public static String getHash(String input) throws NoSuchAlgorithmException {
		String response = null;

		MessageDigest messageDigest = provide();
		messageDigest.update(input.getBytes());
		consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}

	private static MessageDigest provide() throws NoSuchAlgorithmException {
		MessageDigest digest = null;

		try {
			digest = stack.pop();
		} catch (EmptyStackException emptyStackException) {
			digest = MessageDigest.getInstance(hashingAlgo);
		}
		return digest;
	}

	private static void consume(MessageDigest digest) {
		stack.push(digest);
	}
	
	public static String base64UrlDecoder(String str) {
		
		return Base64.getUrlEncoder().encodeToString(str.getBytes());
		
	}
	
	
	public static void main(String args[]) {
		System.out.println(Encryption.decryptCardNumberOrExpOrCvv("R6xwDeUq+Z1U/tw+gbJrOqTzQPujDZFIU1tMUIGGw9eYyBxhTbFFjgOgPwpuAGEIkM18AKOZcMjq3Aqa9LWzS//LGie9lsMLoNrY8F+VGko="));
	}
	
}
