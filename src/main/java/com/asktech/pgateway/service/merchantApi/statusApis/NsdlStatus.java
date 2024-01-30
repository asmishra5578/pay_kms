package com.asktech.pgateway.service.merchantApi.statusApis;

import java.util.ArrayList;
import java.util.List;

import com.asktech.pgateway.dto.merchant.MerchantTransactionResponse;
import com.asktech.pgateway.dto.nsdl.NSDLStatusResponseDecode;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.nsdl.NSDLUtilityClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NsdlStatus {
    @Autowired
    PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
    @Autowired
    TransactionDetailsRepository transactionDetailsRepository;
    @Autowired
    MerchantPGDetailsRepository merchantPGDetailsRepository;
    @Autowired
    MerchantDetailsRepository merchantDetailsRepository;
    @Autowired
    NSDLUtilityClass nSDLUtilityClass;
    Logger logger = LoggerFactory.getLogger(NsdlStatus.class);

    public List<MerchantTransactionResponse> NSDLPopulateTransactionDetails(
            List<TransactionDetails> listTransactionDetails) {

        MerchantTransactionResponse merchantTransactionResponse = new MerchantTransactionResponse();
        List<MerchantTransactionResponse> listMerchantTransactionResponse = new ArrayList<MerchantTransactionResponse>();

        for (TransactionDetails transactionDetails : listTransactionDetails) {
            // PGConfigurationDetails pgConfigurationDetails =
            // pgConfigurationDetailsRepository
            // .findByPgName(transactionDetails.getPgType());
            try {
                logger.info("Run Status API::");
                NSDLStatusResponseDecode StatusResponse = nSDLUtilityClass.getUPIDBStatusDetails(transactionDetails.getOrderID(), false);
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
                if (StatusResponse.getResponseMessage() != null) {
                    txtmsg = StatusResponse.getResponseMessage();
                    reason = StatusResponse.getResponseCode();
                    merchantTransactionResponse.setTxtMsg(StatusResponse.getResponseMessage());
                }
                logger.info("NSDL Payment Status::" + sta + "|reason::" + reason + "|txtmsg::" + txtmsg);
                merchantTransactionResponse.setStatus(sta);
                // if (sta != null) {
                // merchantTransactionResponse.setStatus(sta);
                // merchantTransactionResponse.setTxtMsg(txtmsg);
                // }
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

            }
        }
        return listMerchantTransactionResponse;
    }
}
