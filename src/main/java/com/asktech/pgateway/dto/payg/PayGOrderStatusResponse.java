package com.asktech.pgateway.dto.payg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class PayGOrderStatusResponse {

	private String PaymentTransactionId;
	private String UniqueRequestId;
	private String OrderNotes;
	private String OrderKeyId;
	private String PaymentDateTime;
	private String OrderPaymentCustomerData;
	private String OrderStatus;
	private String OrderType;
	private String PaymentApprovalCode;
	private List<OrderPaymentTransactionDetail> OrderPaymentTransactionDetail;
	private String UpiLink;
	private String OrderPaymentStatus;
	private String OrderAmount;
	private String MerchantKeyId;
	private String OrderPaymentStatusText;
	private String PaymentStatus;
	private String ProductData;
	private String PaymentTransactionRefNo;
	private String PaymentAccount;
	private CustomerData CustomerData;
	private String PaymentResponseCode;
	private String PaymentProcessUrl;
	private String OrderId;
	private String PaymentResponseText;
	private String PaymentMethod;
	private String UpdatedDateTime;
	private String Id;
}
