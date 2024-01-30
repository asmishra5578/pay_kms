package com.asktech.pgateway.util.nsdl;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.internal.LinkedTreeMap;

public class DataHashing {

	public static String getHashValue(Map<Object, Object> fields, String sSalt) throws Exception {

		String sHashString = null;

		try {

			while (fields.values().remove(null));

			Map<Object, Object> myMap = new LinkedHashMap<>();

			for (Object str : fields.keySet()) {

				if (fields.get(str).getClass() == LinkedTreeMap.class) {

					for (Object str2 : ((LinkedTreeMap) fields.get(str)).keySet()) {

						if (((LinkedTreeMap) fields.get(str)).get(str2).getClass() == LinkedTreeMap.class) {

							for (Object str3 : ((LinkedTreeMap) ((LinkedTreeMap) fields.get(str)).get(str2)).keySet()) {
								myMap.put(str3.toString(), ((LinkedTreeMap) ((LinkedTreeMap) fields.get(str)).get(str2))
										.get(str3).toString());
							}

						} else {
							myMap.put(str2.toString(), ((LinkedTreeMap) fields.get(str)).get(str2).toString());
						}
					}

				} else {
					myMap.put(str, fields.get(str).toString());
				}

			}

			List fieldNames = new ArrayList(myMap.keySet());
			Collections.sort(fieldNames);

			System.out.println("fieldNames for hashing -> " + fieldNames);

			StringBuffer buf = new StringBuffer();
			buf.append(sSalt);

			Iterator itr = fieldNames.iterator();
			while (itr.hasNext()) {
				Object fieldName = (Object) itr.next();
				Object fieldValue = (Object) myMap.get(fieldName);
				if ((fieldValue != null) && !fieldValue.equals("")) {
					buf.append(fieldValue);
				}
			}

			System.out.println("buffer -> " + buf);

			StringBuffer hexString = new StringBuffer();

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(buf.toString().getBytes("UTF-8"));
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			sHashString = hexString.toString();
		} catch (Exception e) {

			System.out.println("Exception in getHashValue -> [" + e + "]");
			e.printStackTrace();

			throw e;

		}

		return sHashString;
	}
}
