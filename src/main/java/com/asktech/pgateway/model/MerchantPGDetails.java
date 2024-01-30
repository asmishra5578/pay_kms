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
public class MerchantPGDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="merchantid")
	private String merchantID;
	@Column(name="merchantpgid")
	private String merchantPGId;
	@Column(name="merchantpgname")
	private String merchantPGName;
	@Column(name="merchantpgapp_id")
	private String merchantPGAppId;
	@Column(name="merchantpgsecret")
	private String merchantPGSecret;
	@Column(name="merchantpgsalt_key")
	private String merchantPGSaltKey;
	@Column(name="merchantPGAdd1")
	private String merchantPGAdd1;
	@Column(name="merchantPGAdd2")
	private String merchantPGAdd2;
	@Column(name="merchantPGAdd3")
	private String merchantPGAdd3;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="status")
	private String status; 
	@Column(name="reason")
	private String reason;
	
}
