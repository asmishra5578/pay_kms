package com.asktech.pgateway.model.seam;






import javax.persistence.Entity;


import com.asktech.pgateway.model.AbstractTimeStampAndId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CardPaymentDetails {

	private String cardnumber;
	private String expiry;
	private String cvv;
	
}
