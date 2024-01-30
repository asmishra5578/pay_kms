package com.asktech.pgateway.model;

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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EaseBuzzTransactionDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "txStatus")	
	private String txStatus;
	@Column(name = "orderAmount")	
    private String orderAmount;
	@Column(name = "orderId")	
    private String orderId;
	@Column(name = "paymentMode")	
    private String paymentMode;
	@Column(name = "txTime")	
    private String txTime;
	@Column(name = "signature")	
    private String signature;
	@Column(name = "txMsg")	
    private String txMsg;
	@Column(name = "referenceId")	
    private String referenceId;
	@Column(name = "merchantOderId")
	private String merchantOrderId;
	@Column(name = "updateFlag")
	private String updateFlag;
	@Column(name = "source")
	private String source;
	@Column(name = "email")
	private String email;
	@Column(name = "phoneNo")
	private String phoneNo;
	@Column(name="responseText" , columnDefinition = "LONGTEXT")
	private String responseText;
}
