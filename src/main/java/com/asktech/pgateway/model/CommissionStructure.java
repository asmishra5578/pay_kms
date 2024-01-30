package com.asktech.pgateway.model;

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
public class CommissionStructure extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String merchantId;
	private String merchantType;
	private String serviceType;
	private String pgId;
	private String status;
	private String pgCommissionType;
	private Integer pgAmount ;
	private String askCommissionType;
	private Integer askAmount ;
	private String cardType;
	private String cardSeriesStart;
	private String cardSeriesEnd;
	private String cardMaker;
	private String bankName;
	private String walletType;

}
