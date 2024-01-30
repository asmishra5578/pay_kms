package com.asktech.pgateway.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.Fields;
import com.asktech.pgateway.constants.TransactioMethods;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.ValidationExceptions;


public class FormValidationsCashFree implements ErrorValues, Fields, TransactioMethods {
	
	static Logger logger = LoggerFactory.getLogger(FormValidationsCashFree.class);
	
	public static boolean AllFieldsValueBlank(MultiValueMap<String, String> formData) throws ValidationExceptions {
		
		// checkFields(formData, keys);
		for (String key : formData.keySet()) {
			checkData(key, formData.get(key).get(0));
			
		}
		return true;
	}

	private static boolean checkData(String key, String value) throws ValidationExceptions {
		if (value.trim().length() < 1) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		return true;
	}

	public static boolean checkAllAvailableFields(MultiValueMap<String, String> formData) throws ValidationExceptions {
		Set<String> keyvalue = formData.keySet();
		logger.info(keyvalue.toString());
		if (!keyvalue.contains(PAYMENTOPTION)) {
			throw new ValidationExceptions(PAYMENTOPTION + " Field is missing1",
					FormValidationExceptionEnums.FIELED_NOT_FOUND);
		}
		List<String> paymentopt = formData.get(PAYMENTOPTION);
		ArrayList<String> keys;
		switch (paymentopt.get(0)) {
		case CARD:
			keys = FieldDefinitionsNonSeamless.cardsFields();
			break;
		case NETBANKING:
			keys = FieldDefinitionsNonSeamless.netBankingFields();
			break;
		case UPI:
			keys = FieldDefinitionsNonSeamless.upiFields();
			break;
		case WALLET:
			keys = FieldDefinitionsNonSeamless.netBankingFields();
			break;
		default:
			throw new ValidationExceptions(paymentopt + ": Payment Option Does not exists",
					FormValidationExceptionEnums.FIELED_NOT_FOUND);
		}
		for (String key : keys) {
			logger.info("keys :: "+ key);
			if (!keyvalue.contains(key)) {
				throw new ValidationExceptions(key + " Field is missing",
						FormValidationExceptionEnums.FIELED_NOT_FOUND);
			}
		}
		return true;
	}

	public static boolean isVerifySignature(MultiValueMap<String, String> formData, String secretKey)
			throws ValidationExceptions {
		// GeneralUtils.convertMultiToRegularMap(formData);
		logger.info("Input formData :: "+formData.toString());
		String sign = "";
		boolean signverified = false;
		String signeddata = "";
		Map<String, String> m = GeneralUtils.convertMultiToRegularMap(formData);
		signeddata = m.get(Fields.SIGNATURE);
		m.remove(Fields.SIGNATURE);
		logger.info("Parameter :: "+m.toString());
		try {
			sign = EncryptSignature.encryptSignature(secretKey, m);
			logger.info("Signature :: "+sign);
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			throw new ValidationExceptions(e.getMessage(), FormValidationExceptionEnums.SIGNATURE_VERIFICATION_ERROR);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			throw new ValidationExceptions(e.getMessage(), FormValidationExceptionEnums.SIGNATURE_VERIFICATION_ERROR);
		}
		logger.info("signeddata :: "+signeddata);
		signeddata = signeddata.replace(" ", "+").replace("=", "");
		//System.out.println(sign);
		//System.out.println(signeddata);
		signverified = (sign.equals(signeddata)) ? true : false;
		return signverified;
	}
	public static boolean FormFieldValidation(MultiValueMap<String, String> formData) throws ValidationExceptions {
		 ArrayList<String> keys;
		keys = new ArrayList<String>();
		keys.add(CUSTOMERNAME);
		keys.add(CUSTOMERPHONE);
		keys.add(CUSTOMEREMAIL);		
		keys.add(SIGNATURE);
		keys.add(ORDERAMOUNT);
		keys.add(NOTIFYURL);
		keys.add(ORDERID);
		keys.add(CUSTOMERID);
		Set<String> keyvalue = formData.keySet();
		for (String key : keys) {
			if (!keyvalue.contains(key)) {
				throw new ValidationExceptions(key + " Field is missing",
						FormValidationExceptionEnums.FIELED_NOT_FOUND);
			}else {
				checkData(key, formData.get(key).get(0));
			}
		}
		return true;
	}

}
