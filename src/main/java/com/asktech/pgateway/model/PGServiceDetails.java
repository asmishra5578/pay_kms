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
public class PGServiceDetails extends AbstractTimeStampAndId{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="pg_id")
	private String pgId;
	@Column(name="pg_services")
	private String pgServices;
	@Column(name="status")
	private String status;
	@Column(name="thresold_day")
	private long thresoldDay;
	@Column(name="thresold_week")
	private long thresoldWeek;
	@Column(name="thresold_month")
	private long thresoldMonth;
	@Column(name="thresold_3month")
	private long thresold3Month;
	@Column(name="thresold_6month")
	private long thresold6Month;
	@Column(name="thresold_year")
	private long thresoldYear;
	@Column(name="created_by")
	private String createdBy;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="defaultService")
	private String defaultService;
	@Column(name="priority")
	private Integer priority;

}
