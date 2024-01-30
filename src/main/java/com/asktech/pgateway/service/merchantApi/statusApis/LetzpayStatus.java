package com.asktech.pgateway.service.merchantApi.statusApis;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.asktech.pgateway.constants.letsPay.LetzPayConstants;
import com.asktech.pgateway.dto.letzpay.LetzTransactionStatusResponse;
import com.asktech.pgateway.dto.merchant.MerchantTransactionResponse;
import com.asktech.pgateway.dto.setu.SetuErrorResponse;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.LetzpayTransactionDetails;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.LetzpayTransactionDetailsRepository;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.letzPay.LetzPayChecksumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Component
public class LetzpayStatus implements LetzPayConstants {

    @Value("${pgEndPoints.letzPayPaymentStatus}")
    String letzPayPaymentStatus;
    @Autowired
    LetzpayTransactionDetailsRepository letzpayTransactionDetailsRepository;

    public LetzTransactionStatusResponse callLetzPayStatusapi(PGConfigurationDetails pgConfigurationDetails,
            TransactionDetails transactionDetails) throws IOException, NoSuchAlgorithmException {
        List<LetzpayTransactionDetails> listLetzPayTransactionDetails = letzpayTransactionDetailsRepository
                .findByOrderId(transactionDetails.getOrderID());
        Logger logger = LoggerFactory.getLogger(LetzpayStatus.class);

        Map<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put(PAY_ID, listLetzPayTransactionDetails.get(0).getPayId());
        parameters.put(ORDER_ID, transactionDetails.getOrderID());
        parameters.put(AMOUNT, String.valueOf(transactionDetails.getAmount()));
        parameters.put(TXNTYPE, TXNTYPE_VALUE);
        parameters.put(CURRENCY_CODE, CURRENCY_CODE_VALUE);
        parameters.put(HASH, LetzPayChecksumUtils.generateCheckSum(parameters, pgConfigurationDetails.getPgSaltKey()));

        logger.info("LetzPay Request parameters :: " + parameters.toString());

        HttpResponse<Object> letzPayResponse = Unirest.post(letzPayPaymentStatus)
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(Utility.convertDTO2JsonString(parameters)).asObject(Object.class)
                .ifFailure(SetuErrorResponse.class, r -> {
                    SetuErrorResponse e = r.getBody();
                    try {
                        logger.info("LetzPay Status Request Response Error::" + Utility.convertDTO2JsonString(e));
                    } catch (JsonProcessingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                });

        ObjectMapper mapper = new ObjectMapper();
        LetzTransactionStatusResponse transactionStatusResponse = mapper.convertValue(letzPayResponse.getBody(),
                LetzTransactionStatusResponse.class);
        logger.info("LetzPay Status Response::" + Utility.convertDTO2JsonString(transactionStatusResponse));
        return transactionStatusResponse;
    }

    public List<MerchantTransactionResponse> LetzPopulateTransactionDetails(
            List<TransactionDetails> listTransactionDetails, LetzTransactionStatusResponse LetzStatusResponse) {

        MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
        List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

        for (TransactionDetails transactionDetails : listTransactionDetails) {
            merchantTransactionResponse
                    .setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
            merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
            merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
            merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
            merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());

            merchantTransactionResponse.setStatus(checkStatus(LetzStatusResponse.getResponseCode(),
                    LetzStatusResponse.getStatus(), LetzStatusResponse.getResponseMessage()));
            merchantTransactionResponse.setTxtMsg(LetzStatusResponse.getPgTxnMessage());

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

    public String checkStatus(String statusCode, String status, String statusMsg) {
        String[] failedCodes = { "001", "002", "004", "005", "007", "008", "009", "010", "011", "012", "013", "014",
                "015", "016", "017", "018", "019", "020", "021", "022", "023", "024","300","324" };
        String[] pendingCode = { "006", "003" };
        if (status.equalsIgnoreCase("Captured") && statusCode.equalsIgnoreCase("000")
                && statusMsg.equalsIgnoreCase("SUCCESS")) {
            return UserStatus.SUCCESS.toString();
        }
        if (Utility.inArray(failedCodes, statusCode)) {
            return UserStatus.FAILED.toString();
        }
        if (Utility.inArray(pendingCode, statusCode)) {
            return UserStatus.PENDING.toString();
        }
        return status;
    }
}
