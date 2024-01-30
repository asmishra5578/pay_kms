package com.asktech.pgateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
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
@Table(indexes = { 
    @Index(columnList = "paygUniqueId"),
    @Index(columnList = "orderId"),
    @Index(columnList = "merchnatkeyId"),
	@Index(columnList = "payGOrderKeyId"),
	@Index(columnList = "status")
})
public class PayGTransactionDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(length = 80)
	private String orderId;
	@Column(length = 80)
	private String paygUniqueId;
	@Column(length = 80)
	private String payGOrderKeyId;
	@Column(length = 80)
	private String merchnatkeyId;
	private String paymentType;
	private String orderAmount;
	private String emailId;
	private String mobileNo;
	@Column(columnDefinition = "LONGTEXT")
	private String inputRequest;
	@Column(columnDefinition = "LONGTEXT")
	private String orderResponse;
	@Column(length = 80)
	private String status;
	private String source;
	private String paymentId;
	private String paymentOrderId;
	private String paymentTransactionId;
	private String paymentResponseCode;
	private String paymentResponseText;
	
	
}
