package com.asktech.pgateway.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessSettlement {

	private String orderid;
	private Integer settlementAmount;
	private Integer custCommission;
	private Integer pgCommission;
	private String	remarks;
}
