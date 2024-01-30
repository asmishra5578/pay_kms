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
@Table(name="service_wise_payment_thresold_log")
public class ServiceWisePaymentThresoldLog extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="old_service")
	private String old_service;
	@Column(name="old_pgid")
	private String old_pgid;
	@Column(name="old_merchant_id")
	private String old_merchant_id;
	@Column(name="old_status")
	private String old_status;
	@Column(name="old_thresold_value")
	private long old_thresold_value;
	@Column(name="new_service")
	private String new_service;
	@Column(name="new_pgid")
	private String new_pgid;
	@Column(name="new_merchant_id")
	private String new_merchant_id;
	@Column(name="new_status")
	private String new_status;
	@Column(name="new_thresold_value")
	private long new_thresold_value;
}
