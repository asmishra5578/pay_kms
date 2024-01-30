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
public class PgUtilityOptionConfiguration extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="pg_id")
	private String pgId;
	@Column(name="pg_name")
	private String pgName;
	@Column(name="utility_type")
	private String utilityType;
	@Column(name="pg_appid")
	private String pgAppId;
	@Column(name="pg_secret", columnDefinition = "LONGTEXT")
	private String pgSecret;
	@Column(name="pg_saltkey")
	private String pgSaltKey;
	@Column(name="pg_add1")
	private String pgAdd1;
	@Column(name="pg_add2")
	private String pgAdd2;
	@Column(name="pg_add3")
	private String pgAdd3;
	@Column(name="apiEndPoint")
	private String apiEndPoint;
	@Column(name="status")
	private String status;
	private String createdBy;
	private String updatedBy;
}
