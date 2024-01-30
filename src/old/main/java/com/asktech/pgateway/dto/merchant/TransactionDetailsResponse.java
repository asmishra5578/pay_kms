package com.asktech.pgateway.dto.merchant;

import java.util.List;

import com.asktech.pgateway.model.TransactionDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetailsResponse {

	private List<TransactionDetails> transactionDetails; 
}
