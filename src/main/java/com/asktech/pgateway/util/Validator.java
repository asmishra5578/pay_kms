package com.asktech.pgateway.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class Validator {

	static Logger logger = LoggerFactory.getLogger(Validator.class);

	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	public static boolean isValidUserName(String userName) {
		String UserName = "^[a-zA-Z]([a-zA-Z0-9._]{4,28})[a-zA-Z0-9_]$";
		Pattern ptn = Pattern.compile(UserName);
		if (userName == null)
			return false;
		return ptn.matcher(userName).matches();
	}

	public static boolean isValidCardUserName(String userName) {
		String UserName = "[a-zA-Z][^#&<>\\\"~;$^%{}?]{1,20}$";
		Pattern ptn = Pattern.compile(UserName);
		if (userName == null)
			return false;
		return ptn.matcher(userName).matches();
	}
	
	public static boolean isValidGstNumber(String gstId) {
		String number = "^[0-9]{2}[A-Z]{5}[0-9]{4}"
                + "[A-Z]{1}[1-9A-Z]{1}"
                + "Z[0-9A-Z]{1}$"; 
		Pattern ptn = Pattern.compile(number);
		if (gstId == null)
			return false;
		return ptn.matcher(gstId).matches();
	}
	
	public static boolean isValidWebUrl(String url) {
		String number = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)";
		Pattern ptn = Pattern.compile(number);
		if (url == null)
			return false;
		return ptn.matcher(url).matches();
	}
	
	public static boolean isValidAlphaNumber(String str) {
		String number = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$"; 
		Pattern ptn = Pattern.compile(number);
		if (str == null)
			return false;
		return ptn.matcher(str).matches();
	}
	
	public static boolean isValidPANNumber(String PanNum) {
		String number = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
		Pattern ptn = Pattern.compile(number);
		if (PanNum == null)
			return false;
		return ptn.matcher(PanNum).matches();
	}

	public static boolean isValidName(String name) {
		// String UserName = "^[A-Za-z\\\\s]+$";
		String UserName = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}";
		Pattern ptn = Pattern.compile(UserName);
		if (name == null)
			return false;
		return ptn.matcher(name).matches();
	}

	public static boolean isValidPhoneNumber(String phonenumber) {
		String PhoneNumber = "^([0-9]{10})$";
		Pattern ptn = Pattern.compile(PhoneNumber);
		if (phonenumber == null)
			return false;
		return ptn.matcher(phonenumber).matches();
	}

	public static boolean isValidFirstName(String firstname) {
		String FirstName = "^[a-zA-Z]+$";
		Pattern ptn = Pattern.compile(FirstName);
		if (firstname == null)
			return false;
		return ptn.matcher(firstname).matches();
	}

	public static boolean isValidLastName(String lastname) {
		String LastName = "^[a-zA-Z]+$";
		Pattern ptn = Pattern.compile(LastName);
		if (lastname == null)
			return false;
		return ptn.matcher(lastname).matches();
	}

	public static boolean isValidPinCode(String pincode) {
		String PinCode = "^[1-9][0-9]{5}$";
		Pattern ptn = Pattern.compile(PinCode);
		if (pincode == null)
			return false;
		return ptn.matcher(pincode).matches();
	}

	public static boolean isValidIP(String ipAddress) {
		String IpAddress = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
		Pattern ptn = Pattern.compile(IpAddress);
		if (ipAddress == null)
			return false;
		return ptn.matcher(ipAddress).matches();
	}

	public static String generteTicketId() {
		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder("TicketId-" + (100000 + rnd.nextInt(900000)));
		for (int i = 0; i < 5; i++)
			sb.append(chars[rnd.nextInt(chars.length)]);

		return sb.toString();
	}

	public static boolean validateFileExtension(MultipartFile file) {
		String documents = file.getOriginalFilename();
		if (getFileExtension(documents).equalsIgnoreCase(".jpg")
				|| getFileExtension(documents).equalsIgnoreCase(".jpeg")
				|| getFileExtension(documents).equalsIgnoreCase(".png")
				|| getFileExtension(documents).equalsIgnoreCase(".pdf")
				|| getFileExtension(documents).equalsIgnoreCase(".docx")) {
			return true;
		} else
			return false;
	}

	private static String getFileExtension(String documents) {
		String extension = "";
		extension = documents.substring(documents.lastIndexOf("."));
		logger.info("File Extention :: " + extension);
		return extension;
	}

	public static boolean validateBulkFileExtension(MultipartFile file) {
		String documents = file.getOriginalFilename();
		logger.info("File Name :: " + documents);
		if (getFileExtension(documents).equalsIgnoreCase(".xlsx")) {
			logger.info("File Extension validation Success");
			return true;
		} else {
			logger.info("File Extension validation Failed");
			return false;
		}
	}

	public static boolean isValidateIdType(String idType) {
		if (idType.equals("AADHAAR") || idType.equals("VOTER_ID") || idType.equals("DRIVING_LICENSE")
				|| idType.equals("PAN") || idType.equals("PASSPORT") || idType.equals("MOBILE_NUMBER")) {
			return true;
		}
		return false;
	}

	public static int isValidateIdNumber(String idNumber, String idType) {
		int data = 0;
		if (idType.equals("AADHAAR")) {
			String IDNumber = "^([0-9]{12}|[0-9]{16})$";
			Pattern ptn = Pattern.compile(IDNumber);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}
		if (idType.equals("VOTER_ID")) {
			String IDNumber = "^([a-zA-Z]){3}([0-9]){7}?$";
			Pattern ptn = Pattern.compile(IDNumber);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}
		if (idType.equals("DRIVING_LICENSE")) {
			String IDNumber = "^([a-zA-Z]){2}(([0-9]){13}|([0-9]){12})$";
			Pattern ptn = Pattern.compile(IDNumber);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}
		if (idType.equals("PAN")) {
			String IDNumber = "^([A-Z]{5}[0-9]{4}[A-Z]{1})$";
			Pattern ptn = Pattern.compile(IDNumber);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}
		if (idType.equals("PASSPORT")) {
			String IDNumber = "^([A-Z]{1}[0-9]{7})$";
			Pattern ptn = Pattern.compile(IDNumber);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}
		if (idType.equals("MOBILE_NUMBER")) {
			String IDNumber = "^([0-9]{10})$";
			Pattern ptn = Pattern.compile(IDNumber);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}
		if (idType.equals("UPI")) {
			String IDNumber = "^(.+)@(.+)$";
			Pattern ptn = Pattern.compile(IDNumber, Pattern.CASE_INSENSITIVE);
			if (ptn.matcher(idNumber).matches()) {
				data = 1;
				return data;
			}
		}

		if (idType.equals("CARD")) {
			Long number = (long) 0;
			try {
				number = Long.parseLong(idNumber);
			} catch (Exception e) {
				return data;
			}
			if (getSize(number) >= 13 && getSize(number) <= 16) {
				if (prefixMatched(number, 4) || prefixMatched(number, 5) || prefixMatched(number, 37)
						|| prefixMatched(number, 6) || prefixMatched(number, 8)) {
					data = 1;
					return data;
				}
			}

		}
		return data;
	}

	// private static final DateTimeFormatter dateFormatter =
	// DateTimeFormatter.ofPattern("uuuuMMdd");
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");

	public static boolean isValidateDob(String dob) {
		int valid = 0;
		// String ddob = dob.replaceAll("-", "").replaceAll("/", "");
		LocalDate rdob = LocalDate.parse(dob, dateFormatter);
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Dushanbe"));
		if (rdob.isAfter(today)) {
			valid = 0;
		} else {
			int age = (int) ChronoUnit.YEARS.between(rdob, today);
			if (age < 18) {
				valid = 0;
			} else {
				valid = 1;
			}
		}
		if (valid == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean validDouble(String strDouble) {

		try {
			Double.parseDouble(strDouble);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isValidateDateFormat(String dateStr) {
		/*
		 * int valid = 0; String ddob = dateStr.replaceAll("-", "").replaceAll("/", "");
		 * LocalDate rdob = LocalDate.parse(ddob, dateFormatter); LocalDate today =
		 * LocalDate.now(ZoneId.of("Asia/Dushanbe"));
		 * System.out.println(" dateStr :: "+dateStr +", '"); if (rdob.isAfter(today)) {
		 * valid = 1; } if (valid == 1) { return true; } else { return false; }
		 */

		DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = originalFormat.parse(dateStr);
			return true;
		} catch (ParseException e) {
			return false;
		}

	}

	public static boolean isValidateGender(String gender) {
		if (gender.equals("M") || gender.equals("F")) {
			return true;
		}
		return false;
	}

	public static boolean isValidateCardType(String cardType) {
		if (cardType.equals("P") || cardType.equals("V")) {
			return true;
		}
		return false;
	}

	public static boolean isValidatePassword(String newPass) {
		String password = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
		Pattern ptn = Pattern.compile(password);
		if (newPass == null)
			return false;
		return ptn.matcher(newPass).matches();
	}
	
	public static boolean isValidMerchantOrderId(String newPass) {
		String password = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-_.]).{8,16}$";
		Pattern ptn = Pattern.compile(password);
		if (newPass == null)
			return false;
		return ptn.matcher(newPass).matches();
	}

	public static boolean isValidProxyNumber(String proxynumber) {
		String ProxyNumber = "^([0-9]{12}|[0-9]{11}|[0-9]{10}|[0-9]{9}|[0-9]{8})$";
		Pattern ptn = Pattern.compile(ProxyNumber);
		if (proxynumber == null)
			return false;
		return ptn.matcher(proxynumber).matches();
	}

	public static boolean isValidCardNumberNumber(String cardNumber) {
		String CardNumber = "^([0-9]{16})$";
		Pattern ptn = Pattern.compile(CardNumber);
		if (cardNumber == null)
			return false;
		return ptn.matcher(cardNumber).matches();
	}

	public static boolean isValidKitNo(String cardNumber) {
		String CardNumber = "^([0-9]{9})$";
		Pattern ptn = Pattern.compile(CardNumber);
		if (cardNumber == null)
			return false;
		return ptn.matcher(cardNumber).matches();
	}

	public static boolean isValidateTextField(String str) {
		String testPattern = "^[a-zA-Z]+$";
		Pattern ptn = Pattern.compile(testPattern);
		if (str == null)
			return false;
		return ptn.matcher(str).matches();
	}

	public static <E extends Enum<E>> boolean containsEnum(Class<E> _enumClass, String value) {
		try {
			return EnumSet.allOf(_enumClass).contains(Enum.valueOf(_enumClass, value));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	public static boolean prefixMatched(long number, int d) {
		return getPrefix(number, getSize(d)) == d;
	}

	// Return the number of digits in d
	public static int getSize(long d) {
		String num = d + "";
		return num.length();
	}

	// Return the first k number of digits from
	// number. If the number of digits in number
	// is less than k, return number.
	public static long getPrefix(long number, int k) {
		if (getSize(number) > k) {
			String num = number + "";
			return Long.parseLong(num.substring(0, k));
		}
		return number;
	}
	
	public static boolean isValidIfsc(String val) {
		if (val == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[A-Z|a-z]{4}[0][a-zA-Z0-9]{6}$");
		return pattern.matcher(val).matches();
	}
	
	public static boolean isValidAccountNumber(String val) {
		if (val == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[0-9]{9,18}$");
		return pattern.matcher(val).matches();
	}
	
	public static boolean isValidMICR(String val) {
		if (val == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("^\\d{9}$");
		return pattern.matcher(val).matches();
	}
}
