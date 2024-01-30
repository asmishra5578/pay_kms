package com.asktech.pgateway.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferMerReq;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferUPIMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransactionReportMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransactionRequestFilterMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransferStatusReq;
import com.asktech.pgateway.dto.payout.merchant.WalletTransferMerReq;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.service.payout.PayoutMerchant;
import com.asktech.pgateway.util.GoogleCaptchaAssement;
import com.asktech.pgateway.util.JwtUserValidator;
import com.asktech.pgateway.util.Utility;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController()
@RequestMapping("/api/payout")
public class PGMerchantPayout implements ErrorValues {
	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);
	@Autowired
	PayoutMerchant payoutMerchant;

	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;

	@Autowired
	JwtUserValidator jwtValidator;
	@Autowired
	GoogleCaptchaAssement googleCaptchaAssement;

	@PostMapping(value = "/walletTransfer")
	@ApiOperation(value = "Wallet Transfer.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> WalletTransfer(@RequestBody WalletTransferMerReq dto,
			@RequestHeader Map<String, String> headers) throws ValidationExceptions, JsonProcessingException,
			ParseException, UserException, JWTException, SessionExpiredException {
		logger.info("Wallet Transfer");
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		logger.info(uuid);
		String merchantid = user.getMerchantID();
		logger.info(merchantid);
		dto.setOrderid(Utility.getRandomId());
		String res = payoutMerchant.WalletTransfer(dto, merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/accountTransfer")
	@ApiOperation(value = "Account Transfer.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> accountTransfer(@RequestBody List<AccountTransferMerReq> dto,
			@RequestHeader Map<String, String> headers)
			throws ParseException, UserException, JWTException, SessionExpiredException, IOException,
			ValidationExceptions {
		if (!googleCaptchaAssement.verifyToken(headers.get("captchaToken"))) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}
		dto.forEach(o -> {
			try {
				o.setOrderid(Utility.getRandomId());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		logger.info(uuid);
		String merchantid = user.getMerchantID();
		List<String> res = new ArrayList<>();
		dto.forEach(o -> {
			String oneByOneRes = payoutMerchant.AccountTransfer(o, merchantid);
			res.add(oneByOneRes);
		});
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/upiTransfer")
	@ApiOperation(value = "Account Transfer.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> upiTransfer(@RequestBody List<AccountTransferUPIMerReq> dto,
			@RequestHeader Map<String, String> headers)
			throws ParseException, UserException, JWTException, SessionExpiredException, IOException, ValidationExceptions {
		if (!googleCaptchaAssement.verifyToken(headers.get("captchaToken"))) {
			throw new ValidationExceptions(CAPTCHA_VALIDATION_ERROR,
					FormValidationExceptionEnums.CAPTCHA_VALIDATION_ERROR);
		}
		dto.forEach(o -> {
			try {
				o.setOrderid(Utility.getRandomId());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		String merchantid = user.getMerchantID();
		List<String> res = new ArrayList<>();
		dto.forEach(o -> {
			String oneByOneRes = payoutMerchant.AccountTransferUPI(o, merchantid);
			res.add(oneByOneRes);
		});
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@GetMapping(value = "/balanceCheck")
	@ApiOperation(value = "Payout balance check", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> balanceCheck(@RequestHeader Map<String, String> headers)
			throws UserException, JWTException, SessionExpiredException {
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		String merchantid = user.getMerchantID();
		String res = payoutMerchant.BalanceCheck(merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/transactionReport")
	@ApiOperation(value = "Transaction Report", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> transactionReport(@RequestHeader Map<String, String> headers,
			@RequestBody TransactionReportMerReq dto) throws UserException, JWTException, SessionExpiredException {
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		String merchantid = user.getMerchantID();
		String res = payoutMerchant.TransactionReport(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/walletReport")
	@ApiOperation(value = "Wallet Report", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> walletReport(@RequestHeader Map<String, String> headers,
			@RequestBody TransactionReportMerReq dto) throws UserException, JWTException, SessionExpiredException {
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		String merchantid = user.getMerchantID();
		String res = payoutMerchant.WalletReport(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/transactionStatus")
	@ApiOperation(value = "Transaction Status", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getStatus(@RequestHeader Map<String, String> headers, @RequestBody TransferStatusReq dto)
			throws UserException, JWTException, SessionExpiredException {
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		String merchantid = user.getMerchantID();
		String res = payoutMerchant.TransactionStatus(dto, merchantid);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	@PostMapping(value = "/transactionReportFilter")
	@ApiOperation(value = "Transaction Report", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> transactionReportFilter(@RequestHeader Map<String, String> headers,
			@RequestBody TransactionRequestFilterMerReq dto)
			throws UserException, JWTException, SessionExpiredException {
		String uuid = headers.get("merchantid");
		MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
		String merchantid = user.getMerchantID();
		String res = payoutMerchant.TransactionReportFilter(merchantid, dto);

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/json").body(res);
	}

	private String getMerchantIdFromUuid(String uuid) {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);
		if (merchantDetails.getMerchantID() != null) {
			return merchantDetails.getMerchantID();
		} else {
			return "NF";
		}
	}

	// @GetMapping(value = "api/admin/transactionFilterReport")
	// @ApiOperation(value = "Transaction Report", authorizations = {
	// @Authorization(value = "apiKey") })
	// public ResponseEntity<?> transactionFilterReport(@RequestHeader Map<String,
	// String> headers,
	// @RequestBody TransactionFilterReq dto) throws UserException, JWTException,
	// SessionExpiredException {
	//
	// String uuid = headers.get("merchantid");
	// MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
	// String merchantid = user.getMerchantID();
	// String res = payoutMerchant.transactionFilter(dto);
	//
	// return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,
	// "application/json").body(res);
	// }
	// @GetMapping(value = "api/admin/walletFilterReport")
	// @ApiOperation(value = "Transaction Report", authorizations = {
	// @Authorization(value = "apiKey") })
	// public ResponseEntity<?> walletFilterReport(@RequestHeader Map<String,
	// String> headers,
	// @RequestBody WalletFilterReq dto) throws UserException, JWTException,
	// SessionExpiredException {
	//
	// String uuid = headers.get("merchantid");
	// MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
	// String merchantid = user.getMerchantID();
	// String res = payoutMerchant.walletFilter(dto);
	//
	// return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,
	// "application/json").body(res);
	// }

	// @GetMapping(value = "/wallet7DaysReport")
	// @ApiOperation(value = "Transaction Report", authorizations = {
	// @Authorization(value = "apiKey") })
	// public ResponseEntity<?> wallet7DaysReport(@RequestHeader Map<String, String>
	// headers) throws UserException, JWTException, SessionExpiredException {
	//
	// String uuid = headers.get("merchantid");
	// MerchantDetails user = jwtValidator.validatebyJwtMerchantDetails(uuid);
	// String merchantid = user.getMerchantID();
	// String res = payoutMerchant.wallet7days(merchantid);
	//
	// return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,
	// "application/json").body(res);
	// }

}
