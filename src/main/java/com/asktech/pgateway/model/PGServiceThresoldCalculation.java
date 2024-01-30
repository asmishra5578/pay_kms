package com.asktech.pgateway.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PGServiceThresoldCalculation extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="pgid")
	private String pgId;
	@Column(name="servicetype")
	private String serviceType;
	@Column(name="daywiseamount")
	private long daywiseAmount;
	@Column(name="weekwiseamount")
	private long weekwiseAmount;
	@Column(name="monthwiseamount")
	private long monthwiseAmount;
	@Column(name="month3wiseamount")
	private long month3wiseAmount;
	@Column(name="month6wiseamount")
	private long month6wiseAmount;
	@Column(name="yearwiseamount")
	private long yearwiseAmount;
	@Column(name="createdby")
	private String createdBy;
	@Column(name="updatedby")
	private String updatedBy;
}
