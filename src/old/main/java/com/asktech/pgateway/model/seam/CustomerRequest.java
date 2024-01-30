package com.asktech.pgateway.model.seam;

import java.util.Date;

import javax.persistence.Column;
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
public class CustomerRequest extends AbstractTimeStampAndId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String uuid;
	private String userName;
	private String userPhone;
	private String userEmail;
	private Integer amount;
	private String returnUrl;
	private String sessionStatus;
	@Column(unique = true)
	private String sessionToken;
	private Date sessionExpiryDate;
	private Date idealSessionExpiry;
	@Column(unique = true)
	private String orderId;
	private String customerId;
	private String orderCurrency;
	private String orderNote;

}
