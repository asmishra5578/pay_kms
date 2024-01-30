package com.asktech.pgateway.util.getepay;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import com.asktech.pgateway.constants.GetepayConstants;
import com.asktech.pgateway.dto.getepay.Properties;


public class GetePayUtil implements GetepayConstants {

	public static String decryptApiRequest(String encryptedRequest, List<Properties> properties) {
		String privateKeyPath = "";
		String publicKeyPath = "";

		String decryptedRequest = null;
		try {
			for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
				Properties properties2 = (Properties) iterator.next();
				if (properties2.getPropertykey() != null
						&& properties2.getPropertykey().equalsIgnoreCase(PRIVATE_KEY)) {
					privateKeyPath = properties2.getPropertyValue();
				}
				if (properties2.getPropertykey() != null && properties2.getPropertykey().equalsIgnoreCase(PUBLIC_KEY)) {
					publicKeyPath = properties2.getPropertyValue();
				}
			}
			decryptedRequest = RSAUtil.decrypt(encryptedRequest, privateKeyPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedRequest;
	}
	
	public static String encryptApiResponse(String decryptRequest, List<Properties> properties) {
		String privateKeyPath = "";
		String publicKeyPath = "";

		String encryptedRequest = null;
		try {
			for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
				Properties properties2 = (Properties) iterator.next();
				if (properties2.getPropertykey() != null
						&& properties2.getPropertykey().equalsIgnoreCase(PRIVATE_KEY)) {
					privateKeyPath = properties2.getPropertyValue();
				}
				if (properties2.getPropertykey() != null && properties2.getPropertykey().equalsIgnoreCase(PUBLIC_KEY)) {
					publicKeyPath = properties2.getPropertyValue();
				}
			}
			System.out.println("publicKeyPath :: "+publicKeyPath);
		//	System.out.println(RSAUtil.encrypt(decryptRequest, publicKeyPath).toString());
			encryptedRequest =  Base64.getEncoder().encodeToString(RSAUtil.encrypt(decryptRequest, publicKeyPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedRequest;
	}
}
