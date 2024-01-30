package com.asktech.pgateway.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.asktech.pgateway.constants.Keys;

public class Encryption {

	/*
	public static String getEncryptedPassword(String password) throws NoSuchAlgorithmException {

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(password.getBytes());
		byte byteData[] = messageDigest.digest();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}
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
		
	final static String secretKey = Keys.askTechSecret;
	private static String salt = Keys.askTechSalt;

	public static String getEncryptedPassword(String password) {
		String data = "";
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(Keys.serviceFactoryInstance);
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), Keys.secretKeyType);
			Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			data = Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(Keys.utfType)));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return data;
	}

	public static String getDecryptedPassword(String password) {
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
			data = new String(cipher.doFinal(Base64.getDecoder().decode(password)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return data;
	}
	
	public static String encryptCardNumberOrExpOrCvv(String cardNumberOrExpOrCvv) {
		String data = "";
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(Keys.serviceFactoryInstance);
			KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), Keys.secretKeyType);
			Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			data = Base64.getEncoder().encodeToString(cipher.doFinal(cardNumberOrExpOrCvv.getBytes(Keys.utfType)));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return data;
	}

	public static String decryptCardNumberOrExpOrCvv(String cardNumberOrExpOrCvv) {
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
		}
		return data;
	}
	
	//static String key = "paytekk715wallet";
	//static String iv = "paytekk715wallet";
	
	static String key = Keys.askTechFTSecret;
	static String iv = Keys.askTechFTSalt;
	@SuppressWarnings("restriction")
	public static String encryptForFrontEndData(String data) {
		 try {
	            Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
	            int blockSize = cipher.getBlockSize();
	            byte[] dataBytes = data.getBytes();
	            int plaintextLength = dataBytes.length;
	            if (plaintextLength % blockSize != 0) {
	                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
	            }
	            byte[] plaintext = new byte[plaintextLength];
	            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
	            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), Keys.secretKeyType);
	            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
	            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	            byte[] encrypted = cipher.doFinal(plaintext);
	            //return new sun.misc.BASE64Encoder().encode(encrypted);
	            String encrypt = Base64.getDecoder().decode(encrypted).toString();
	            return  encrypt;
	            

	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	}

	@SuppressWarnings("restriction")
	public static String decryptForFrontEndData(String data){
		try
        {
			byte[] encrypted1 = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance(Keys.cipherInstance);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), Keys.secretKeyType);
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	

    public static String genSecretKey() throws NoSuchAlgorithmException {

        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] values = new byte[32]; // 256 bit
        random.nextBytes(values);

        StringBuilder sb = new StringBuilder();
        for (byte b : values) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
	
	
	public static void main(String args[]) {
		System.out.println("Welcome#123 :: "+getEncryptedPassword("Welcome#123"));
	}

}
