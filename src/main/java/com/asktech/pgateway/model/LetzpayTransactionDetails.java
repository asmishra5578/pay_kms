package com.asktech.pgateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class LetzpayTransactionDetails extends AbstractTimeStampAndId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "responseDateTime")
	@JsonProperty("RESPONSE_DATE_TIME")
	private String responseDateTime;
	@Column(name = "responseCode")
	@JsonProperty("RESPONSE_CODE")
	private String responseCode;
	@Column(name = "txtId")
	@JsonProperty("TXN_ID")
	private String txtId;
	@Column(name = "custPhone")
	@JsonProperty("CUST_PHONE")
	private String custPhone;
	@Column(name = "mopType")
	@JsonProperty("MOP_TYPE")
	private String mopType;
	@Column(name = "acqId")
	@JsonProperty("ACQ_ID")
	private String acqId;
	@Column(name = "txtType")
	@JsonProperty("TXNTYPE")
	private String txtType;
	@Column(name = "currencyCode")
	@JsonProperty("CURRENCY_CODE")
	private String currencyCode;
	@Column(name = "rrn")
	@JsonProperty("RRN")
	private String rrn;
	@Column(name = "surchagreFlag")
	@JsonProperty("SURCHARGE_FLAG")
	private String surchagreFlag;
	@Column(name = "hashValue")
	@JsonProperty("HASH")
	private String hashValue;
	@Column(name = "paymentType")
	@JsonProperty("PAYMENT_TYPE")
	private String paymentType;
	@Column(name = "pgTxtMessage")
	@JsonProperty("PG_TXN_MESSAGE")
	private String pgTxtMessage;
	@Column(name = "returnUrl")
	@JsonProperty("RETURN_URL")
	private String returnUrl;
	@Column(name = "status")
	@JsonProperty("STATUS")
	private String status;
	@Column(name = "pgRefNumber")
	@JsonProperty("PG_REF_NUM")
	private String pgRefNumber;
	@Column(name = "payId")
	@JsonProperty("PAY_ID")
	private String payId;
	@Column(name = "orderId")
	@JsonProperty("ORDER_ID")
	private String orderId;
	@Column(name = "amount")
	@JsonProperty("AMOUNT")
	private String amount;
	@Column(name = "responseMessage")
	@JsonProperty("RESPONSE_MESSAGE")
	private String responseMessage;
	@Column(name = "bookingAmountFlag")
	@JsonProperty("BOOKING_MERCHANT_FLAG")
	private String bookingAmountFlag;
	@Column(name = "custEmail")
	@JsonProperty("CUST_EMAIL")
	private String custEmail;
	@Column(name = "totalAmount")
	@JsonProperty("TOTAL_AMOUNT")
	private String totalAmount;
	@Column(name = "custName")
	@JsonProperty("CUST_NAME")
	private String custName;
	@Column(name="updateFlag")
	private String updateFlag;
	@Column(name="updateCounter")
	private Integer updateCounter;

}
