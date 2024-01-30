package com.asktech.pgateway.model.seam;






import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardPaymentDetails {

	private String cardnumber;
	private String expiry;
	private String cvv;
	
}
