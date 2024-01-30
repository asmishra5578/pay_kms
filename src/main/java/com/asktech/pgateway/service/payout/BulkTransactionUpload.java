package com.asktech.pgateway.service.payout;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.merchant.UploadFileResponse;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.FileLoading;
import com.asktech.pgateway.repository.FileUploadRepo;
import com.asktech.pgateway.util.FileUpload;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.payout.PayoutBulkFileProcess;

@Service
public class BulkTransactionUpload implements ErrorValues {
	@Autowired
	FileUploadRepo fileUploadRepo;
	@Autowired
	private FileUpload fileStorageService;
	
	@Autowired
	private PayoutBulkFileProcess payoutBulkFileProcess;

	static Logger logger = LoggerFactory.getLogger(BulkTransactionUpload.class);

	public SuccessResponseDto bulkAccountTransaction(MultipartFile file, String merchantid)
			throws ValidationExceptions, NoSuchAlgorithmException, EncryptedDocumentException, IOException, InvalidFormatException {
		logger.info("bulkAccountTransaction Service");
		if (!file.getContentType().contains("officedocument.spreadsheetml.sheet")) {
			throw new ValidationExceptions(INVALID_FILE_FORMAT, FormValidationExceptionEnums.INVALID_FILE_FORMAT);
		}
		if (file.getSize() > 104857600) {
			throw new ValidationExceptions(INVALID_FILE_SIZE, FormValidationExceptionEnums.INVALID_FILE_SIZE);
		}

		String fileName = fileStorageService.storeFile(file,
				Utility.randomStringGenerator(10) + "_" + merchantid + "_" + file.getOriginalFilename());

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();
		logger.info(fileName);
		String[] fl = fileName.split("\\|");
		FileLoading fileLoading = new FileLoading();
		fileLoading.setFileName(fl[0]);
		fileLoading.setFileHash(fl[1]);
		fileLoading.setFileSize(String.valueOf(file.getSize()));
		fileLoading.setFileStatus("UPLOADED");
		fileLoading.setFileType(file.getContentType());
		fileLoading.setPurpose("BANK_TRANSFER");
		fileLoading.setMerchantid(merchantid);
		fileUploadRepo.save(fileLoading);
		payoutBulkFileProcess.accountProcessing(fl[0], merchantid);
		logger.info("Sent for Processing");
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Transaction Success !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("bulkAccountTransaction", new UploadFileResponse(fl[0], fileDownloadUri, "UPLOAD SUCCESS"));	
		return sdto;

	}

	
}
