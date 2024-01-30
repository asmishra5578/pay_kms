package com.asktech.pgateway.dto.admin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllMerchantDetails {
    private String merchantEMail;
    private String phoneNumber;
    private String merchantId;
    private String kycStatus;
    private List<MerchantPgdetails> merchantpgdetails;
    /*
    "merchantName": "Idea",
    "phoneNumber": "9900000001",
    "merchantId": "621520059677",
    "serviceType": "WALLET",
    "kycStatus": "ACTIVE",
    "serviceStatus": "ACTIVE",
    "pgname": "CASHFREE",
    "pgstatus": "ACTIVE"
    */
}
