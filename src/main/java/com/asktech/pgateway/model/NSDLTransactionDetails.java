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
public class NSDLTransactionDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String bankId;
	private String merchantId;
	private String orderId;
	private String retailerOrderId;
	private String pgId;
	private String authErrorMessage;
	@Column(columnDefinition = "LONGTEXT")
	private String authEncData;
	private String authErrorCode;
	private String authTerminalId;
	@Column(name = "TransactionStatus")
	private String transactionStatus;
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
	@Column(name = "responceCode")
	private String responceCode;
	@Column(name = "approvalCode")
	private String approvalCode;
}
