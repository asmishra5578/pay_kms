package com.asktech.pgateway.dto.paytmwebhook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodalWebhookReqData {
private String transactionType;
private String amount;
private String response_code;
private String clientRequestId;
private String transactionRequestId;
private String transactionDate;
private String status;
private NodalWebhookReqDataExtInfo extra_info;

}
