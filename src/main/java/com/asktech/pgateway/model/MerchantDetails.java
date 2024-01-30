package com.asktech.pgateway.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="merchant_details")
public class MerchantDetails extends AbstractTimeStampAndId{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String merchantID;
	private String appID;
	private String uuid;
	private String secretId;
	private String merchantEMail;
	private String password;
	private String initialPwdChange;
	private String userStatus;
	private String phoneNumber;
	private String merchantName;
	private String kycStatus;
	private String createdBy;
	private String saltKey;
	private String tr_mail_flag;
	private String merchantType;
	private String designation;
	private String companyName;
	private String companyType;
	private String supportEmailId;
	private String supportPhoneNo;
	private String logoUrl;
	private String payoutFlag;
	@Column(columnDefinition = "VARCHAR(255) default 'DISABLE' ")
	private String otpStatus;
	@Column(columnDefinition = "integer default 1")
	private int failCount;
	private String updateBy;
	private String updateReason;
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private UserSession userSession;
	
}


