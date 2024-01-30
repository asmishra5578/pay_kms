package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.UserOTPDetails;

public interface UserOTPDetailsRepository extends JpaRepository<UserOTPDetails, String>{

	UserOTPDetails findByUuid(String uuid);

}
