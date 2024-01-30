package com.asktech.pgateway.model.seam;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BankList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	private long id;
	private String bankname;
	private String bankcode;
	private String pgBankCode;
	private String pgName;
	private String status;
	
}
