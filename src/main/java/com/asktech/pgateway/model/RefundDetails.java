package com.asktech.pgateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="refund_details")
public class RefundDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;	
	@Column(name="initiatedBy")
	private String initiatedBy;
	@Column(name="updatedBy")
	private String updatedBy;
	@Column(name="refOrderId")
	private String refOrderId;
	@Column(name="amount")
	private String amount;
	@Column(name="merchantId")
	private String merchantId;
	@Column(name="paymentOption")
	private String paymentOption;
	@Column(name="paymentMode")
	private String paymentMode;
	@Column(name="pgOrderId")
	private String pgOrderId;
	@Column(name="pgStatus")
	private String pgStatus;
	@Column(name="pgTrTime")
	private String pgTrTime;
	@Column(name="userId")
	private String userId;
	@Column(name="merchantOrderId")
	private String merchantOrderId;
	@Column(name="paymentCode")
	private String paymentCode;
	@Column(name="vpaUpi")
	private String vpaUpi;
	@Column(name="status")
	private String status;
	@Column(name="refundMsg")
	private String refundMsg;
	
}
