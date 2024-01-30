package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.UserAdminDetails;

public interface UserAdminDetailsRepository extends JpaRepository<UserAdminDetails, String>{

	UserAdminDetails findByUserId(String userNameOrEmail);

	UserAdminDetails findByEmailId(String email);

	UserAdminDetails findByuuid(String uuid);


}
