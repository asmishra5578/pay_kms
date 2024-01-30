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
public class MerchantKycDetailsResponse {

	private String merchantLegalName;
	private String panCardNumber;
	private String gstId;
	private String webstieUrl;
	private String businessEntityType;
	private String productDescription;
	private String tanNumber;
	private String regName;
    private String regAddress;
    private String regPinCode;
    private String regNumber;
    private String regEmailAddress;
    private String commName;
    private String commAddress;
    private String commPinCode;
    private String commNumber;
    private String commEmailAddress;
  
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
