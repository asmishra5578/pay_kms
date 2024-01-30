package com.asktech.pgateway.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransactionDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long userID;
	private String merchantId;
	private String pgId;
	private int amount;
	private String paymentOption;
	private String orderID;
	private String pgOrderID;
	private String pgType;
	private String status;
	private String paymentMode;
	private String txtMsg;
	private String txtPGTime;
	private String merchantOrderId;
	private String custOrderId;
	private String merchantReturnURL;
	private String cardNumber;
	private String paymentCode;
	private String vpaUPI;
	private String reconStatus;
	private String source;
	private String merchantAlertURL;
	private String orderNote;
	private String emailId;
	
}
