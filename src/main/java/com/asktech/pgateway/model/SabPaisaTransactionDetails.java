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
public class SabPaisaTransactionDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "txStatus")	
	private String txStatus;
	@Column(name = "clientName")	
    private String clientName;
	@Column(name = "usern")	
    private String usern;
	@Column(name = "pass")	
    private String pass;
	@Column(name = "amt")	
    private String amt;
	@Column(name = "txnId")	
    private String txnId;
	@Column(name = "firstName")	
    private String firstName;	
	@Column(name = "contactNo")
	private String contactNo;
	@Column(name = "email")
	private String email;
	@Column(name = "modeTransfer")
	private String modeTransfer;	
	@Column(name = "source")
	private String source;
	@Column(name="responseText" , columnDefinition = "LONGTEXT")
	private String responseText;
	@Column(name = "txTime")	
    private String txTime;
	@Column(name = "pgTxnNo")	
    private String pgTxnNo;
	@Column(name = "sabPaisaTxId")	
    private String sabPaisaTxId;
	@Column(name = "updateFlag")
	private String updateFlag;
}
