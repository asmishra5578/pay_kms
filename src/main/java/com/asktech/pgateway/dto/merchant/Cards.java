package com.asktech.pgateway.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cards {

	   private String bank;
	   private String scheme;
	   private String countryCode;
	   private String subType;
	   private String type;
	   
}
