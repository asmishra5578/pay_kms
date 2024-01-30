package com.asktech.pgateway.service.merchantApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.asktech.pgateway.constants.EasebuzzConstants;
import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.GetepayConstants;
import com.asktech.pgateway.constants.OnePayConstants;
import com.asktech.pgateway.constants.paytm.PaytmConstants;
import com.asktech.pgateway.constants.payu.PayUConstants;
import com.asktech.pgateway.dto.TransactionDto;
import com.asktech.pgateway.dto.asanpay.AsanPayTransactionStatus;
import com.asktech.pgateway.dto.cashfree.TransactionResponse;
import com.asktech.pgateway.dto.easebuzz.EaseBuzzTransactionResponse;
import com.asktech.pgateway.dto.getepay.Properties;
import com.asktech.pgateway.dto.getepay.TransactionRequeryRequest;
import com.asktech.pgateway.dto.getepay.TransactionStatusResponse;
import com.asktech.pgateway.dto.grezpay.GrezPayTransactionStatus;
import com.asktech.pgateway.dto.letzpay.LetzTransactionStatusResponse;
import com.asktech.pgateway.dto.merchant.Cards;
import com.asktech.pgateway.dto.merchant.ExposeCashfreeApi;
import com.asktech.pgateway.dto.merchant.MerchantRefundResponse;
import com.asktech.pgateway.dto.merchant.MerchantTransaction;
import com.asktech.pgateway.dto.merchant.MerchantTransactionResponse;
import com.asktech.pgateway.dto.onePay.OnePayTransactionResponse;
import com.asktech.pgateway.dto.paytm.PaytmStatusResponse;
import com.asktech.pgateway.dto.payu.PayUStatusResponse;
import com.asktech.pgateway.dto.setu.SetuErrorResponse;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.CashfreeCards;
import com.asktech.pgateway.model.EaseBuzzTransactionDetails;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.OnePayTransactionDetails;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.RefundDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.CashFreeCardRepository;
import com.asktech.pgateway.repository.EaseBuzzTransactionDetailsRepository;
import com.asktech.pgateway.repository.MerchantBalanceSheetRepository;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.OnePayTransactionDetailsRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.RefundDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.security.SecurityExposerFunction;
import com.asktech.pgateway.service.PaymentMerchantService;
import com.asktech.pgateway.service.merchantApi.statusApis.LetzpayStatus;
import com.asktech.pgateway.service.merchantApi.statusApis.NsdlStatus;
import com.asktech.pgateway.service.merchantApi.statusApis.PaygStatusapi;
import com.asktech.pgateway.service.merchantApi.statusApis.RazorPayStatusSchedular;
import com.asktech.pgateway.service.merchantApi.statusApis.SabPaisaStatus;
import com.asktech.pgateway.util.GeneralUtils;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.getepay.GetePayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.pg.merchant.PaytmChecksum;

