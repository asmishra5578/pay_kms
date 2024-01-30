package com.asktech.pgateway.util;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.ValidationExceptions;

public class FileValidations implements ErrorValues {
	public static void bulkFileUploadValidationForAccount(MultipartFile file, String fileType) throws ValidationExceptions {
		String docs = file.getOriginalFilename();
		if (!(getFileExtension(docs).equalsIgnoreCase(".csv") || getFileExtension(docs).equalsIgnoreCase(".xlsx"))) {
			throw new ValidationExceptions(UPLOAD_FORMATE_ERROR, FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
		}
		if (!(fileType.equalsIgnoreCase("ACCOUNT"))) {
			throw new ValidationExceptions(FILE_TYPE_ERROR, FormValidationExceptionEnums.FILE_TYPE_ERROR);
		}
	}

	public static void bulkFileUploadValidationForUPI(MultipartFile file, String fileType) throws ValidationExceptions {
		String docs = file.getOriginalFilename();
		if (!(getFileExtension(docs).equalsIgnoreCase(".csv") || getFileExtension(docs).equalsIgnoreCase(".xlsx"))) {
			throw new ValidationExceptions(UPLOAD_FORMATE_ERROR, FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
		}
		if (!(fileType.equalsIgnoreCase("UPI"))) {
			throw new ValidationExceptions(FILE_TYPE_ERROR, FormValidationExceptionEnums.FILE_TYPE_ERROR);
		}
	}

	public static String getFileExtension(String docs) {
		String extension = "";
		extension = docs.substring(docs.lastIndexOf("."));
		return extension;

	}

	static int dataCount = 0;

	@SuppressWarnings("resource")
	public static void checkFileFormate(MultipartFile file) throws ValidationExceptions, IOException {
		dataCount = 0;
		String docs = file.getOriginalFilename();
		if (getFileExtension(docs).equalsIgnoreCase(".xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet worksheet = workbook.getSheetAt(0);
			DataFormat fmt = workbook.createDataFormat();
			CellStyle textStyle = workbook.createCellStyle();
			textStyle.setDataFormat(fmt.getFormat("@"));
			worksheet.setDefaultColumnStyle(0, textStyle);
			worksheet.setDefaultColumnStyle(1, textStyle);
			worksheet.setDefaultColumnStyle(2, textStyle);
			worksheet.setDefaultColumnStyle(3, textStyle);
			worksheet.setDefaultColumnStyle(4, textStyle);
			worksheet.setDefaultColumnStyle(5, textStyle);
			worksheet.setDefaultColumnStyle(6, textStyle);
			dataCount = worksheet.getLastRowNum();
			while (0 <= worksheet.getLastRowNum()) {
				XSSFRow row = worksheet.getRow(0);
				if (!row.getCell(0).getStringCellValue().equals("phonenumber")
						|| !row.getCell(1).getStringCellValue().equals("amount")
						|| !row.getCell(2).getStringCellValue().equals("bankaccount")
						|| !row.getCell(3).getStringCellValue().equals("ifsc")
						|| !row.getCell(4).getStringCellValue().equals("beneficiaryName")
						|| !row.getCell(5).getStringCellValue().equals("requestType")
						|| !row.getCell(6).getStringCellValue().equals("purpose")) {
					throw new ValidationExceptions(UPLOAD_FORMATE_ERROR,
							FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
				}
				break;
			}
		}

	}
	@SuppressWarnings("resource")
	public static void checkFileFormateForUPI(MultipartFile file) throws IOException, ValidationExceptions {
		dataCount = 0;
		String docs = file.getOriginalFilename();
		if (getFileExtension(docs).equalsIgnoreCase(".xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
			XSSFSheet worksheet = workbook.getSheetAt(0);
			DataFormat fmt = workbook.createDataFormat();
			CellStyle textStyle = workbook.createCellStyle();
			textStyle.setDataFormat(fmt.getFormat("@"));
			worksheet.setDefaultColumnStyle(0, textStyle);
			worksheet.setDefaultColumnStyle(1, textStyle);
			worksheet.setDefaultColumnStyle(2, textStyle);
			worksheet.setDefaultColumnStyle(3, textStyle);
			worksheet.setDefaultColumnStyle(4, textStyle);
			worksheet.setDefaultColumnStyle(5, textStyle);
			dataCount = worksheet.getLastRowNum();
			while (0 <= worksheet.getLastRowNum()) {
				XSSFRow row = worksheet.getRow(0);
				if (!row.getCell(0).getStringCellValue().equals("phonenumber")
						|| !row.getCell(1).getStringCellValue().equals("amount")
						|| !row.getCell(2).getStringCellValue().equals("beneficiaryVPA")
						|| !row.getCell(3).getStringCellValue().equals("beneficiaryName")
						|| !row.getCell(4).getStringCellValue().equals("requestType")
						|| !row.getCell(5).getStringCellValue().equals("purpose")) {
					throw new ValidationExceptions(UPLOAD_FORMATE_ERROR,
							FormValidationExceptionEnums.UPLOAD_FORMATE_ERROR);
				}
				break;
			}
		}
	}
}
