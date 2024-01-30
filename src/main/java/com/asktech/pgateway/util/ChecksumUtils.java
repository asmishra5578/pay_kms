package com.asktech.pgateway.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Hex;

import com.fasterxml.jackson.core.JsonProcessingException;


public class ChecksumUtils {

	private static Stack<MessageDigest> stack = new Stack<MessageDigest>();
	private final static String separator = "~";
	private final static String equator = "=";
	private final static String hashingAlgo = "SHA-256";

	// Hash calculation from request map
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

		allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
		allFields.append(secretKey);
		// Calculate hash
		return getHash(allFields.toString());
	}
	
	public static String generateCheckSumWOSecret(Map<String, String> parameters,
			String secretKey) throws NoSuchAlgorithmException, JsonProcessingException {
		Map<String, Object> treeMap = new TreeMap<String, Object>(parameters);

		StringBuilder allFields = new StringBuilder();
		for (String key : treeMap.keySet()) {
			allFields.append(separator);
			allFields.append(key);
			allFields.append(equator);
			allFields.append(treeMap.get(key));
		}

		System.out.println("Print Values() :: "+allFields.toString());
		allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
		System.out.println("allFields1 :: "+allFields.toString());
		allFields.append(secretKey);
		System.out.println("allFields 2 :: "+Utility.convertDTO2JsonString(allFields.toString()));
		return getHash(allFields.toString());
	}
	
	public static String getString(Map<String, String> parameters) {
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
	

	// Response hash validation
	public static boolean validateResponseChecksum(
			Map<String, String> responseParameters, String secretKey,
			String responseHash) throws NoSuchAlgorithmException {
		boolean flag = false;
		String generatedHash = generateCheckSum(responseParameters, secretKey);
		if (generatedHash.equals(responseHash)) {
			flag = true;
		}
		return flag;
	}

	// Generate hash from the supplied string
	public static String getHash(String input) throws NoSuchAlgorithmException {
		String response = null;

		MessageDigest messageDigest = provide();
		messageDigest.update(input.getBytes());
		consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}// getSHA256Hex()

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
	
	public static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException
	{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0; 
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	   return sb.toString();
	}
}
