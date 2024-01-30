package com.asktech.pgateway.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="user_admin_details")
public class UserAdminDetails extends AbstractTimeStampAndId{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String uuid;
	private String userId;
	private String password;
	private String initialPwdChange;
	private String userStatus;
	private String emailId;
	private String phoneNumber;
	private String userName;
	private String kycStatus;
	private String address1;
	private String address2;
	private String address3;
	private String pincode;
	private String city;
	private String country;
	private String compantName;
	private String userType;
	
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private UserSession userSession;
}
