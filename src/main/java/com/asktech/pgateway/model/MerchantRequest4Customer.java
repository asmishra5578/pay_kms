package com.asktech.pgateway.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantRequest4Customer extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="cust_name")
	private String custName;
	@Column(name="cust_phone")
	private String custPhone;
	@Column(name="cust_email")
	private String custEmail;
	@Column(name="amount")
	private String amount;
	@Column(name="orderid")
	private String orderId;
	@Column(name="ordercurrency")
	private String orderCurrency;
	@Column(name="return_url")
	private String returnUrl;
	@Column(name="ordernote")
	private String orderNote;
	@Column(name="signature" , columnDefinition = "TEXT")	
	private String signature;
	@Column(name="merchant_id")
	private String merchantId;
	@Column(name="app_id")
	private String appId;
	@Column(name="link_expiry")
	private int linkExpiry ;
	@Column(name="link_customer" , columnDefinition = "TEXT")
	private String linkCustomer;
	@Column(name="link_expiry_time")
	private Date linkExpiryTime;
	@Column(name="status")
	private String status;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="mailCounter")
	private int emailCounter;
	@Column(name="smsCounter", nullable = false, columnDefinition = "int default 0")
	private int smsCounter;
	@Column(name="source")
	private String source;
	
}
