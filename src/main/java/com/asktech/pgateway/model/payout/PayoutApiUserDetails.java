package com.asktech.pgateway.model.payout;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.asktech.pgateway.model.AbstractTimeStampAndId;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PayoutApiUserDetails extends AbstractTimeStampAndId {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int sno;
	private String merchantId;
	private String token;
	private String whitelistedip;
}
