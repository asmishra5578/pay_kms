package com.asktech.pgateway.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.asktech.pgateway.dto.error.ErrorResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {
	private final static String LOCALHOST_IPV4 = "127.0.0.1";
	private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static Instant getTimestamp() {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Instant instant = timestamp.toInstant();

		return instant;
	}

	public static String convertDTO2JsonString(Object json) throws JsonProcessingException {
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(json);
		return jsonStr;
	}

	public static boolean validateBalance(int fromBalabce, int toBalance) {

		if (fromBalabce >= toBalance) {
			return true;
		}

		return false;
	}

	public static boolean check24HrsPassed(String val) {
		long dt = (Long.parseLong(val) / 1000);
		long curtime = Instant.now().getEpochSecond();		
		if (curtime - dt > 105061) {			
			return true;
		}
		return false;
	}
	
	public static Long getEpochTIme() throws ParseException {
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
		String currentTime = crunchifyFormat.format(today);
		Date date = crunchifyFormat.parse(currentTime);
		long epochTime = date.getTime();
		return epochTime;
	}
	
	public static String getRandomId() throws ParseException {
		String str = String.valueOf(getEpochTIme())+UUID.randomUUID().toString().split("-")[0];
		return str.toUpperCase();
	}
	public static String populateDbTime() {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strDate = dateFormat.format(date);
		return strDate;
	}
	public static String beautifyJson(String strJson) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(strJson);
		return null;
	}

	public static String getJsonFileCreate(JSONObject jsonObject, String requestType) throws ParseException {

		// JSONObject jsonObject = new JSONObject(jsonStr);
		String fileName = requestType + "_" + getEpochTIme() + ".json";
		System.out.println("File Name :: " + fileName);
		try (FileWriter file = new FileWriter("/home/asktech/AskTech/Webhook/" + fileName)) {
			// try (FileWriter file = new
			// FileWriter("/home/asktech/AskTech/Webhook/fromPostMan/"+fileName)) {
			file.write(jsonObject.toString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private static int inc = 0;

	public static long getMerchantsID() {
		// 12 digits.

		long id = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(1, 13));
		inc = (inc + 1) % 10;
		return id;
	}

	public static String generateAppId() {

		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString().replace("-", "");

		return uuidAsString;
	}

	public static ErrorResponseDto populateErrorDto(FormValidationExceptionEnums fieledNotFound,
			Map<String, Object> extraData, String msg, boolean status, int statusCode) {
		ErrorResponseDto errorResponseDto = new ErrorResponseDto();
		errorResponseDto.getMsg().add(msg);
		errorResponseDto.setStatus(status);
		errorResponseDto.setStatusCode(statusCode);

		return errorResponseDto;
	}

	public static String convertDatetoMySqlDateFormat(String dateIn) throws ParseException {

		DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = originalFormat.parse(dateIn);
		String formattedDate = targetFormat.format(date);

		return formattedDate;
	}

	public static Integer randomNumberForOtp(int sizeOfOtp) {

		int rand = (new Random()).nextInt(90000000) + 10000000;
		return rand;
	}

	public static String maskCardNumber(String cardNumber) {
		//logger.info("maskCardNumber() :: "+cardNumber);
		if(cardNumber == null || StringUtils.isEmpty(cardNumber)) {
			//logger.info("Inside Null check Block maskCardNumber() :: "+cardNumber);
			return "";
		}
		StringBuilder maskedNumber = new StringBuilder();		
		
		for(int i=0 ; i< cardNumber.length() ; i++) {			
			if(i < cardNumber.length()-4) {
				maskedNumber.append("X");
			}else {
				maskedNumber.append(cardNumber.charAt(i));
			}
		}
		
		return maskedNumber.toString();
	}

	public static String getAmountConversion(String amount) {

		return String.format("%.2f", Double.parseDouble(amount) / 100);

	}

	public static String randomStringGenerator(int sizeOfString) {

		return RandomStringUtils.randomAlphanumeric(sizeOfString);

	}
	
	public static String maskUpiCode(String upiCode) {
		if(upiCode == null || StringUtils.isEmpty(upiCode)) {
			return "";
		}
		return upiCode.substring(0,upiCode.lastIndexOf("@")).replaceAll("\\S", "*")+upiCode.substring(upiCode.lastIndexOf("@"));
	}
	
	public static boolean validateIFSCCode(String strIFCS) {
		String regex = "^[A-Z]{4}0[A-Z0-9]{6}$";
		return strIFCS.matches(regex); 
	}
	
	public static boolean checkNumericValue(String strValue) {
		String regex = "[0-9]+";
		return strValue.matches(regex);
	}
	
	
	public static String getClientIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}

		if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}

		if (!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
				try {
					InetAddress inetAddress = InetAddress.getLocalHost();
					ipAddress = inetAddress.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
		if (ipAddress != null) {
			if (!StringUtils.hasLength(ipAddress) && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}

		return ipAddress;
	}
	
	public static JSONObject convertStringToJSONObject(String strJson) {
		JSONObject json = new JSONObject(strJson);  
		
		return json;
	}
	public static String getMerchantOrderId() {
		String uuid =UUID.randomUUID().toString().substring(0,16);                 
		return uuid;
	}
	public static boolean inArray(String[] arr, String val) {
		return Arrays.stream(arr).anyMatch(val::equals);

	}
}
