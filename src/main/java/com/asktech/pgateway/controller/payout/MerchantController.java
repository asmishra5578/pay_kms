package com.asktech.pgateway.controller.payout;

import java.util.Map;

import com.asktech.pgateway.dto.payout.beneficiary.VerifyBankAccount;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferMerReq;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferUPIMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransactionReportMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransferStatusReq;
import com.asktech.pgateway.dto.payout.merchant.WalletTransferMerReq;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.service.payout.PayoutMerchant;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

	static Logger logger = LoggerFactory.getLogger(MerchantController.class);
	
	@Autowired
	PayoutMerchant payoutMerchant;

	@PostMapping(value = "/walletTransfer")
	public ResponseEntity<?> WalletTransfer(@RequestBody WalletTransferMerReq dto,
			@RequestHeader Map<String, String> headers) throws ValidationExceptions, JsonProcessingException {
		
		logger.info("Inside WalletTransfer() ::");
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			logger.error("Merchant id Invalid");
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		String res = payoutMerchant.WalletTransfer(dto, merchantid);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/accountTransfer")
	public ResponseEntity<?> accountTransfer(@RequestBody AccountTransferMerReq dto,
			@RequestHeader Map<String, String> headers) throws ValidationExceptions {
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		String res = payoutMerchant.AccountTransfer(dto, merchantid);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}
	
	@PostMapping(value = "/accountTransferUPI")
	public ResponseEntity<?> accountTransfer(@RequestBody AccountTransferUPIMerReq dto,
			@RequestHeader Map<String, String> headers) throws ValidationExceptions {
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		String res = payoutMerchant.AccountTransferUPI(dto, merchantid);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "/balanceCheck")
	public ResponseEntity<?> balanceCheck(@RequestHeader Map<String, String> headers) throws ValidationExceptions {
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		String res = payoutMerchant.BalanceCheck(merchantid);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/transactionReport")
	public ResponseEntity<?> transactionReport(@RequestHeader Map<String, String> headers,
			@RequestBody TransactionReportMerReq dto) throws ValidationExceptions {
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		String res = payoutMerchant.TransactionReport(merchantid, dto);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/transactionStatus")
	public ResponseEntity<?> getStatus(@RequestHeader Map<String, String> headers, @RequestBody TransferStatusReq dto)
			throws ValidationExceptions {
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		String res = payoutMerchant.TransactionStatus(dto, merchantid);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}
	
	@PostMapping(value = "/accountVerify")
	public ResponseEntity<?> accountVerify(@RequestHeader Map<String, String> headers, @RequestBody VerifyBankAccount dto)
			throws ValidationExceptions {
		String merchantid = headers.get("merchantid");
		if (merchantid.length() < 5) {
			throw new ValidationExceptions("Merchant id Invalid", FormValidationExceptionEnums.DATA_INVALID);
		}
		dto.setMerchantId(merchantid);
		String res = payoutMerchant.verifyAccount(dto);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}
}
