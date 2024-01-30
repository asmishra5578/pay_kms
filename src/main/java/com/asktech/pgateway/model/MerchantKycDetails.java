package com.asktech.pgateway.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="merchant_kyc_details")
public class MerchantKycDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String merchantID;
	private String merchantLegalName;
	private String panCardNumber;
	private String gstId;
	private String webstieUrl;
	private String businessEntityType;
	private String productDescription;
	private String MerchantKycStatus;
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
    private String kycComment;
  
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
