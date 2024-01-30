package com.asktech.pgateway.service.merchantApi.statusApis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.dto.merchant.MerchantTransactionResponse;
import com.asktech.pgateway.dto.sabPaisa.SabPaisaStatusRequest;
import com.asktech.pgateway.dto.sabPaisa.SabPaisaTransactionStatus;
import com.asktech.pgateway.model.SabPaisaTransactionDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.SabPaisaTransactionDetailsRepository;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Component
public class SabPaisaStatus {
    Logger logger = LoggerFactory.getLogger(NsdlStatus.class);
    @Autowired
    SabPaisaTransactionDetailsRepository sabPaisaTransactionDetailsRepository;

    public List<MerchantTransactionResponse> SabPaisaPopulateTransactionDetails(
            List<TransactionDetails> listTransactionDetails) throws JsonProcessingException {
        MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
        List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();
        for (TransactionDetails transactionDetails : listTransactionDetails) {
            if (check24HrsPassed(transactionDetails.getCreated())) {
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
                return listMerchantTransactionResponse;
            }
            try {
                SabPaisaTransactionDetails sabPaisaTransactionDetails = sabPaisaTransactionDetailsRepository
                        .findByTxnId(transactionDetails.getOrderID());
                logger.info(Utility.convertDTO2JsonString(transactionDetails.getOrderID()));
                logger.info(Utility.convertDTO2JsonString(sabPaisaTransactionDetails));
                SabPaisaStatusRequest sabPaisaStatusRequest = new SabPaisaStatusRequest();
                sabPaisaStatusRequest.setClientCode(sabPaisaTransactionDetails.getClientName());
                sabPaisaStatusRequest.setClientxnId(sabPaisaTransactionDetails.getTxnId());

                SabPaisaTransactionStatus sabPaisaTransactionStatus = callSabPaisaStatusapi(sabPaisaStatusRequest);
                logger.info("SabPaisaTransactionStatus::" + Utility.convertDTO2JsonString(sabPaisaTransactionStatus));
                logger.info(Utility.convertDTO2JsonString(transactionDetails.getOrderID()));
                merchantTransactionResponse
                        .setAmount(Utility.getAmountConversion(String.valueOf(transactionDetails.getAmount())));
                merchantTransactionResponse.setMerchantId(transactionDetails.getMerchantId());
                merchantTransactionResponse.setMerchantOrderId(transactionDetails.getMerchantOrderId());
                merchantTransactionResponse.setOrderID(transactionDetails.getOrderID());
                merchantTransactionResponse.setPaymentOption(transactionDetails.getPaymentOption());
                String sta = null;
                String txtmsg = null;
                if (sabPaisaTransactionStatus.getMessage() != null) {
                    txtmsg = sabPaisaTransactionStatus.getError();
                } else {
                    txtmsg = sabPaisaTransactionStatus.getSabPaisaRespCode();
                }
                merchantTransactionResponse.setTxtMsg(txtmsg);
                sta = trxStatus(sabPaisaTransactionStatus.getTxnStatus());
                merchantTransactionResponse.setStatus(sta);
                merchantTransactionResponse.setTxtPGTime(transactionDetails.getTxtPGTime());
                merchantTransactionResponse.setPaymentMode(transactionDetails.getPaymentMode());

                if (transactionDetails.getVpaUPI() != null) {
                    merchantTransactionResponse.setVpaUPI(
                            SecurityUtils.decryptSaveData(transactionDetails.getVpaUPI()).replace("\u0000", ""));
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
                e.printStackTrace();
            }
        }
        return listMerchantTransactionResponse;
    }

    private String trxStatus(String trx) {
        if (trx.equals("INITIATED")) {
            return "PENDING";
        }
        return trx;
    }

    @Value("${pgEndPoints.sabPaisaStatusAPI}")
    String sabPaisaStatusAPI;

    public SabPaisaTransactionStatus callSabPaisaStatusapi(SabPaisaStatusRequest sabPaisaStatusRequest)
            throws JsonProcessingException {

        // AsanpayStatusAPI
        logger.info("sabPaisaStatusAPI link::" + sabPaisaStatusAPI);
        logger.info("sabPaisaStatusAPI Request:: " + Utility.convertDTO2JsonString(sabPaisaStatusRequest));
        HttpResponse<SabPaisaTransactionStatus> sabPaisaTransactionDetails = Unirest
                .post(sabPaisaStatusAPI)
                .header("Content-Type", "application/json").body(Utility.convertDTO2JsonString(sabPaisaStatusRequest))
                .asObject(SabPaisaTransactionStatus.class).ifFailure(Object.class, r -> {
                    Object e = r.getBody();
                    try {
                        
                        logger.info("SabPaisa Status Request Response Error::" + Utility.convertDTO2JsonString(e));
                    } catch (JsonProcessingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                });

        // logger.info("Response :: " +
        // Utility.convertDTO2JsonString(sabPaisaTransactionDetails.getBody()));

        return sabPaisaTransactionDetails.getBody();

    }

    private boolean check24HrsPassed(Date val) {
        logger.info("TIME::"+String.valueOf(val));
        long dt = (val.getTime() / 1000);
        long curtime = System.currentTimeMillis() / 1000;
        logger.info("DT::" + String.valueOf(dt) + "|curtime::" + String.valueOf(curtime));
        if (curtime - dt > 105061) {
            logger.info("TRUE:" + String.valueOf(curtime - dt));
            return true;
        }
        return false;
    }

}
