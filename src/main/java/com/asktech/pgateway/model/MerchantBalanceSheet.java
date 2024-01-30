package com.asktech.pgateway.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
public class MerchantBalanceSheet extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String merchantId;
	private String orderId;
	private String trType;
	private String userId ; 
	private int amount;
	private String pgStatus;
	private String pgOrderId;
	private String settlementStatus;
	private String pgSettlementStatus;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pgSettlementDate" ,nullable = true)
	private Date pgSettlementDate = new Date();
	@Temporal(TemporalType.TIMESTAMP )
	@Column(name = "settlementDate" ,nullable = true)
	private Date settlementDate = new Date();
	private String settleBy;
	private Integer bankId;
	private Integer pgCommission;
	private Integer askCommission;
	private Integer associateCommission;
	private Integer settleAmountToMerchant;
	private String merchantOrderId;
	private String pgId;
	private String cardNumber;
	private String paymentCode;
	private String paymentMode;
	private String vpaUPI;
	private String merchantType;
	private String processedBy;

}
