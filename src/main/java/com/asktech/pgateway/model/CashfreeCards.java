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
public class CashfreeCards extends AbstractTimeStampAndId{

	   @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   private long id;
	   private String bank;
	   private String scheme;
	   private String countryCode;
	   private String subType;
	   private String type;
	   
}
