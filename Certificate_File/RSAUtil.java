package com.futuretek.pgcomponent.util;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSAUtil {

	public String encrypt(String value) {
		try {
			byte[] key = "KEY".getBytes("UTF-8");
			byte[] ivs = "iv".getBytes("UTF-8");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivs);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static PublicKey getRSAPublicKey(String base64PublicKey) {
		PublicKey publicKey = null;
		try {
			byte[]  data = Files.readAllBytes(Paths.get(base64PublicKey));
			byte[] publicKeyBytes = Base64.getDecoder().decode(data);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return publicKey;
	}
	 
	public static PublicKey getPublicKey(String publicKeyPath) throws Exception {
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream(publicKeyPath);
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
		PublicKey key = cer.getPublicKey();
		return key;
	}

	public static PrivateKey getPrivateKey(String privateKeyPath) throws Exception {
		String privateKey = new String(Files.readAllBytes(Paths.get(privateKeyPath)));
		// privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----",
		// "").replace("-----END PRIVATE KEY-----",
		// "").replaceAll(System.lineSeparator(), "");
		byte[] pkbytes = Base64.getDecoder().decode(privateKey.getBytes());
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkbytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		PrivateKey pk = keyFactory.generatePrivate(keySpec);
		return pk;
	}

	/*
	 * public static PrivateKey getPrivateKey(String base64PrivateKey){ PrivateKey
	 * privateKey = null; PKCS8EncodedKeySpec keySpec = new
	 * PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
	 * KeyFactory keyFactory = null; try { keyFactory =
	 * KeyFactory.getInstance("RSA"); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); } try { privateKey =
	 * keyFactory.generatePrivate(keySpec); } catch (InvalidKeySpecException e) {
	 * e.printStackTrace(); } return privateKey; }
	 */
	
	public static byte[] encryptRSA(String data, String publicKeyPath) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getRSAPublicKey(publicKeyPath));
		return cipher.doFinal(data.getBytes());
	}

	public static byte[] encrypt(String data, String publicKeyPath) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKeyPath));
		return cipher.doFinal(data.getBytes());
	}

	public static String decrypt(byte[] data, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return new String(cipher.doFinal(data));
	}

	public static String decrypt(byte[] data, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(data));
	}

	public static String decrypt(String data, String privateKeyPath) throws Exception {
		return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(privateKeyPath));
	}

	public static String encrypt1(String data, String privateKeyPath) throws Exception {
		return encrypt(Base64.getDecoder().decode(data.getBytes()), getPublicKey(privateKeyPath));
	}

	private static String encrypt(byte[] decode, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return new String(cipher.doFinal(decode));
	}

}
