package com.asktech.pgateway.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="business_associate")
public class BusinessAssociate extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@NotNull
	private String merchantID;	
	private String uuid;
	@NotNull
	private String phoneNumber;
	@NotNull
	private String emailId;
	private String address;
	@NotNull
	private String name;
	private String createdBy;
	@NotNull
	private String bankName;
	@NotNull
	private String bankAccountNo;
	@NotNull
	private String ifscCode;
	@NotNull
	private String micrCode;
	private String nickName;
	
}
