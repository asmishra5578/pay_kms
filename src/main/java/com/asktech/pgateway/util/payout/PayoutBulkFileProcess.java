package com.asktech.pgateway.util.payout;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.model.payout.PayoutBulkTransaction;
import com.asktech.pgateway.repository.payout.PayoutBulkTransactionRepo;
import com.asktech.pgateway.util.FileUpload;
import com.asktech.pgateway.util.Validator;

@Component
public class PayoutBulkFileProcess {
	static Logger logger = LoggerFactory.getLogger(PayoutBulkFileProcess.class);

	@Autowired
	private PayoutBulkTransactionRepo payoutBulkTransactionRepo;
	@Autowired
	private FileUpload fileStorageService;

	@Async
	public void accountProcessing(String filename, String merchantid) throws EncryptedDocumentException, IOException, InvalidFormatException {
		logger.info("Running File::" + filename);

		Workbook workbook = WorkbookFactory.create(fileStorageService.getFileAsFileObject(filename));
		Sheet sheet = workbook.getSheetAt(0);
		DataFormatter dataFormatter = new DataFormatter();
		int j = 0;
		for (Row row : sheet) {
			if (j == 0) {
				logger.info("skipped first row.");
				j++;
			} else {
				int i = 0;
				boolean fl = true;
				String msg = "";
				PayoutBulkTransaction payoutBulkTransaction = new PayoutBulkTransaction();
				payoutBulkTransaction.setMerchantId("");
				payoutBulkTransaction.setServiceType("ACCOUNT_TRANSFER");
				payoutBulkTransaction.setFileName(filename);
				payoutBulkTransaction.setMerchantId(merchantid);
				for (Cell cell : row) {
					String cellValue = dataFormatter.formatCellValue(cell);
					switch (i) {
					case 0:
						if (!Validator.isValidPhoneNumber(cellValue)) {
							msg = "Invalid Phone Number";
							fl = false;
						}
						payoutBulkTransaction.setPhnumber(cellValue);
						break;
					case 1:
						if (!Validator.validDouble(cellValue)) {
							msg = msg + "|Invalid Amount, only double allowed";
							fl = false;
						}
						payoutBulkTransaction.setAmount(cellValue);
						break;
					case 2:
						if (!Validator.isNumeric(cellValue)) {
							msg = msg + "|Invalid Account Number";
							fl = false;
						}
						payoutBulkTransaction.setBankaccount(cellValue);
						break;
					case 3:
						if (!Validator.isValidIfsc(cellValue)) {
							msg = msg + "|Invalid IFSC Code";
							fl = false;
						}
						payoutBulkTransaction.setIfsc(cellValue);
						break;
					case 4:
						if (!Validator.isValidName(cellValue)) {
							msg = msg + "|Invalid Customer Name";
							fl = false;
						}
						payoutBulkTransaction.setBeneficiaryName(cellValue);
						break;
					case 5:
						String optionlist = "NEFT, IMPS, RTGS";
						if (!optionlist.contains(cellValue)) {
							msg = msg + "|Invalid Payment Option";
							fl = false;
						}
						payoutBulkTransaction.setRequestType(cellValue);
						break;
					default:
						msg = msg + "|Invalid Data";
						break;
					}

					System.out.print(cellValue + "\t");
					i++;
				}

				if (fl == true) {
					payoutBulkTransaction.setFileMsg("LOAD_SUCCESS");
					payoutBulkTransaction.setStatus("LOADED");
				} else {
					payoutBulkTransaction.setFileMsg(msg);
					payoutBulkTransaction.setStatus("DATA_ERROR");
				}
				payoutBulkTransactionRepo.save(payoutBulkTransaction);
				j++;
			}
		}
		workbook.close();

	}
}
