package com.asktech.pgateway.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asktech.pgateway.constants.Keys;
import com.asktech.pgateway.controller.PGUserLoginController;

public class Encryption {
	static Logger logger = LoggerFactory.getLogger(Encryption.class);

	/*
	 * public static String getEncryptedPassword(String password) throws
	 * NoSuchAlgorithmException {
	 * 
	 * MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	 * messageDigest.update(password.getBytes()); byte byteData[] =
	 * messageDigest.digest(); StringBuffer stringBuffer = new StringBuffer(); for
	 * (int i = 0; i < byteData.length; i++) {
	 * stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100,
	 * 16).substring(1)); } return stringBuffer.toString(); }
	 */

	public static String getSHA256Hash(String data) {
		String result = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data.getBytes("UTF-8"));
			return bytesToHex(hash); // make it printable
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}

	public static String generateCardNumber(String cardNumber) {
		String lastFourDigits = "";
		if (cardNumber.length() > 4)
			lastFourDigits = cardNumber.substring(cardNumber.length() - 4);
		else
			lastFourDigits = cardNumber;
		return lastFourDigits;
	}

	public static String generateProxyNumber(String proxyNumber) {
		String lastFourDigits = "";
		if (proxyNumber.length() > 4)
			lastFourDigits = "XXXXXXXX" + proxyNumber.substring(proxyNumber.length() - 4);
		else
			lastFourDigits = "XXXXXXXX" + proxyNumber;
		return lastFourDigits;
	}

	public static String getEncryptedProxy(String proxy) throws NoSuchAlgorithmException {

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(proxy.getBytes());
		byte byteData[] = messageDigest.digest();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));

		}
		return stringBuffer.toString();
	}

	public static String getEncryptedPassword(String password) {
		return AmazonKMS.encryptionWithKMS(password);
	}

	final static String secretKey = Keys.askTechSecret;
	private static String salt = Keys.askTechSalt;

	/*
	 * final static String secretKey = Keys.askTechSecret; private static String
	 * salt = Keys.askTechSalt;
	 * 
	 * public static String getEncryptedPassword(String password) { String data =
	 * ""; try { byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	 * IvParameterSpec ivspec = new IvParameterSpec(iv); SecretKeyFactory factory =
	 * SecretKeyFactory.getInstance(Keys.serviceFactoryInstance); KeySpec spec = new
	 * PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256); SecretKey
	 * tmp = factory.generateSecret(spec); SecretKeySpec secretKey = new
	 * SecretKeySpec(tmp.getEncoded(), Keys.secretKeyType); Cipher cipher =
	 * Cipher.getInstance(Keys.cipherInstance); cipher.init(Cipher.ENCRYPT_MODE,
	 * secretKey, ivspec); data =
	 * Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(Keys.
	 * utfType))); } catch (Exception e) {
	 * System.out.println("Error while encrypting: " + e.toString()); } return data;
	 * }
	 */

	public static String getDecryptedPassword(String password) {
		return AmazonKMS.decryptionWithKMS(password);
	}
	/*
	 * public static String getDecryptedPassword(String password) { String data =
	 * ""; try { byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	 * IvParameterSpec ivspec = new IvParameterSpec(iv);
	 * 
	 * SecretKeyFactory factory =
	 * SecretKeyFactory.getInstance(Keys.serviceFactoryInstance); KeySpec spec = new
	 * PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256); SecretKey
	 * tmp = factory.generateSecret(spec); SecretKeySpec secretKey = new
	 * SecretKeySpec(tmp.getEncoded(), Keys.secretKeyType);
	 * 
	 * Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
	 * cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec); data = new
	 * String(cipher.doFinal(Base64.getDecoder().decode(password))); } catch
	 * (Exception e) { System.out.println("Error while decrypting: " +
	 * e.toString()); } return data.trim().strip().replace("\u0000", ""); }
	 */

	public static String encryptCardNumberOrExpOrCvv(String password) {
		return AmazonKMS.encryptionWithKMS(password);
	}
	/*
	 * public static String encryptCardNumberOrExpOrCvv(String cardNumberOrExpOrCvv)
	 * { String data = ""; try { byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	 * 0, 0, 0, 0 }; IvParameterSpec ivspec = new IvParameterSpec(iv);
	 * SecretKeyFactory factory =
	 * SecretKeyFactory.getInstance(Keys.serviceFactoryInstance); KeySpec spec = new
	 * PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256); SecretKey
	 * tmp = factory.generateSecret(spec); SecretKeySpec secretKey = new
	 * SecretKeySpec(tmp.getEncoded(), Keys.secretKeyType); Cipher cipher =
	 * Cipher.getInstance(Keys.cipherInstance); cipher.init(Cipher.ENCRYPT_MODE,
	 * secretKey, ivspec); data =
	 * Base64.getEncoder().encodeToString(cipher.doFinal(cardNumberOrExpOrCvv.
	 * getBytes(Keys.utfType))); } catch (Exception e) {
	 * System.out.println("Error while encrypting: " + e.toString()); } return data;
	 * }
	 */

	public static String decryptCardNumberOrExpOrCvv(String password) {
		String result;
		try {
			result = AmazonKMS.decryptionWithKMS(password);
		} catch (Exception e) {
			logger.info(e.getMessage());
			result = oldDecryptCardNumberOrExpOrCvv(password);
		}
		return result;
	}

	public static String oldDecryptCardNumberOrExpOrCvv(String cardNumberOrExpOrCvv) {
		String data = "";
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance(Keys.serviceFactoryInstance);
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), Keys.secretKeyType);

			Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			data = new String(cipher.doFinal(Base64.getDecoder().decode(cardNumberOrExpOrCvv)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
			return cardNumberOrExpOrCvv;
		}
		return data.trim().strip().replace("\u0000", "");
	}

	static String key = "paytekk715wallet";
	static String iv = "paytekk715wallet";

	public static String encryptForFrontEndData(String password) {
		return AmazonKMS.encryptionWithKMS(password);
	}

	/*
	 * static String key = Keys.askTechFTSecret; static String iv =
	 * Keys.askTechFTSalt;
	 * 
	 * @SuppressWarnings("restriction") public static String
	 * encryptForFrontEndData(String data) { try { Cipher cipher =
	 * Cipher.getInstance(Keys.cipherInstance); int blockSize =
	 * cipher.getBlockSize(); byte[] dataBytes = data.getBytes(); int
	 * plaintextLength = dataBytes.length; if (plaintextLength % blockSize != 0) {
	 * plaintextLength = plaintextLength + (blockSize - (plaintextLength %
	 * blockSize)); } byte[] plaintext = new byte[plaintextLength];
	 * System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length); SecretKeySpec
	 * keyspec = new SecretKeySpec(key.getBytes(), Keys.secretKeyType);
	 * IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	 * cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec); byte[] encrypted =
	 * cipher.doFinal(plaintext); //return new
	 * sun.misc.BASE64Encoder().encode(encrypted); String encrypt =
	 * Base64.getDecoder().decode(encrypted).toString(); return encrypt;
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return null; } }
	 */

	public static String decryptForFrontEndData(String password) {
		String results = null;
		try {
			results = AmazonKMS.decryptionWithKMS(password);
		} catch (Exception e) {
			logger.info("decryptForFrontEndData " + e.getMessage());
			results = oldDecryptForFrontEndData(password);
			if (results == null) {
				results = password;
			}
		}
		return results;
	}

	@SuppressWarnings("restriction")
	public static String oldDecryptForFrontEndData(String data) {
		try {
			byte[] encrypted1 = Base64.getDecoder().decode(data);
			Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), Keys.secretKeyType);
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String genSecretKey() throws NoSuchAlgorithmException {

		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		SecretKey secretKey = keyGen.generateKey();
		// String encodedKey =
		// Base64.getEncoder().encodeToString(secretKey.getEncoded());
		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		return encodedKey.replaceAll("[^a-zA-Z0-9]", "");
	}

	public static String generateRandomPassword(int len) {
		final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			int randomIndex = random.nextInt(chars.length());
			sb.append(chars.charAt(randomIndex));
		}

		return sb.toString();
	}

}
