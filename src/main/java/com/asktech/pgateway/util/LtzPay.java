package com.asktech.pgateway.util;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.security.Encryption;


public class LtzPay {
	public static final String ALGO = "AES";
	private static String key = null;
	private static Key keyObj = null;
	private static Stack<MessageDigest> stack = new Stack<MessageDigest>();


	public static void main(String[] args) throws NoSuchAlgorithmException {
		String encKey = "81971163ed75454a";
		encKey = getHash(encKey).substring(0,32);
		
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		
		System.out.println("Decrypted Data");
		
		String d = generateDecryption("5aBtfy8yWT83rnlgKtNfrAv+eWvxxd35sxW2P1kjcBb5U8xvGAvp75LZ+RmokXmS9j06p+C7XjkQpYWWYl2lZfFUsnt+FqF/Fwv/KBPS+X0NsayLWlvM6jLREPdXp13Gntkdnp2HQ2/vaxmFCZ/nWOGB8YJkwKFrKTQTFogd30rwSbwWfEqJjVHqeZW60qGC123IdIO9n/+nvW1AzrWOdW4zfD55FZbhA5EyyyjVJADcs45JEcsJffNcs9muHpOBs77SD0SH0VEUFhK8/dfdsukY9zMivSLOxp1fBR1VNA2409z7z7QXvZ4bk1Uv5idN5zI2yjaUN28BGKFHhIoIHkRPpZhP7q193JcKJoXLdtEYlxOXWFgnH9r9pTyYigW07DHM1KHXK1PdGy2CMvl+qlV82O6I57ZC0QH7zdKHNP5lYe5eeW8x5rjMUA+SNI+yLMtXGwvbCdSWPB9rB0PgwQ");
		System.out.println(d);
	}
	
	public static String generateEncryption(MerchantPGDetails merchantPGDetails , Map<String, String> parameters ) throws NoSuchAlgorithmException {
		String encKey = Encryption.decryptCardNumberOrExpOrCvv(merchantPGDetails.getMerchantPGSecret());
		System.out.println("LetzPay Secret :: "+ encKey);
		encKey = LtzPay.getHash(encKey).substring(0,32);
		keyObj = new  SecretKeySpec(encKey.getBytes(), ALGO);		
		String data = ChecksumUtils.getString(parameters);	
		
		String req = data+"~HASH="+ChecksumUtils.generateCheckSum(parameters, "0191a0a608c04f12");
		String encdata = LtzPay.hostedEncrypt(req,encKey,keyObj);	
		
		return encdata;
	}
	
	public static String generateDecryption(String data ) throws NoSuchAlgorithmException {
		
		System.out.println("Inside generateDecryption()");
		String encKey = "81971163ed75454a";
		encKey = LtzPay.getHash(encKey).substring(0,32);
		key = encKey;
		keyObj = new SecretKeySpec(key.getBytes(), ALGO);
		
		String encdata = decrypt(data, key , keyObj);
		
		
		
		return encdata;
	}
	
	
	public static String hostedEncrypt(String data,String key , Key keyObj ) {
		try {
				System.out.println("Inside hostedEncrypt()");
			String ivString = key.substring(0, 16);
			IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));

			Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

			byte[] encValue = cipher.doFinal(data.getBytes("UTF-8"));

			Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();
			String base64EncodedData = base64Encoder.encodeToString(encValue);
			System.out.println("End hostedEncrypt()");
			return base64EncodedData;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException scramblerExceptionException) {
			
		}
		return "";
	}

	public static MessageDigest provide(){
		MessageDigest digest = null;
		try{
			digest = stack.pop();
		} catch (EmptyStackException emptyStackException){
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
				
			}
		}
		
		return digest;
	}
	
	public static void consume(MessageDigest digest){
		stack.push(digest);
	}
	
	public static String getHash(String input) {
		String response = null;

		MessageDigest messageDigest = provide();
		messageDigest.update(input.getBytes());
		consume(messageDigest);

		response = new String(Hex.encodeHex(messageDigest.digest()));

		return response.toUpperCase();
	}// getSHA256Hex()

	public static String decrypt(String data , String key , Key keyObj) {
		System.out.println("Input :: "+ data);
		try {
			String ivString = key.substring(0, 16);
			IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));
			Cipher cipher = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);
			byte[] decodedData = Base64.getDecoder().decode(data);
			byte[] decValue = cipher.doFinal(decodedData);

			return new String(decValue);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException
				| IllegalBlockSizeException | BadPaddingException
				| InvalidAlgorithmParameterException scramblerExceptionException) {
			scramblerExceptionException.printStackTrace();

		}
		return "";
	}

	

}
