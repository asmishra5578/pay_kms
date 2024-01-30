package com.asktech.pgateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {

   private String pgId;
   private String orderNote;
   private String emailId;
   private String merchantOrderId;
   private String reconStatus;
   private String source;
   private String userID;
   private String merchantReturnURL;
   private String createdAt;
   private String merchantId;
   private String paymentOption;
   private String id;
   private String updatedAt;
   private String amount;
   private String txtPGTime;
   private String pgType;
   private String orderID;
   private String paymentMode;
   private String created;
   private String txtMsg;
   private String paymentCode;
   private String vpaUPI;
   private String merchantAlertURL;
   private String custOrderId;
   private String updated;
   private String pgOrderID;
   private String cardNumber;
   private String status;

}