package com.asktech.pgateway.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.asktech.pgateway.constants.Keys;
import com.asktech.pgateway.security.AmazonKMS;

import org.apache.commons.codec.digest.DigestUtils;

public class SecurityUtils extends Keys {
	public static String getOrderNumber() {
		Instant instant = Instant.now();
		long timeStampSeconds = instant.getEpochSecond();
		Random rnd = new Random();
		int n = 1000 + rnd.nextInt(9000);
		String xl = String.valueOf(timeStampSeconds);
		String xn = String.valueOf(n);
		return xl + xn;
	}

	public static String encryptFrontEndData(String password) {
		return AmazonKMS.encryptionWithKMS(password);
	}
	/*
	 * static String key = Keys.FrontKey; static String iv = Keys.FrontSalt;
	 * 
	 * public static String encryptFrontEndData(String data) { Base64.Encoder
	 * base64Encoder = Base64.getEncoder(); try { Cipher cipher =
	 * Cipher.getInstance("AES/CBC/NoPadding"); int blockSize =
	 * cipher.getBlockSize(); byte[] dataBytes = data.getBytes(); int
	 * plaintextLength = dataBytes.length; if (plaintextLength % blockSize != 0) {
	 * plaintextLength = plaintextLength + (blockSize - (plaintextLength %
	 * blockSize)); } byte[] plaintext = new byte[plaintextLength];
	 * System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length); SecretKeySpec
	 * keyspec = new SecretKeySpec(key.getBytes(), "AES"); IvParameterSpec ivspec =
	 * new IvParameterSpec(iv.getBytes()); cipher.init(Cipher.ENCRYPT_MODE, keyspec,
	 * ivspec); byte[] encrypted = cipher.doFinal(plaintext); return new
	 * String(base64Encoder.encode(encrypted));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return null; } }
	 */

	public static String decryptFrontEndData(String password) {
		return AmazonKMS.decryptionWithKMS(password);
	}

	/*
	 * public static String decryptFrontEndData(String data) { Base64.Decoder
	 * base64Decoder = Base64.getDecoder(); try { byte[] encrypted1 =
	 * base64Decoder.decode(data); Cipher cipher =
	 * Cipher.getInstance("AES/CBC/NoPadding"); SecretKeySpec keyspec = new
	 * SecretKeySpec(key.getBytes(), "AES"); IvParameterSpec ivspec = new
	 * IvParameterSpec(iv.getBytes()); cipher.init(Cipher.DECRYPT_MODE, keyspec,
	 * ivspec); byte[] original = cipher.doFinal(encrypted1); String originalString
	 * = new String(original); return
	 * originalString.trim().strip().replace("\u0000", ""); } catch (Exception e) {
	 * e.printStackTrace(); return null; } }
	 */
	public static String encryptSaveData(String password) {
		return AmazonKMS.encryptionWithKMS(password);
	}

	static String keys = Keys.BackKey;
	static String ivs = Keys.BackSalt;
	/*
	 * public static String encryptSaveData(String data) { Base64.Encoder
	 * base64Encoder = Base64.getEncoder(); try { Cipher cipher =
	 * Cipher.getInstance("AES/CBC/NoPadding"); int blockSize =
	 * cipher.getBlockSize(); byte[] dataBytes = data.getBytes(); int
	 * plaintextLength = dataBytes.length; if (plaintextLength % blockSize != 0) {
	 * plaintextLength = plaintextLength + (blockSize - (plaintextLength %
	 * blockSize)); } byte[] plaintext = new byte[plaintextLength];
	 * System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length); SecretKeySpec
	 * keyspec = new SecretKeySpec(keys.getBytes(), "AES"); IvParameterSpec ivspec =
	 * new IvParameterSpec(ivs.getBytes()); cipher.init(Cipher.ENCRYPT_MODE,
	 * keyspec, ivspec); byte[] encrypted = cipher.doFinal(plaintext); return new
	 * String(base64Encoder.encode(encrypted));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return null; } }
	 */

	public static String decryptSaveData(String password) {
		String results = password;
		try {
			results = AmazonKMS.decryptionWithKMS(password);
		} catch (Exception e) {
			results = oldDecryptSaveData(password);
			if (results == null) {
				results = password;
			}
		}
		return results;
	}

	public static String oldDecryptSaveData(String data) {
		Base64.Decoder base64Decoder = Base64.getDecoder();
		try {
			byte[] encrypted1 = base64Decoder.decode(data);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(keys.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(ivs.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString.trim().strip().replace("\u0000", "");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Java program to create a
	// asymmetric key

	// Class to create an asymmetric key

	// Generating public and private keys
	// using RSA algorithm.

	public static void RSAKeyPairGenerator() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair pair = keyGen.generateKeyPair();
		System.out.println(
				String.format("Public Key : %s", Base64.getEncoder().encodeToString(pair.getPublic().getEncoded())));
		System.out.println(
				String.format("Private Key : %s", Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded())));

	}

	public static PublicKey getPublicKey(String base64PublicKey) {
		PublicKey publicKey = null;
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	public static PrivateKey getPrivateKey(String base64PrivateKey) {
		PrivateKey privateKey = null;
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	public static byte[] encryptRSA(String data, String publicKey) throws BadPaddingException,
			IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
		return cipher.doFinal(data.getBytes());
	}

	public static String decryptRSA(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(data));
	}

	public static String decryptRSABase64(String data, String base64PrivateKey) throws IllegalBlockSizeException,
			InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		return decryptRSA(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
	}

	/* Encryption Logic for Input Data */

	public static String encryptInputData(String data, String saltKey, String secret) {
		Base64.Encoder base64Encoder = Base64.getEncoder();
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			SecretKeySpec keyspec = new SecretKeySpec(secret.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(saltKey.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			return new String(base64Encoder.encode(encrypted));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decryptInputData(String data, String saltKey, String secret) {
		Base64.Decoder base64Decoder = Base64.getDecoder();
		try {
			byte[] encrypted1 = base64Decoder.decode(data);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(secret.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(saltKey.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String hash256Bit(String data) throws NoSuchAlgorithmException {
		return DigestUtils.sha256Hex(data);
	}
}
