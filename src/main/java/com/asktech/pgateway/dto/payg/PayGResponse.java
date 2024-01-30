package com.asktech.pgateway.dto.payg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class PayGResponse {

	@JsonProperty("PaymentTransactionId")
	private String PaymentTransactionId;
	@JsonProperty("UniqueRequestId")
	private String UniqueRequestId;
	@JsonProperty("OrderNotes")
	private String OrderNotes;
	@JsonProperty("OrderKeyId")
	private String OrderKeyId;
	@JsonProperty("PaymentDateTime")
	private String PaymentDateTime;
	@JsonProperty("OrderPaymentCustomerData")
	private OrderPaymentCustomerData OrderPaymentCustomerData;
	@JsonProperty("OrderStatus")
	private String OrderStatus;
	@JsonProperty("OrderType")
	private String OrderType;
	@JsonProperty("PaymentApprovalCode")
	private String PaymentApprovalCode;
	@JsonProperty("OrderPaymentTransactionDetail")
	private String OrderPaymentTransactionDetail;
	@JsonProperty("UpiLink")
	private String UpiLink;
	@JsonProperty("OrderPaymentStatus")
	private String OrderPaymentStatus;
	@JsonProperty("OrderAmount")
	private String OrderAmount;
	@JsonProperty("MerchantKeyId")
	private String MerchantKeyId;
	@JsonProperty("OrderPaymentStatusText")
	private String OrderPaymentStatusText;
	@JsonProperty("PaymentStatus")
	private String PaymentStatus;
	@JsonProperty("ProductData")
	private String ProductData;
	@JsonProperty("PaymentTransactionRefNo")
	private String PaymentTransactionRefNo;
	@JsonProperty("UserDefinedData")
	private String UserDefinedData;
	@JsonProperty("PaymentAccount")
	private String PaymentAccount;
	@JsonProperty("CustomerData")
	private CustomerData CustomerData;
	@JsonProperty("PaymentResponseCode")
	private String PaymentResponseCode;
	@JsonProperty("PaymentProcessUrl")
	private String PaymentProcessUrl;
	@JsonProperty("OrderId")
	private String OrderId;
	@JsonProperty("PaymentResponseText")
	private String PaymentResponseText;
	@JsonProperty("PaymentMethod")
	private String PaymentMethod;
	@JsonProperty("UpdatedDateTime")
	private String UpdatedDateTime;
	@JsonProperty("Id")
	private String Id;
	@JsonProperty("ResponseCode")
	private String ResponseCode;
	@JsonProperty("Code")
	private String Code;
	@JsonProperty("Message")
	private String Message;
	@JsonProperty("FieldName")
	private String FieldName;
	@JsonProperty("DeveloperMessage")
	private String DeveloperMessage;
	@JsonProperty("MoreInfoUrl")
	private String MoreInfoUrl;
	@JsonProperty("RequestUniqueId")
	private String RequestUniqueId;
	private PayGErrorResponse payGErrorResponse;

}