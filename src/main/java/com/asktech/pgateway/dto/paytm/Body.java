package com.asktech.pgateway.dto.paytm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Body {

    private ResultInfo resultInfo;
    private String txnId;
    private String bankTxnId;
    private String orderId;
    private String txnAmount;
    private String txnType;
    private String gatewayName;
    private String bankName;
    private String mid;
    private String paymentMode;
    private String refundAmt;
    private String txnDate;
}
