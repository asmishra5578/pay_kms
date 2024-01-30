package com.asktech.pgateway.dto.merchant;

import com.asktech.pgateway.model.CashfreeCards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExposeCashfreeApi {

	   private CashfreeCards cards;
	   private String status;
	   
}
