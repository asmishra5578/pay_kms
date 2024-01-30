package com.asktech.pgateway.service.merchantApi.statusApis;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.asktech.pgateway.constants.payg.PayGConstants;
import com.asktech.pgateway.dto.merchant.MerchantTransactionResponse;
import com.asktech.pgateway.dto.payg.PayGOrderStatusRequest;
import com.asktech.pgateway.dto.payg.PayGOrderStatusResponse;
import com.asktech.pgateway.dto.setu.SetuErrorResponse;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.PayGTransactionDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.PayGTransactionDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Component
public class PaygStatusapi implements PayGConstants {
    @Value("${pgEndPoints.paygstatusapi}")
    String paygPayOrderStatus;
    @Autowired
    PayGTransactionDetailsRepository payGTransactionDetailsRepository;
    @Autowired
    PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
    @Autowired
    TransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    MerchantPGDetailsRepository merchantPGDetailsRepository;
    @Autowired
    MerchantDetailsRepository merchantDetailsRepository;

    Logger logger = LoggerFactory.getLogger(PaygStatusapi.class);

    public List<MerchantTransactionResponse> PayGPopulateTransactionDetails(
            List<TransactionDetails> listTransactionDetails) {

        MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
        List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

        for (TransactionDetails transactionDetails : listTransactionDetails) {
            // PGConfigurationDetails pgConfigurationDetails =
            // pgConfigurationDetailsRepository
            // .findByPgName(transactionDetails.getPgType());
            try {
                logger.info("Run Status API::");
                TransactionDetails StatusResponse = checkTransactionStatus(transactionDetails.getOrderID());
                logger.info(Utility.convertDTO2JsonString(StatusResponse));
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
                sta = StatusResponse.getStatus();
                if (StatusResponse.getTxtMsg() != null) {
                    merchantTransactionResponse.setTxtMsg(StatusResponse.getTxtMsg());
                }
                logger.info("PayG Payment Status::" + sta + "|reason::" + reason + "|txtmsg::" + txtmsg);
                // if (sta != null) {
                // merchantTransactionResponse.setStatus(sta);
                // merchantTransactionResponse.setTxtMsg(txtmsg);
                // }
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

    public TransactionDetails checkTransactionStatus(String orderId) throws JsonProcessingException {
        PayGTransactionDetails payGTransactionDetails = payGTransactionDetailsRepository.findByOrderId(orderId);
        if (payGTransactionDetails != null) {
            TransactionDetails transactionDetails = transactionDetailsRepository
                    .findByOrderID(payGTransactionDetails.getOrderId());
            if (transactionDetails != null) {
                PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
                        .findByPgUuid(transactionDetails.getPgId());

                PayGOrderStatusRequest payGOrderStatusRequest = new PayGOrderStatusRequest();
                payGOrderStatusRequest.setMerchantKeyId(payGTransactionDetails.getMerchnatkeyId());
                payGOrderStatusRequest.setOrderKeyId(payGTransactionDetails.getPayGOrderKeyId());
                PayGOrderStatusResponse payGOrderStatusResponse = getOrderStatus(payGOrderStatusRequest,
                        pgConfigurationDetails);

                if (payGOrderStatusResponse != null
                        && payGOrderStatusResponse.getPaymentResponseText() != null) {
                    transactionDetails.setStatus(checkStatus(payGOrderStatusResponse.getPaymentResponseText(),
                            payGOrderStatusResponse.getPaymentResponseCode()));
                    transactionDetails.setPgOrderID(payGTransactionDetails.getPayGOrderKeyId());
                    if (payGOrderStatusResponse.getOrderPaymentStatusText() != null) {
                        transactionDetails
                                .setTxtMsg(payGOrderStatusResponse.getOrderPaymentStatusText());
                    }
                    // transactionDetails.setTxtPGTime(
                    // payGOrderStatusResponse.getOrderPaymentTransactionDetail().get(0).getUpdatedDateTime());
                    transactionDetails.setSource("StatusURL");

                    transactionDetails = transactionDetailsRepository.save(transactionDetails);
                    return transactionDetails;
                }
                return transactionDetails;
            }
            return transactionDetails;
        }
        return null;
    }

    private PayGOrderStatusResponse getOrderStatus(PayGOrderStatusRequest payGOrderStatusRequest,
            PGConfigurationDetails PGConfigurationDetails) throws JsonProcessingException {

        logger.debug("Request for Payg :: " + Utility.convertDTO2JsonString(payGOrderStatusRequest));

        HttpResponse<PayGOrderStatusResponse> payGOrderStatusResponse = Unirest.post(paygPayOrderStatus)
                .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .header(AUTHORIZATION,
                        AUTHORIZATION_VALUE + populatePayGAuth(
                                Encryption.decryptCardNumberOrExpOrCvv(PGConfigurationDetails.getPgSecret()),
                                PGConfigurationDetails.getPgSaltKey(), PGConfigurationDetails.getPgAppId()))
                .header(ACCEPT, ACCEPT_VALUE).body(Utility.convertDTO2JsonString(payGOrderStatusRequest))
                .asObject(PayGOrderStatusResponse.class).ifFailure(SetuErrorResponse.class, r -> {
                    SetuErrorResponse e = r.getBody();
                    try {
                        logger.info("PayG Order Create Response Error::" + Utility.convertDTO2JsonString(e));
                    } catch (JsonProcessingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                });
        logger.info("PayG Status Response::" + Utility.convertDTO2JsonString(payGOrderStatusResponse.getBody()));
        return payGOrderStatusResponse.getBody();
    }

    private String populatePayGAuth(String authenticationKey, String authenticationToken, String MerchantKey) {

        String authStr = authenticationKey + ":" + authenticationToken + ":M:" + MerchantKey;

        byte[] bytesEncoded = Base64.getEncoder().encode(authStr.getBytes());
        String generatedAuth = new String(bytesEncoded);

        // logger.debug("generatedAuth :: " + generatedAuth);
        return generatedAuth;

    }

    private String checkStatus(String responseText, String responseCodes) {
        if (responseText.equalsIgnoreCase("success") && responseCodes.equalsIgnoreCase("1")) {
            return UserStatus.SUCCESS.toString();
        }
        if (responseText.equalsIgnoreCase("approved") && responseCodes.equalsIgnoreCase("1")) {
            return UserStatus.SUCCESS.toString();
        }
        if (responseText.equalsIgnoreCase("declined") && responseCodes.equalsIgnoreCase("2")) {
            return UserStatus.FAILED.toString();
        }
        if (responseText.equalsIgnoreCase("failed") && responseCodes.equalsIgnoreCase("2")) {
            return UserStatus.FAILED.toString();
        }
        if (responseText.equalsIgnoreCase("submitted") && responseCodes.equalsIgnoreCase("4")) {
            return UserStatus.PENDING.toString();
        }
        if (responseText.equalsIgnoreCase("pending") && responseCodes.equalsIgnoreCase("4")) {
            return UserStatus.PENDING.toString();
        }
        return responseText.toUpperCase();
    }
}