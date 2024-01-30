package com.asktech.pgateway.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class BulkFileUrlData extends AbstractTimeStampAndId{
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String createdByUuid;
	private String url;
	private String fileName;
	private String parsingStatus;
	private String fileType;
}
