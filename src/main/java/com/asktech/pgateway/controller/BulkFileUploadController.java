package com.asktech.pgateway.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.FileResponseDto;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferMerReq;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferUPIMerReq;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.BulkFileUrlData;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.service.BulkFileUploadService;
import com.asktech.pgateway.util.FileValidations;
import com.asktech.pgateway.util.GoogleCaptchaAssement;
import com.asktech.pgateway.util.JwtUserValidator;

import io.swagger.annotations.ApiOperation;

@RestController
public class BulkFileUploadController implements ErrorValues {
	static Logger logger = LoggerFactory.getLogger(BulkFileUploadController.class);
	@Autowired
	JwtUserValidator jwtValidator;
	@Autowired
	BulkFileUploadService bulkFileUploadService;
	@Autowired
	GoogleCaptchaAssement googleCaptchaAssement;

	@PostMapping(value = "api/payout/bulk/accountTransfer")
	@ApiOperation(value = "Bulk file upload for accountTransfer")
	ResponseEntity<Object> bulkFileUploadForAccountTransfer(@RequestParam("merchantUuid") String merchantUuid,
			@RequestPart("file") MultipartFile file, @RequestParam("fileType") String fileType,
			@RequestHeader Map<String, String> headers) throws Exception {
		if (!googleCaptchaAssement.verifyToken(headers.get("captchaToken"))) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}
		SuccessResponseDto successResponseDto = new SuccessResponseDto();
		logger.info("Inside Controller and validate file extension..");
		FileValidations.bulkFileUploadValidationForAccount(file, fileType);
		logger.info("User Auth call............ Request: " + merchantUuid);
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(merchantUuid);
		logger.info("service call........ Request: " + merchantUuid + "file: " + file);
		FileValidations.checkFileFormate(file);
		FileResponseDto url = bulkFileUploadService.accountandUPITransferUisingBulkFileUpload(file);
		logger.info("File url==> " + url);
		BulkFileUrlData data = bulkFileUploadService.saveFile(url, user, fileType);
		logger.info("Bulk file data==> " + data.toString());
		List<AccountTransferMerReq> requestDto = bulkFileUploadService.bulkRegistrationParser(
				url.getFileData(), FileValidations.getFileExtension(file.getOriginalFilename()), data);
		bulkFileUploadService.callBulkUploadAccountTransfer(requestDto, user.getMerchantID(), data);
		successResponseDto.getMsg().add("File uploaded successfully and parse internally");
		successResponseDto.setSuccessCode(SuccessCode.API_SUCCESS);
		logger.info("File uploaded successfully and parse internally==> ");
		return ResponseEntity.ok().body(successResponseDto);
	}

	@PostMapping(value = "api/payout/bulk/upiTransfer")
	@ApiOperation(value = "Bulk file upload for UPI Transfer")
	ResponseEntity<Object> bulkFileUploadForUPITransfer(@RequestParam("merchantUuid") String merchantUuid,
			@RequestPart("file") MultipartFile file, @RequestParam("fileType") String fileType,
			@RequestHeader Map<String, String> headers) throws Exception {
		if (!googleCaptchaAssement.verifyToken(headers.get("captchaToken"))) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}
		SuccessResponseDto successResponseDto = new SuccessResponseDto();
		logger.info("Inside Controller and validate file extension..");
		FileValidations.bulkFileUploadValidationForUPI(file, fileType);
		logger.info("User Auth call............ Request: " + merchantUuid);
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(merchantUuid);
		logger.info("service call........ Request: " + merchantUuid + "file: " + file);
		FileValidations.checkFileFormateForUPI(file);
		FileResponseDto url = bulkFileUploadService.accountandUPITransferUisingBulkFileUpload(file);
		logger.info("File url==> " + url);
		BulkFileUrlData data = bulkFileUploadService.saveFile(url, user, fileType);
		logger.info("Bulk file data==> " + data.toString());
		List<AccountTransferUPIMerReq> requestDto = bulkFileUploadService.bulkParserForUPI(
				url.getFileData(), FileValidations.getFileExtension(file.getOriginalFilename()), data);
		bulkFileUploadService.callBulkUploadUPITransfer(requestDto, user.getMerchantID(), data);
		successResponseDto.getMsg().add("File uploaded successfully and parse internally");
		successResponseDto.setSuccessCode(SuccessCode.API_SUCCESS);
		logger.info("File uploaded successfully and parse internally==> ");
		return ResponseEntity.ok().body(successResponseDto);
	}

	@GetMapping("check/transfer/file/parsing/staus")
	@ApiOperation(value = "check parsing status, bulk file is parsed or not.")
	public ResponseEntity<?> checkParsingStatus(@RequestParam("fileName") String fileName)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (bulkFileUploadService.checkParsingStatus(fileName))
			sdto.getExtraData().put("status", true);
		else
			sdto.getExtraData().put("status", false);
		sdto.getMsg().add("Parsing Status");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping("api/get/all/transfer/uploaded/files")
	@ApiOperation(value = "Get all uploaded files...")
	public ResponseEntity<?> getAllTxnUpdateBulkFile(@RequestParam("merchantUuid") String merchantUuid,
			@RequestParam("fileType") String fileType)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		SuccessResponseDto sdto = new SuccessResponseDto();
		logger.info("User Auth call............ Request: " + merchantUuid);
		jwtValidator.validatebyJwtMerchantDetails(merchantUuid);
		List<BulkFileUrlData> data = bulkFileUploadService.getAllUploadedFilesBulkFile(fileType, merchantUuid);
		sdto.getMsg().add("Get all files");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("FilesList", data);
		return ResponseEntity.ok().body(sdto);
	}
}
