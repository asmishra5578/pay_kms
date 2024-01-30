package com.asktech.pgateway.util.nsdl;

import java.security.Key;
import java.sql.Connection;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

public class DataEncryptionDecryption {

	private static final String ALGO = "AES";

	private static Key generateKey(byte[] keyByte) {
		Key key = new SecretKeySpec(keyByte, ALGO);
		return key;
	}

	public static String encrypt(String sDataToEncrypt, String sEncryptionKey) {

		String encryptedValue = null;

		try {

			//System.out.println("Encrypting data...");

			byte[] keyByte = sEncryptionKey.getBytes();
			Key key = generateKey(keyByte);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte encVal[] = c.doFinal(sDataToEncrypt.getBytes());
			byte encryptedByteValue[] = new Base64().encode(encVal);
			encryptedValue = new String(encryptedByteValue);

			//System.out.println(" Encrypted value " + encryptedValue);

		} catch (Exception ex) {

			System.out.println("Exception while encrypting Data -> [" + ex + "]");
			ex.printStackTrace();

		}

		return encryptedValue;

	}

	public static String decrypt(String sDataToDecrypt, String sDecryptionKey) {

		String decryptedValue = null;

		try {

			byte[] keyByte = sDecryptionKey.getBytes();
			Key key = generateKey(keyByte);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			byte decryptedByteValue[] = new Base64().decode(sDataToDecrypt.getBytes());
			byte decValue[] = c.doFinal(decryptedByteValue);
			decryptedValue = new String(decValue);

		} catch (Exception ex) {

			System.out.println("Exception while decrypting Data -> [" + ex + "]");
			ex.printStackTrace();

		}

		return decryptedValue;

	}
}
