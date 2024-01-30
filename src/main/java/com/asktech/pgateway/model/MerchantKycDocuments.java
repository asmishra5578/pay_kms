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
@Table(name="merchant_kyc_documents")
public class MerchantKycDocuments {
	
	    @Id
     	@GeneratedValue(strategy = GenerationType.IDENTITY)
    	private long id;
	    private String merchantID;
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
