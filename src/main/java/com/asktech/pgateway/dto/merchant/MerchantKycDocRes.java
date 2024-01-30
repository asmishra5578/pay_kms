package com.asktech.pgateway.dto.merchant;

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
public class MerchantKycDocRes {

    private String cancelledChequeOrAccountProof;
    private String certificateOfIncorporation ;
    private String businessPAN ;
    private String certificateOfGST ;
    private String directorKYC ;
    private String aoa;
    private String moa;
    private String certficateOfNBFC ;
    private String certficateOfBBPS ;
    private String certificateOfSEBIOrAMFI;
}
