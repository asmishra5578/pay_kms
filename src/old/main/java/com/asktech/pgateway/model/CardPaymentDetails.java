package com.asktech.pgateway.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CardPaymentDetails extends AbstractTimeStampAndId {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "card_number is mandatory")
	private String cardNumber;
	
	@NotBlank(message = "card_holder is mandatory")
	private String cardHolder;

	@NotBlank(message = "card_expiryMonth is mandatory")
	private String cardExpiryMonth;
	
	@NotBlank(message = "card_expiryYear is mandatory")
	private String cardExpiryYear;

	@NotBlank(message = "card_cvv is mandatory")
	private String cardCvv;

	@NotBlank(message = "OrderID is mandatory")
	private String orderId;

	@Min(value = 1, message = "Currency should not be less than 1")
	@NotBlank(message = "orderAmount is mandatory")
	private String orderAmount;

	@NotBlank(message = "orderCurrency is mandatory")
	private String orderCurrency;

	private String orderNote;

	@Size(min = 3, max = 200, message = "Name Me must be between 3 and 200 characters")
	private String customerName;

	@Email(message = "Email should be valid")
	private String customerEmail;

	private String customerPhone;

	private String paymentOption;

}
