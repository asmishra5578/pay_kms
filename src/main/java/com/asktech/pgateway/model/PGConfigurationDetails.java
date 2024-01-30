package com.asktech.pgateway.model;

import javax.persistence.Column;
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
public class PGConfigurationDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="pg_name")
	private String pgName;
	@Column(name="pg_api")
	private String pgApi;
	@Column(name="pg_app_id")
	private String pgAppId;
	@Column(name="pg_secret")
	private String pgSecret;
	@Column(name="pg_salt_key")
	private String pgSaltKey;
	@Column(name="pg_Add_info1")
	private String pgAddInfo1;
	@Column(name="pg_Add_info2")
	private String pgAddInfo2;
	@Column(name="pg_Add_info3")
	private String pgAddInfo3;
	@Column(name="status")
	private String status;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="pg_uuid")
	private String pgUuid;	
	
}
