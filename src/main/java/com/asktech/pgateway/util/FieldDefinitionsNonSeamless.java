package com.asktech.pgateway.util;

import java.util.ArrayList;

import com.asktech.pgateway.constants.nonseamless.CardsFields;
import com.asktech.pgateway.constants.nonseamless.NetBankingFields;
import com.asktech.pgateway.constants.nonseamless.UpiFields;
import com.asktech.pgateway.constants.nonseamless.WalletFields;


public class FieldDefinitionsNonSeamless implements CardsFields, UpiFields, NetBankingFields, WalletFields{
	private static ArrayList<String>keys;
	private static ArrayList<String> NonSeamlessFields() {
		keys = new ArrayList<String>();
		keys.add(NAME);
		keys.add(PHN);
		keys.add(EMAIL);				
		keys.add(PAYMENTOPTION);		
		keys.add(SIGNATURE);
		keys.add(ORDERAMOUNT);
		//keys.add(NOTIFYURL);
		return keys;
	}
	
	public static ArrayList<String>  cardsFields(){
		keys = NonSeamlessFields();
		keys.add(CARD_NUMBER);
		keys.add(CARD_EXPMONTH);
		keys.add(CARD_EXPYEAR);
		keys.add(CARD_CVV);
		keys.add(CARD_HOLDER);
		return keys;
	}
	
	public static ArrayList<String>  upiFields(){
		keys = NonSeamlessFields();
		keys.add(UPI_VPA);
		return keys;
	}
	public static ArrayList<String>  walletFields(){
		keys = NonSeamlessFields();
		keys.add(PAYMENT_CODE);
		return keys;
	}
	public static ArrayList<String>  netBankingFields(){
		keys = NonSeamlessFields();
		keys.add(NB_PAYMENT_CODE);
		return keys;
	}
}
