package com.asktech.pgateway.model.payout;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.asktech.pgateway.model.AbstractTimeStampAndId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class PayoutBulkTransaction extends AbstractTimeStampAndId{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String merchantId;
	private String serviceType;
	private String phnumber;
	private String amount;
	private String bankaccount;
	private String ifsc;
	private String beneficiaryName;
	private String requestType;
	private String UPIId;
	private String status;
	private String payoutMsg;
	private String fileMsg;
	private String fileName;
}
