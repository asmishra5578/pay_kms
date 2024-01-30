package com.asktech.pgateway.dto.razorpay;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Items { 

   private String fee;
   private String description;
   private String created_at;
   private String amount_refunded;
   private String bank;
   private String error_reason;
   private String error_description;
   private Acquirer_data acquirer_data;
   private String captured;
   private String contact;
   private String invoice_id;
   private String currency;
   private String id;
   private String international;
   private String email;
   private String amount;
   private String refund_status;
   private String wallet;
   private String method;
   private String vpa;
   private String error_source;
   private String error_step;
   private String tax;
   private String card_id;
   private String gateway_provider;
   private String error_code;
   private String order_id;
   private String entity;
   private String status;

}