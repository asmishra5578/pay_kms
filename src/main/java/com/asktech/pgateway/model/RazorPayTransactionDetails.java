package com.asktech.pgateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Index;

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
	@Index(name = "razorPayOrderId_index", columnList = "razorPayOrderId"),
	@Index(name = "orderId_index", columnList = "orderId"),
	@Index(name = "merchantId_index", columnList = "merchantId")
})
public class RazorPayTransactionDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String merchantId;
	private String orderId;
	private String pgId;
	private String amount;
	private String razorPayOrderId;
	private String status;
	private String paymentMode;
	@Column(name="generatedHtml" , columnDefinition = "LONGTEXT")
	private String generatedHtml;
	private String updateFlag;
	private String source;
	private String signature;
	private String razorPayPaymentId;

}
