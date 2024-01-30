package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.asktech.pgateway.model.MobileOtp;

public interface MobileOtpRepository extends JpaRepository<MobileOtp, String>{

	MobileOtp findBymobileNo(String phoneNumber);

	MobileOtp findByOtpAndMobileNo(int otp, String userNameOrEmail);

	MobileOtp findByOtpAndUserName(int otp, String userNameOrEmail);

	MobileOtp findByOtpAndUserNameAndOtpSessionId(int otp, String userNameOrEmail, String sessionId);
	
	@Modifying
	@Transactional
	@Query(value = "TRUNCATE TABLE mobile_otp ", nativeQuery = true)
	void truncateOtp();
	

	void deleteByMobileNo(String phoneNumber);

}
