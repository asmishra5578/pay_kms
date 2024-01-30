package com.asktech.pgateway.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.asktech.pgateway.dto.error.ErrorResponseDto;
import com.asktech.pgateway.enums.AskTechGateway;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

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

	public static Long getEpochTIme() throws ParseException {
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
		String currentTime = crunchifyFormat.format(today);
		Date date = crunchifyFormat.parse(currentTime);
		long epochTime = date.getTime();
		return epochTime;
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

	public static ErrorResponseDto populateErrorDto(FormValidationExceptionEnums fieledNotFound, Map<String, Object> extraData, String msg,
			boolean status, int statusCode) {
		ErrorResponseDto errorResponseDto = new ErrorResponseDto();
		errorResponseDto.getMsg().add(msg);
		errorResponseDto.setStatus(status);
		errorResponseDto.setStatusCode(statusCode);

		return errorResponseDto;
	}

	public static void main(String args[]) {

		System.out.println("Card Number :: " + getMerchantsID());

	}
}
