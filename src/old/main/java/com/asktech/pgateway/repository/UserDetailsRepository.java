package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, String>{
		
	UserDetails findAllByEmailIdAndPhoneNumberAndCardNumberAndMerchantId(String customerEmail, String customerPhone,
			String card_number, String string);

	UserDetails findAllByEmailIdOrPhoneNumber(String custEmailorPhone, String custEmailorPhone2);

}
