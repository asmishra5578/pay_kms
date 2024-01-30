package com.asktech.pgateway.dto.easebuzz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Msg { 

   private String firstname;
   private String merchant_logo;
   private String cardCategory;
   private String udf10;
   private String error;
   private String addedon;
   private String mode;
   private String udf9;
   private String udf7;
   @JsonProperty("issuing_bank")
   private String issuingBank;
   private String udf8;
   @JsonProperty("cash_back_percentage")
   private String cashBackPercentage;
   @JsonProperty("deduction_percentage")
   private Double deductionPercentage;
   @JsonProperty("error_Message")
   private String errorMessage;
   @JsonProperty("payment_source")
   private String paymentSource;
   @JsonProperty("bank_ref_num")
   private String bankRefNum;
   private String key;
   private String bankcode;
   private String email;
   private String upi_va;
   private String txnid;
   private String amount;
   private String unmappedstatus;
   private String easepayid;
   private String udf5;
   private String udf6;
   private String udf3;
   private String surl;
   @JsonProperty("net_amount_debit")
   private String netAmountDebit;
   private String udf4;
   private String udf1;
   private String card_type;
   private String udf2;
   private String cardnum;
   private String phone;
   private String furl;
   private String productinfo;
   private String hash;
   @JsonProperty("PG_TYPE")
   private String pgTYPE;
   @JsonProperty("name_on_card")
   private String nameOnCard;
   private String status;

}