import io.jsonwebtoken.Claims;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ServiceMerchantApiExposer implements ErrorValues {

	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantBalanceSheetRepository merchantBalanceSheetRepository;
	@Autowired
	RefundDetailsRepository refundDetailsRepository;
	@Autowired
	PaymentMerchantService paymentMerchantService;
	@Autowired
	EaseBuzzTransactionDetailsRepository easeBuzzTransactionDetailsRepository;
	@Autowired
	CashFreeCardRepository cashFreeCardRepository;
	@Autowired
	OnePayTransactionDetailsRepository onePayTransactionDetailsRepository;

	static Logger logger = LoggerFactory.getLogger(ServiceMerchantApiExposer.class);

	public MerchantTransaction transactionStatus(String tokenInfo, MultiValueMap<String, String> formData)
			throws ValidationExceptions, NoSuchAlgorithmException, JsonProcessingException {
		logger.info("MerchantAPIService transactionStatus In this Method.");
		logger.info("transactionStatus ::" + GeneralUtils.MultiValueMaptoJson(formData));
		SecurityExposerFunction securityExposerFunction = new SecurityExposerFunction();
		MerchantTransaction merchantTransaction = new MerchantTransaction();

		logger.info("tokenInfo :: " + tokenInfo);
		logger.info("strBody :: " + formData);

		Claims claimBody = securityExposerFunction.decodeJWT(tokenInfo);
		MerchantDetails merchantDetails = merchantDetailsRepository
				.findByAppID(Encryption.encryptCardNumberOrExpOrCvv(claimBody.get("appId").toString()));

		logger.info(Utility.convertDTO2JsonString(claimBody));
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		logger.info("Before Verify the Token with Secrect key and Signature");
		String SECRET = Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId()).trim().strip()
				.replace("\u0000", "");
		logger.info("SECRET::" + SECRET);
		securityExposerFunction.decodeJWTwithSignature(tokenInfo, SECRET);
		logger.info("Verification done from Signature with JWT");
		logger.info("claimBody.checkSum :: " + claimBody.get("checkSum"));
		logger.info("Generate the CheckSum :: "
				+ SecurityExposerFunction.generateCheckSum(populatedMultiPleMapToMap(formData), SECRET)
				+ populatedMultiPleMapToMap(formData));

		if (!validateCheckSum(claimBody.get("checkSum").toString(),
				SecurityExposerFunction.generateCheckSum(populatedMultiPleMapToMap(formData), SECRET))) {
			throw new ValidationExceptions(CHECKSUM_MISMATCH, FormValidationExceptionEnums.CHECKSUM_MISMATCH);
		}

		logger.info("CheckSum Validation done ...." + merchantDetails.getMerchantID() + "|"
				+ formData.get("orderId").get(0));

		List<TransactionDetails> listTransactionDetails = transactionDetailsRepository
				.findAllByMerchantOrderIdAndMerchantId(formData.get("orderId").get(0), merchantDetails.getMerchantID());

		List<MerchantTransactionResponse> listMerchantTransactionResponse = populateTransactionDetails(
				listTransactionDetails);

		merchantTransaction.setListMerchantTransactionResponse(listMerchantTransactionResponse);
		merchantTransaction.setHeader(createdHeader(listMerchantTransactionResponse.toString(), SECRET));

		return merchantTransaction;
	}

	public MerchantRefundResponse generateRefundRequest(String appId, String secret, String orderId)
			throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException {

		logger.info("MerchantAPIService generateRefundRequest In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByAppID(appId);
		logger.info(Utility.convertDTO2JsonString(appId));
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		logger.info("Before Verify the Token with Secrect key and Signature");

		logger.info("Check sum validation success ...");
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository
				.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(merchantDetails.getMerchantID(),
						orderId, UserStatus.PENDING.toString());

		if (merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_INITIATE_FAILED, FormValidationExceptionEnums.REFUND_INITIATE_FAILED);
		}

		RefundDetails refundDetails = new RefundDetails();
		refundDetails.setAmount(String.valueOf(merchantBalanceSheet.getAmount()));
		refundDetails.setInitiatedBy(merchantDetails.getUuid());
		refundDetails.setMerchantId(merchantDetails.getMerchantID());
		refundDetails.setMerchantOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setPaymentCode(merchantBalanceSheet.getPaymentCode());
		refundDetails.setPaymentMode(merchantBalanceSheet.getPaymentMode());
		refundDetails.setPaymentOption(merchantBalanceSheet.getTrType());
		refundDetails.setPgOrderId(merchantBalanceSheet.getPgOrderId());
		refundDetails.setPgStatus(merchantBalanceSheet.getPgStatus());
		refundDetails.setRefOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setStatus(UserStatus.INITIATED.toString());
		refundDetails.setVpaUpi(merchantBalanceSheet.getVpaUPI());
		refundDetails.setUserId(merchantBalanceSheet.getUserId());

		RefundDetails refundDetailsUpd = refundDetailsRepository.save(refundDetails);
		MerchantRefundResponse merchantRefundResponse = new MerchantRefundResponse();
		merchantRefundResponse.setRefundDetails(refundDetailsUpd);
		// merchantRefundResponse.setHeader(createdHeader(refundDetailsUpd.toString(),
		// Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId())));

		return merchantRefundResponse;
	}

	public ExposeCashfreeApi cardDetail(Cards cardDetails) {
		ExposeCashfreeApi card = new ExposeCashfreeApi();
		CashfreeCards cardDet = new CashfreeCards();
		cardDet.setBank(cardDetails.getBank());
		cardDet.setCountryCode(cardDetails.getCountryCode());
		cardDet.setScheme(cardDetails.getScheme());
		cardDet.setSubType(cardDetails.getSubType());
		cardDet.setType(cardDetails.getType());

		cashFreeCardRepository.save(cardDet);
		card.setCards(cardDet);
		card.setStatus("OK");

		return card;
	}

	public Map<String, String> populatedMultiPleMapToMap(MultiValueMap<String, String> formData) {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("orderId", formData.get("orderId").get(0));

		return parameters;
	}

	public boolean validateCheckSum(String inputCheckSum, String generatedCheckSum) {

		if (inputCheckSum.equals(generatedCheckSum)) {
			return true;
		}

		return false;
	}

	public List<MerchantTransactionResponse> populateTransactionDetails(
			List<TransactionDetails> listTransactionDetails) {

		
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
			merchantTransactionResponse.setStatus(transactionDetails.getStatus());
			merchantTransactionResponse.setTxtMsg(transactionDetails.getTxtMsg());
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(
						SecurityUtils.decryptSaveData(transactionDetails.getPaymentCode()).replace("\u0000", ""));
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}
			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String createdHeader(String strJson, String secretKey) throws NoSuchAlgorithmException {

		return SecurityExposerFunction.generateCheckSum(strJson, secretKey);
	}

	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
	@Autowired
	LetzpayStatus letzpayStatus;
	@Autowired
	RazorPayStatusSchedular razorpayStatus;
	@Autowired
	PaygStatusapi paygStatusapi;
	@Autowired
	NsdlStatus nsdlStatus;
	@Autowired
	SabPaisaStatus sabPaisaStatus;

	public MerchantTransaction getTransactionDetailsUsingOrderId(String appId, String secret, String orderId)
			throws Exception {
		MerchantTransaction merchantTransaction = new MerchantTransaction();
		MerchantDetails merchantDetails = paymentMerchantService.getMerchantFromAppId(appId);
		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND + appId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		String secretid = SecurityUtils
				.hash256Bit(Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId()));
		System.out.println(secretid);
		if (!secret.equals(secretid)) {
			throw new ValidationExceptions(INVALID_MERCHANT_REQUEST, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}
		List<TransactionDetails> listTransactionDetails = transactionDetailsRepository
				.findAllByMerchantOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());
		logger.info(orderId + "|" + merchantDetails.getMerchantID());
		if (listTransactionDetails.isEmpty()) {
			logger.debug("No Details found");
			throw new ValidationExceptions("Order Details Not Found", FormValidationExceptionEnums.ORDER_ID_NOT_FOUND);

		}
		List<MerchantTransactionResponse> listMerchantTransactionResponse = null;
		//List<TransactionDetails> listTransactionDetail = listTransactionDetails;

		listMerchantTransactionResponse = populateTransactionDetails(listTransactionDetails);
		merchantTransaction.setListMerchantTransactionResponse(listMerchantTransactionResponse);
		return merchantTransaction;

		// // ---------------- IF 24HRs Passed show default -----
		// String trxDetails = Utility.convertDTO2JsonString(listTransactionDetail);
		// ObjectMapper objectMapper = new ObjectMapper();
		// TransactionDto transactionDto = objectMapper.readValue(trxDetails,
		// TransactionDto.class);

		// if (Utility.check24HrsPassed(transactionDto.getCreated())) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// merchantTransaction.setListMerchantTransactionResponse(listMerchantTransactionResponse);
		// return merchantTransaction;
		// }
		// // --------------------------------------------------

		// PGConfigurationDetails pgConfigurationDetails =
		// pgConfigurationDetailsRepository
		// .findByPgName(listTransactionDetail.getPgType());
		// if (listTransactionDetail.getPgType().contains("CASHFREE")) {
		// logger.info("Running Cashfree API");

		// try {
		// TransactionResponse transactionResponse =
		// callCashFreesTATUSapi(pgConfigurationDetails,
		// listTransactionDetail.getOrderID());
		// listMerchantTransactionResponse =
		// cashfreePopulateTransactionDetails(listTransactionDetails,
		// transactionResponse);
		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("EASEBUZZ")) {
		// logger.info("Running EaseBuzz API");

		// try {
		// EaseBuzzTransactionResponse transactionResponse =
		// callEaseBuzzStatusApi(pgConfigurationDetails,
		// listTransactionDetail);
		// listMerchantTransactionResponse =
		// easebuzzPopulateTransactionDetails(listTransactionDetails,
		// transactionResponse);
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("PAYU")) {
		// try {
		// PayUStatusResponse transactionResponse =
		// callPayUStatusapi(pgConfigurationDetails,
		// listTransactionDetail.getOrderID());
		// if (transactionResponse.getStatus() == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// listMerchantTransactionResponse =
		// payUPopulateTransactionDetails(listTransactionDetails,
		// transactionResponse);
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("PAYTM")) {
		// try {
		// String status = null;
		// String Txtmsg = null;
		// PaytmStatusResponse paytmStatusResponse = convertPaytmJsonObject(
		// fetchStatusFromPaytm(pgConfigurationDetails,
		// listTransactionDetail.getOrderID()));

		// if (paytmStatusResponse != null) {
		// if (paytmStatusResponse.getBody() != null) {
		// if (paytmStatusResponse.getBody().getResultInfo() != null) {
		// status =
		// checkStatusPayTm(paytmStatusResponse.getBody().getResultInfo().getResultStatus());
		// }
		// }
		// }
		// if (status == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// if
		// (!paytmStatusResponse.getBody().getResultInfo().getResultCode().equals("501"))
		// {
		// Txtmsg = paytmStatusResponse.getBody().getResultInfo().getResultMsg();
		// listMerchantTransactionResponse =
		// populatePaytmTransactionDetails(listTransactionDetails,
		// status, Txtmsg);
		// } else {
		// populateTransactionDetails(listTransactionDetails);
		// }
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("GREZPAY")) {
		// try {
		// String status = null;
		// String Txtmsg = null;
		// GrezPayTransactionStatus grezPayTransactionStatus = callGrezPayStatusapi(
		// pgConfigurationDetails.getPgAppId(), listTransactionDetail.getOrderID());
		// status = grezPayTransactionStatus.getStatus();
		// if (status == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// listMerchantTransactionResponse =
		// grezPopulateTransactionDetails(listTransactionDetails,
		// grezPayTransactionStatus);
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("ASANPAY")) {
		// try {
		// String status = null;
		// AsanPayTransactionStatus asanPayTransactionStatus = callAsanPayStatusapi(
		// pgConfigurationDetails.getPgAppId(), listTransactionDetail.getOrderID());
		// status = asanPayTransactionStatus.getStatus();
		// if (status == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// listMerchantTransactionResponse =
		// asanPopulateTransactionDetails(listTransactionDetails,
		// asanPayTransactionStatus);
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("GETEPAY")) {
		// try {
		// String status = null;
		// TransactionStatusResponse getePayTransactionStatus = geteStatusApi(
		// pgConfigurationDetails, listTransactionDetail.getOrderID());
		// logger.info(Utility.convertDTO2JsonString(getePayTransactionStatus));
		// status = getePayTransactionStatus.getRequeryStatus();
		// if (status == null) {
		// logger.info("STATUS NULL");
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// listMerchantTransactionResponse =
		// getePopulateTransactionDetails(listTransactionDetails,
		// getePayTransactionStatus);
		// logger.info(Utility.convertDTO2JsonString(listMerchantTransactionResponse));
		// }
		// if (listMerchantTransactionResponse == null) {
		// logger.info("listMerchantTransactionResponse NULL");
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// e.printStackTrace();
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("LETZPAY")) {
		// try {
		// LetzTransactionStatusResponse transactionResponse =
		// letzpayStatus.callLetzPayStatusapi(
		// pgConfigurationDetails,
		// listTransactionDetail);
		// if (transactionResponse.getStatus() == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// listMerchantTransactionResponse =
		// letzpayStatus.LetzPopulateTransactionDetails(
		// listTransactionDetails,
		// transactionResponse);
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("RAZORPAY")) {
		// try {
		// List<MerchantTransactionResponse> transactionResponse = razorpayStatus
		// .RazorPopulateTransactionDetails(listTransactionDetails);

		// if (transactionResponse.get(0).getStatus() == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// merchantTransaction.setListMerchantTransactionResponse(transactionResponse);
		// return merchantTransaction;
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("PAYG")) {
		// try {
		// List<MerchantTransactionResponse> transactionResponse = paygStatusapi
		// .PayGPopulateTransactionDetails(listTransactionDetails);

		// if (transactionResponse.get(0).getStatus() == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// merchantTransaction.setListMerchantTransactionResponse(transactionResponse);
		// return merchantTransaction;
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("NSDL")) {
		// try {
		// List<MerchantTransactionResponse> transactionResponse = nsdlStatus
		// .NSDLPopulateTransactionDetails(listTransactionDetails);

		// if (transactionResponse.get(0).getStatus() == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// merchantTransaction.setListMerchantTransactionResponse(transactionResponse);
		// return merchantTransaction;
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else if (listTransactionDetail.getPgType().contains("SABPAISA")) {
		// try {
		// List<MerchantTransactionResponse> transactionResponse = sabPaisaStatus
		// .SabPaisaPopulateTransactionDetails(listTransactionDetails);

		// if (transactionResponse.get(0).getStatus() == null) {
		// populateTransactionDetails(listTransactionDetails);
		// } else {
		// merchantTransaction.setListMerchantTransactionResponse(transactionResponse);
		// return merchantTransaction;
		// }
		// if (listMerchantTransactionResponse == null) {
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// } catch (Exception e) {
		// logger.info(e.getMessage());
		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }
		// } else {

		// listMerchantTransactionResponse =
		// populateTransactionDetails(listTransactionDetails);
		// }

		// merchantTransaction.setListMerchantTransactionResponse(listMerchantTransactionResponse);

	//	return merchantTransaction;
	}

	@Value("${pgEndPoints.cashfreePaymentStatus}")
	String cashfreePaymentStatus;

	public TransactionResponse callCashFreesTATUSapi(PGConfigurationDetails pgConfigurationDetails, String orderId)
			throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		OkHttpClient client = new OkHttpClient().newBuilder().build();

		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("appId", pgConfigurationDetails.getPgAppId())
				.addFormDataPart("secretKey",
						Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()))
				.addFormDataPart("orderId", orderId).build();
		logger.info(pgConfigurationDetails.getPgAppId() + "|" + orderId + "|"
				+ Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));
		Request request = new Request.Builder().url(cashfreePaymentStatus).method("POST", body).build();
		Response response = client.newCall(request).execute();
		TransactionResponse transactionResponse = mapper.readValue(response.body().string(), TransactionResponse.class);

		logger.info(Utility.convertDTO2JsonString(transactionResponse));

		return transactionResponse;
	}

	public List<MerchantTransactionResponse> cashfreePopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, TransactionResponse transactionResponse) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
			if (transactionResponse.getStatus().equalsIgnoreCase("ERROR")) {
				merchantTransactionResponse.setStatus("FAILED");
				merchantTransactionResponse.setTxtMsg("Transaction Incomplete");
			} else {
				merchantTransactionResponse.setStatus(transactionResponse.getTxStatus());
				merchantTransactionResponse.setTxtMsg(transactionResponse.getTxMsg());
			}
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(
						SecurityUtils.decryptSaveData(transactionDetails.getPaymentCode()).replace("\u0000", ""));
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}
			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	@Value("${pgEndPoints.easeBuzzPaymentStatus}")
	String easeBuzzPaymentStatus;

	public EaseBuzzTransactionResponse callEaseBuzzStatusApi(PGConfigurationDetails pgConfigurationDetails,
			TransactionDetails transactionDetails) throws Exception {

		EaseBuzzTransactionDetails easeBuzzTransactionDetails = easeBuzzTransactionDetailsRepository
				.findByMerchantOrderIdAndOrderId(transactionDetails.getMerchantOrderId(),
						transactionDetails.getOrderID());

		logger.info(transactionDetails.getMerchantOrderId() + "|" + transactionDetails.getOrderID());
		Map<String, String> params = new HashMap<String, String>();
		params.put(EasebuzzConstants.TXTID, transactionDetails.getOrderID());
		params.put(EasebuzzConstants.AMOUNT, easeBuzzTransactionDetails.getOrderAmount());
		params.put(EasebuzzConstants.EMAIL, easeBuzzTransactionDetails.getEmail());
		params.put(EasebuzzConstants.PHONE, easeBuzzTransactionDetails.getPhoneNo());
		params.put(EasebuzzConstants.KEY, Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));

		String checkSum = generateCommanHash(params, pgConfigurationDetails.getPgSaltKey());
		params.put(EasebuzzConstants.HASH, checkSum);

		logger.info("params :: " + params.toString());

		String requestData = "txnid=" + transactionDetails.getOrderID() + "&amount="
				+ easeBuzzTransactionDetails.getOrderAmount() + "&email=" + easeBuzzTransactionDetails.getEmail()
				+ "&phone=" + easeBuzzTransactionDetails.getPhoneNo() + "&key="
				+ Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()) + "&hash=" + checkSum;

		HttpResponse<EaseBuzzTransactionResponse> easeBuzzTransactionResponse = Unirest.post(easeBuzzPaymentStatus)
				.header(EasebuzzConstants.CONTENTTYPE, "application/x-www-form-urlencoded").body(requestData)
				.asObject(EaseBuzzTransactionResponse.class);

		logger.info("Parsing Error :: " + easeBuzzTransactionResponse.getParsingError().toString());
		logger.info("easeBuzzTransactionResponse :: " + easeBuzzTransactionResponse.getBody().toString());

		logger.info(Utility.convertDTO2JsonString(easeBuzzTransactionResponse.getBody()));

		return easeBuzzTransactionResponse.getBody();
	}

	public String generateCommanHash(Map<String, String> params, String salt) {

		String hashString = "";
		String hash = "";

		String hashSequence = "key|txnid|amount|email|phone";
		if (StringUtils.isEmpty(params.get("key"))) {

			logger.info("Generated HASH Exception due to KEY not found ");
		} else {
			String[] hashVarSeq = hashSequence.split("\\|");
			for (String part : hashVarSeq) {
				hashString = (StringUtils.isEmpty(params.get(part))) ? hashString.concat("")
						: hashString.concat(params.get(part));
				hashString = hashString.concat("|");
			}
			hashString = hashString.concat(salt);
			hash = Easebuzz_Generatehash512("SHA-512", hashString);

		}
		return hash;
	}

	public String Easebuzz_Generatehash512(String type, String str) {
		byte[] hashseq = str.getBytes();
		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(hashseq);
			byte messageDigest[] = algorithm.digest();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1) {
					hexString.append("0");
				}
				hexString.append(hex);
			}
		} catch (NoSuchAlgorithmException nsae) {
		}
		return hexString.toString();
	}

	public List<MerchantTransactionResponse> easebuzzPopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, EaseBuzzTransactionResponse easeBuzzTransactionResponse) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
			if (easeBuzzTransactionResponse.getMsg().getStatus().equalsIgnoreCase("ERROR")) {
				merchantTransactionResponse.setStatus("FAILED");
				merchantTransactionResponse.setTxtMsg("Transaction Incomplete");
			} else {
				merchantTransactionResponse.setStatus(checkStatus(easeBuzzTransactionResponse.getMsg().getStatus()));
				merchantTransactionResponse.setTxtMsg(easeBuzzTransactionResponse.getMsg().getErrorMessage());
			}
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}
			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String checkStatus(String eazeBuzz) {
		if (eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_SUCCESS)) {
			return UserStatus.SUCCESS.toString();
		}
		if (eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_INIT)
				|| eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_PENDING)
				|| eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_BOUNCED)) {
			return UserStatus.PENDING.toString();
		}
		if (eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_FAILURE)) {
			return UserStatus.FAILED.toString();
		}
		if (eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_DROPPED)
				|| eazeBuzz.equalsIgnoreCase(EasebuzzConstants.STAT_USER_CANCELLED)) {
			return UserStatus.DROPPED.toString();
		}
		return UserStatus.FAILED.toString();
	}
	// ONE Pay Structure

	@Value("${pgEndPoints.onePayPaymentStatus}")
	String onePayPaymentStatus;

	public OnePayTransactionResponse callOnePayStatusApi(PGConfigurationDetails pgConfigurationDetails,
			TransactionDetails transactionDetails) throws Exception {

		OnePayTransactionDetails onePayTransactionDetails = onePayTransactionDetailsRepository
				.findByMerchantOrderIdAndOrderId(transactionDetails.getMerchantOrderId(),
						transactionDetails.getOrderID());

		String requestURL = onePayPaymentStatus + "?merchantId=" + pgConfigurationDetails.getPgAppId() + "&txnId="
				+ onePayTransactionDetails.getOrderId();

		HttpResponse<OnePayTransactionResponse> onePayTransactionResponse = Unirest.post(requestURL)
				.header("Content-Type", "application/x-www-form-urlencoded").asObject(OnePayTransactionResponse.class);
		logger.info(Utility.convertDTO2JsonString(onePayTransactionResponse.getBody()));
		return onePayTransactionResponse.getBody();

	}

	public List<MerchantTransactionResponse> onePayPopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, OnePayTransactionResponse onePayTransactionResponse) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
			if (onePayTransactionResponse.getTrans_status().equalsIgnoreCase("ERROR")) {
				merchantTransactionResponse.setStatus(UserStatus.FAILED.toString());
				merchantTransactionResponse.setTxtMsg("Transaction Error");
			} else {
				merchantTransactionResponse.setStatus(checkStatusOnePay(onePayTransactionResponse.getTrans_status()));
				merchantTransactionResponse.setTxtMsg(onePayTransactionResponse.getResp_message());
			}
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}
			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String checkStatusOnePay(String Onepay) {
		if (Onepay.equalsIgnoreCase(OnePayConstants.STATUS_OK)) {
			return UserStatus.SUCCESS.toString();
		}
		if (Onepay.equalsIgnoreCase(OnePayConstants.STATUS_TO)) {
			return UserStatus.PENDING.toString();
		}
		if (Onepay.equalsIgnoreCase(OnePayConstants.STATUS_FAILED)) {
			return UserStatus.FAILED.toString();
		}

		return Onepay;
	}

	public List<MerchantTransactionResponse> payUPopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, PayUStatusResponse payUStatusResponse) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());

			merchantTransactionResponse.setStatus(checkStatusPayU(payUStatusResponse.getStatus()));
			merchantTransactionResponse.setTxtMsg(payUStatusResponse.getMessage());

			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}

			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public PayUStatusResponse callPayUStatusapi(PGConfigurationDetails pgConfigurationDetails, String orderId)
			throws IOException {

		String requestData = "key=" + Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret())
				+ "&command=" + PayUConstants.COMMAND_VERIFY +
				"&var1=" + orderId + "&hash=" + getHashesResponse(orderId, pgConfigurationDetails.getPgSaltKey(),
						Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));

		HttpResponse<JsonNode> payUResponse = Unirest.post("https://info.payu.in/merchant/postservice.php?form=2")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.body(requestData).asJson();

		return parseResponsePayU(payUResponse.getBody().toPrettyString(), orderId);
	}

	public PayUStatusResponse parseResponsePayU(String jsonString, String orderId) {

		PayUStatusResponse payUStatusResponse = new PayUStatusResponse();
		logger.info("PAYU Status Response::" + jsonString);
		JSONObject jsonObject = Utility.convertStringToJSONObject(jsonString);

		JSONObject jsonObjectTrDetails = jsonObject.getJSONObject("transaction_details").getJSONObject(orderId);
		if (jsonObject.getInt("status") == 1) {
			try {
				payUStatusResponse.setErrorCode(jsonObjectTrDetails.get("error_code").toString());
				payUStatusResponse.setErrorDesc(jsonObjectTrDetails.get("error_Message").toString());
				payUStatusResponse.setMessage(jsonObjectTrDetails.get("field9").toString());
				payUStatusResponse.setPg_orderid(jsonObjectTrDetails.get("mihpayid").toString());
				payUStatusResponse.setTxtpgtime(jsonObjectTrDetails.get("addedon").toString());
			} catch (Exception e) {
				payUStatusResponse.setErrorDesc("Waiting from PG");
				payUStatusResponse.setMessage("Waiting from PG");
			}
			payUStatusResponse.setOrderId(orderId);
			payUStatusResponse.setStatus(jsonObjectTrDetails.get("status").toString());
		} else if (jsonObject.getInt("status") == 0) {
			payUStatusResponse.setStatus("DROPPED");
			payUStatusResponse.setMessage("User Dropped");
		}

		return payUStatusResponse;

	}

	public String getHashesResponse(String txnid, String salt, String key) {

		String ph = key + "|" + checkNull(PayUConstants.COMMAND_VERIFY) + "|" + checkNull(txnid) + "|" + salt;
		System.out.println("Payment Hash " + ph);
		String statusHash = getSHA(ph);
		System.out.println("Payment Hash " + statusHash);

		return statusHash;
	}

	private String checkNull(String value) {
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	private String getSHA(String str) {
		MessageDigest md;
		String out = "";
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(str.getBytes());
			byte[] mb = md.digest();

			for (int i = 0; i < mb.length; i++) {
				byte temp = mb[i];
				String s = Integer.toHexString(new Byte(temp));
				while (s.length() < 2) {
					s = "0" + s;
				}
				s = s.substring(s.length() - 2);
				out += s;
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return out;
	}

	public String checkStatusPayU(String payuStatus) {
		if (payuStatus.equalsIgnoreCase(PayUConstants.STATUS_SUCCESS)) {
			return UserStatus.SUCCESS.toString();
		}
		if (payuStatus.equalsIgnoreCase(PayUConstants.STATUS_FAILED)) {
			return UserStatus.FAILED.toString();
		}
		if (payuStatus.equalsIgnoreCase(PayUConstants.STATUS_DROPPED)) {
			return UserStatus.DROPPED.toString();
		}
		if (payuStatus.equalsIgnoreCase(PayUConstants.STATUS_PENDING)) {
			return UserStatus.PENDING.toString();
		}
		if (payuStatus.equalsIgnoreCase(PayUConstants.STATUS_failed_PENDING)) {
			return UserStatus.PENDING.toString();
		}
		return payuStatus;
	}

	@Value("${pgEndPoints.paytmStatusLink}")
	String paytmStatusLink;

	public JSONObject fetchStatusFromPaytm(PGConfigurationDetails pgConfigurationDetails, String orderId)
			throws Exception {

		JSONObject body = new JSONObject();
		JSONObject paytmParams = new JSONObject();
		JSONObject outPutDetails = new JSONObject();

		body.put(PaytmConstants.MID, pgConfigurationDetails.getPgAppId());
		body.put(PaytmConstants.ORDERID, orderId);

		String checksum = PaytmChecksum.generateSignature(body.toString(),
				Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));

		JSONObject head = new JSONObject();
		head.put(PaytmConstants.SIGNATURE, checksum);
		paytmParams.put(PaytmConstants.BODY, body);
		paytmParams.put(PaytmConstants.HEAD, head);
		String post_data = paytmParams.toString();

		URL url = new URL(paytmStatusLink);
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(post_data);
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
				logger.info("Response: " + responseData);
				outPutDetails = new JSONObject(responseData);
			}
			responseReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();

		}

		return outPutDetails;

	}

	public PGConfigurationDetails getPGConfigurationDetails(TransactionDetails transactionDetails) {

		return pgConfigurationDetailsRepository.findByPgName(transactionDetails.getPgType());
	}

	public PaytmStatusResponse convertPaytmJsonObject(JSONObject jsonObject) {
		ObjectMapper mapper = new ObjectMapper();
		PaytmStatusResponse paytmStatusResponse = new PaytmStatusResponse();
		try {
			paytmStatusResponse = mapper.readValue(jsonObject.toString(), PaytmStatusResponse.class);
		} catch (Exception e) {
			logger.error("Exception in Json Object to DTO conversion :: ");
			e.printStackTrace();
			return null;
		}
		return paytmStatusResponse;
	}

	public List<MerchantTransactionResponse> populatePaytmTransactionDetails(
			List<TransactionDetails> listTransactionDetails, String status, String txtmsg) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
			merchantTransactionResponse.setStatus(status);
			merchantTransactionResponse.setTxtMsg(txtmsg);
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(
						SecurityUtils.decryptSaveData(transactionDetails.getPaymentCode()).replace("\u0000", ""));
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}
			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String checkStatusPayTm(String paytm) {
		if (paytm.equalsIgnoreCase("TXN_SUCCESS")) {
			return UserStatus.SUCCESS.toString();
		}
		if (paytm.equalsIgnoreCase("TXN_FAILURE")) {
			return UserStatus.FAILED.toString();
		}

		return paytm;
	}

	@Value("${pgEndPoints.grezpayStatusAPI}")
	String grezpayStatusAPI;

	public GrezPayTransactionStatus callGrezPayStatusapi(String appId, String orderId) throws JsonProcessingException {
		logger.info(grezpayStatusAPI);
		HttpResponse<GrezPayTransactionStatus> grezPayTransactionDetails = Unirest.get(grezpayStatusAPI)
				.queryString("APP_ID", appId)
				.queryString("ORDER_ID", orderId)
				.asObject(GrezPayTransactionStatus.class)
				.ifFailure(SetuErrorResponse.class, r -> {
					SetuErrorResponse e = r.getBody();
					try {
						logger.info("GrezPay Status Request Response Error::" + Utility.convertDTO2JsonString(e));
					} catch (JsonProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

		logger.info("Response :: " + Utility.convertDTO2JsonString(grezPayTransactionDetails.getBody()));

		return grezPayTransactionDetails.getBody();

	}

	public List<MerchantTransactionResponse> grezPopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, GrezPayTransactionStatus grezPayTransactionStatus) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());

			merchantTransactionResponse.setStatus(
					checkGrezStatus(grezPayTransactionStatus.getResponseCode(), grezPayTransactionStatus.getStatus()));
			if (grezPayTransactionStatus.getMessage() != null) {
				merchantTransactionResponse.setTxtMsg(getErrorMsg(grezPayTransactionStatus.getResponseCode()) + "|"
						+ grezPayTransactionStatus.getMessage());
			} else {
				merchantTransactionResponse.setTxtMsg(getErrorMsg(grezPayTransactionStatus.getResponseCode()));
			}
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}

			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String getErrorMsg(String subcode) {
		Map<String, String> map = new HashMap<>();
		map.put("000", "SUCCESS");
		map.put("004", "Declined");
		map.put("001", "Acquirer Error");
		map.put("002", "Denied");
		map.put("003", "Timeout");
		map.put("005", "Authenncation not available");
		map.put("006", "Transaction Processing");
		map.put("007", "Rejected by Acquirer");
		map.put("008", "Duplicate");
		map.put("009", "Response signature did not match");
		map.put("010", "Cancelled by user");
		map.put("011", "Authorization success but error processing");
		map.put("012", "Denied due to fraud detection");
		map.put("013", "Invalid request not available");
		map.put("014", "Refund amount you requested is greater than");
		map.put("007", "Failed/Failed by acquirer");
		map.put("015", "Status is yet to be received from Bank/Customer has led the transaction in middle");
		map.put("016", "Auto Reversal");
		map.put("300", "Invalid Request");
		map.put("113", "Payment option not supported");
		try {
			return map.get(subcode);
		} catch (Exception e) {
			return subcode;
		}

	}

	public String checkGrezStatus(String errorCode, String errorMsg) {
		String[] successarr = { "000|success" };
		String[] pendingarr = { "006|PROCESSING", "015|SENT_TO_BANK" };
		String[] failedarr = { "004|DECLINED", "001|ACQUIRER_ERROR", "002|DENIED", "003|TIMEOUT",
				"005|AUTHENTICATION_UNAVAILABLE", "007|REJECTED", "008|DUPLICATE", "009|SIGNATURE_MISMATCH",
				"010|CANCELLED", "011|RECURRING_PAYMENT_UNSUCCESSFULL", "013|INVALID_REQUEST",
				"014|REFUND_INSUFFICIENT_BALANCE", "007|TXN_FAILED", "016|AUTO_REVERSAL", "007|FAILED_AT_ACQUIRER",
				"300|VALIDATION_FAILED", "113|PAYMENT_OPTION_NOT_SUPPORTED" };
		String[] flaggedarr = { "012|DENIED_BY_RISK" };

		String msg = errorCode + "|" + errorMsg;

		logger.info("MSG::" + msg);
		if (errorCode.equals("000") && errorMsg.equalsIgnoreCase("Captured")) {
			return UserStatus.SUCCESS.toString();
		} else if ((errorCode.equals("006")) || (errorCode.equals("015")) || (errorCode.equals("003"))) {
			return UserStatus.PENDING.toString();
		} else if (errorCode.equals("012")) {
			return UserStatus.FLAGGED.toString();
		} else {
			return UserStatus.FAILED.toString();
		}
	}

	// ---------------------------------------------ASANPAY
	// ---------------------------------------//

	@Value("${pgEndPoints.asanpayStatusAPI}")
	String asanpayStatusAPI;

	public AsanPayTransactionStatus callAsanPayStatusapi(String appId, String orderId) throws JsonProcessingException {

		HttpResponse<AsanPayTransactionStatus> asanPayTransactionDetails = Unirest.get(asanpayStatusAPI)
				.queryString("APP_ID", appId)
				.queryString("ORDER_ID", orderId)
				.asObject(AsanPayTransactionStatus.class)
				.ifFailure(SetuErrorResponse.class, r -> {
					SetuErrorResponse e = r.getBody();
					try {
						logger.info("AsanPay Status Request Response Error::" + Utility.convertDTO2JsonString(e));
					} catch (JsonProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

		logger.info("Response :: " + Utility.convertDTO2JsonString(asanPayTransactionDetails.getBody()));

		return asanPayTransactionDetails.getBody();

	}

	public List<MerchantTransactionResponse> asanPopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, AsanPayTransactionStatus asanPayTransactionStatus) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());

			merchantTransactionResponse.setStatus(
					checkAsanStatus(asanPayTransactionStatus.getResponseCode(), asanPayTransactionStatus.getStatus()));
			if (asanPayTransactionStatus.getMessage() != null) {
				merchantTransactionResponse.setTxtMsg(getErrorMsg(asanPayTransactionStatus.getResponseCode()) + "|"
						+ asanPayTransactionStatus.getMessage());
			} else {
				merchantTransactionResponse.setTxtMsg(getErrorMsg(asanPayTransactionStatus.getResponseCode()));
			}
			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}

			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String checkAsanStatus(String errorCode, String errorMsg) {
		String[] successarr = { "000|success" };
		String[] pendingarr = { "006|PROCESSING", "015|SENT_TO_BANK" };
		String[] failedarr = { "004|DECLINED", "001|ACQUIRER_ERROR", "002|DENIED", "003|TIMEOUT",
				"005|AUTHENTICATION_UNAVAILABLE", "007|REJECTED", "008|DUPLICATE", "009|SIGNATURE_MISMATCH",
				"010|CANCELLED", "011|RECURRING_PAYMENT_UNSUCCESSFULL", "013|INVALID_REQUEST",
				"014|REFUND_INSUFFICIENT_BALANCE", "007|TXN_FAILED", "016|AUTO_REVERSAL", "007|FAILED_AT_ACQUIRER",
				"300|VALIDATION_FAILED", "113|PAYMENT_OPTION_NOT_SUPPORTED" };
		String[] flaggedarr = { "012|DENIED_BY_RISK" };

		String msg = errorCode + "|" + errorMsg;

		logger.info("MSG::" + msg);
		if (errorCode.equals("000") && errorMsg.equalsIgnoreCase("Captured")) {
			return UserStatus.SUCCESS.toString();
		} else if ((errorCode.equals("006")) || (errorCode.equals("015")) || (errorCode.equals("003"))) {
			return UserStatus.PENDING.toString();
		} else if (errorCode.equals("012")) {
			return UserStatus.FLAGGED.toString();
		} else {
			return UserStatus.FAILED.toString();
		}

	}

	@Value("${getepay.publicKeyPath}")
	String publicKeyPath;
	@Value("${getepay.privateKeyPath}")
	String privateKeyPath;
	@Value("${pgEndPoints.getepayStatusAPI}")
	String getepayStatusAPI;

	private TransactionStatusResponse geteStatusApi(PGConfigurationDetails pgConfigurationDetails, String orderId)
			throws IOException {
		logger.info("GETEPAY STATUS API");
		logger.info("GETEPAY STATUS Pg Config" + Utility.convertDTO2JsonString(pgConfigurationDetails));
		List<Properties> properties = new ArrayList<Properties>();
		ObjectMapper wrapper = new ObjectMapper();
		wrapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		Properties p = new Properties();

		p.setPropertykey(GetepayConstants.PUBLIC_KEY);
		p.setPropertyValue(publicKeyPath);
		properties.add(p);

		Properties p1 = new Properties();
		p1.setPropertykey(GetepayConstants.PRIVATE_KEY);
		p1.setPropertyValue(privateKeyPath);
		properties.add(p1);

		TransactionRequeryRequest request = new TransactionRequeryRequest();
		request.setLogin(pgConfigurationDetails.getPgAddInfo1());
		request.setMid(pgConfigurationDetails.getPgAppId());
		request.setPassword(Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));
		request.setMerchantReferenceNo(orderId);

		String requestStringJson = wrapper.writeValueAsString(request);

		String requestStr = GetePayUtil.encryptApiResponse(requestStringJson, properties);

		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter
				.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(getepayStatusAPI, requestStr, String.class);

		// logger.info("GATEPAY RESPONSE ENC::" +
		// Utility.convertDTO2JsonString(responseEntity.getBody()));
		String decryptResponse = GetePayUtil.decryptApiRequest(responseEntity.getBody(),
				properties);
		logger.info(decryptResponse);
		TransactionStatusResponse transactionStatusResponse = wrapper.readValue(decryptResponse,
				TransactionStatusResponse.class);
		logger.info(Utility.convertDTO2JsonString(transactionStatusResponse));
		return transactionStatusResponse;

	}

	public List<MerchantTransactionResponse> getePopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails, TransactionStatusResponse getePayTransactionStatus) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			merchantTransactionResponse
					.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
			merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
			merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
			merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
			merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());

			merchantTransactionResponse.setStatus(checkGeteStatus(getePayTransactionStatus.getTxnStatus()));
			logger.info("DESCRIPTION::" + getePayTransactionStatus.getDescription());
			merchantTransactionResponse.setTxtMsg(getePayTransactionStatus.getDescription());

			merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
			merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());
			if (transactionDetails.getVpaUPI() != null) {
				merchantTransactionResponse.setVpaUPI(Utility.maskUpiCode(
						SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", "")));
			}
			if (transactionDetails.getPaymentCode() != null) {
				merchantTransactionResponse.setPaymentCode(transactionDetails.getPaymentCode());
			}
			if (transactionDetails.getCardNumber() != null) {
				merchantTransactionResponse.setCardNumber(
						Utility.maskCardNumber(SecurityUtils.decryptSaveData(transactionDetails.getCardNumber()))
								.replace("\u0000", ""));
			}

			listMerchantTransactionResponse.add(merchantTransactionResponse);
		}
		return listMerchantTransactionResponse;
	}

	public String checkGeteStatus(String status) {

		if (status.equalsIgnoreCase("SUCCESS")) {
			return UserStatus.SUCCESS.toString();
		} else if (status.equalsIgnoreCase("FAILED")) {
			return UserStatus.FAILED.toString();
		} else if (status.equalsIgnoreCase("PENDING")) {
			return UserStatus.PENDING.toString();
		} else {
			return status;
		}

	}

}
