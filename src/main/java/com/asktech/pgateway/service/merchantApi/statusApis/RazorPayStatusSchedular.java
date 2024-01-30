package com.asktech.pgateway.service.merchantApi.statusApis;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.dto.merchant.MerchantTransactionResponse;
import com.asktech.pgateway.dto.razorpay.RazorPayments;
import com.asktech.pgateway.dto.setu.SetuErrorResponse;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.RazorPayTransactionDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.statusUtils.StatusCodes;
import com.fasterxml.jackson.core.JsonProcessingException;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Component
public class RazorPayStatusSchedular {

	static Logger logger = LoggerFactory.getLogger(RazorPayStatusSchedular.class);

	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
	@Autowired
	StatusCodes statusCodes;
	@Autowired
	RazorPayTransactionDetailsRepository razorPayTransactionDetailsRepository;

	private RazorPayments getOrderStatus(PGConfigurationDetails pgConfigurationDetails, String orderId)
			throws JsonProcessingException {
		logger.info("Running getOrderStatus::" + orderId);
		String razOrderId = razorPayTransactionDetailsRepository.findByOrderId(orderId).getRazorPayOrderId();
		logger.info("RazorPay ORDER ID::" + razOrderId);
		// getOrderStatusTest(pgConfigurationDetails, razOrderId);
		HttpResponse<RazorPayments> razorPayOrderResponse = Unirest
				.get("https://api.razorpay.com/v1/orders/" + razOrderId + "/payments")
				.header("Content-Type", "application/json")
				.header("Authorization",
						"Basic " + populateRazorPayAuth(pgConfigurationDetails.getPgAppId(),
								Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret())))
				.header("Accept", "application/json").asObject(RazorPayments.class)
				.ifFailure(SetuErrorResponse.class, r -> {
					SetuErrorResponse e = r.getBody();
					try {
						logger.info("RazorPay Order Create Response Error::" + Utility.convertDTO2JsonString(e));
					} catch (JsonProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

		logger.debug("Order Response :: " + Utility.convertDTO2JsonString(razorPayOrderResponse.getBody()));

		return razorPayOrderResponse.getBody();
	}

	// private void getOrderStatusTest(PGConfigurationDetails
	// pgConfigurationDetails, String orderId)
	// throws JsonProcessingException {

	// HttpResponse<String> razorPayOrderResponse = Unirest
	// .get("https://api.razorpay.com/v1/orders/" + "order_J7m5D435461Wv3" +
	// "/payments").header("Content-Type",
	// "application/json")
	// .header("Authorization",
	// "Basic " + populateRazorPayAuth(pgConfigurationDetails.getPgAppId(),
	// Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret())))
	// .header("Accept", "application/json").asString();

	// logger.debug("Order Response Test:: " + razorPayOrderResponse.getBody());

	// // return razorPayOrderResponse.getBody();
	// }

	private String populateRazorPayAuth(String mid, String secretKey) {

		String authStr = mid + ":" + secretKey;
		byte[] bytesEncoded = Base64.getEncoder().encode(authStr.getBytes());
		String generatedAuth = new String(bytesEncoded);

		logger.debug("generatedAuth :: " + generatedAuth);
		return generatedAuth;

	}

	public List<MerchantTransactionResponse> RazorPopulateTransactionDetails(
			List<TransactionDetails> listTransactionDetails) {

		MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
		List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

		for (TransactionDetails transactionDetails : listTransactionDetails) {
			PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
					.findByPgName(transactionDetails.getPgType());
			try {
				logger.info("Run Status API");
				RazorPayments StatusResponse = getOrderStatus(pgConfigurationDetails,
						transactionDetails.getOrderID());
				// getOrderStatusTest(pgConfigurationDetails,
				// transactionDetails.getPgOrderID());
				merchantTransactionResponse
						.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
				merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
				merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
				merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
				merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
				String sta = null;
				String txtmsg = null;
				String reason = null;
				if (StatusResponse.getItems().size() > 0) {
					sta = StatusResponse.getItems().get(0).getStatus();
					reason = StatusResponse.getItems().get(0).getError_reason();
					txtmsg = StatusResponse.getItems().get(0).getError_description();
					sta = checkRazorStatus(sta, reason);
					logger.info("Razorpay Payment Status::" + sta + "|reason::" + reason + "|txtmsg::" + txtmsg);
				}
				if (sta != null) {
					merchantTransactionResponse.setStatus(sta);
					merchantTransactionResponse.setTxtMsg(txtmsg);
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
			} catch (Exception e) {
				logger.error(e.getMessage());
				merchantTransactionResponse
						.setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
				merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
				merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
				merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
				merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
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
		}
		return listMerchantTransactionResponse;
	}

	private String checkRazorStatus(String status, String reason) {
		if (status.equalsIgnoreCase("captured")) {
			return UserStatus.SUCCESS.toString();
		}
		if (status.equalsIgnoreCase("authorized") || status.equalsIgnoreCase("created")) {
			return UserStatus.PENDING.toString();
		}
		if (status.equalsIgnoreCase("failed")) {
			String sta = statusCodes.checkStatus("RAZORPAY", reason);
			if (sta != null) {
				return sta;
			} else {
				return UserStatus.FAILED.toString();
			}
		}
		if (status.equalsIgnoreCase("refunded")) {
			return UserStatus.REFUNDED.toString();
		}
		return status;
	}

}
