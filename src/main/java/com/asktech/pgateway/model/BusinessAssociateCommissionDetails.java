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
@Table(name="ba_commission_details")
public class BusinessAssociateCommissionDetails extends AbstractTimeStampAndId{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;	
	@NotNull
	private String merchantID;	
	@NotNull
	private String uuid;
	@NotNull
	private String paymentType;
	private String paymentSubType;
	@NotNull
	private String commissionType;
	@NotNull
	private double commissionAmount;
	@NotNull
	private String createdBy;
	@NotNull
	private String status;
}
