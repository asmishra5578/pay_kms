package com.asktech.pgateway.dto.merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashBoardDetails {
	private String todaysTransactions;
	private String lastSettlements;
	private String unsettledAmount;
}
