package com.asktech.pgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.asktech.pgateway.customInterface.IUserDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, String>{
		
	UserDetails findAllByEmailIdAndPhoneNumberAndCardNumberAndMerchantId(String customerEmail, String customerPhone,
			String card_number, String string);
	
	UserDetails findAllByEmailIdAndPhoneNumberAndMerchantId(String customerEmail, String customerPhone, String string);

	List<UserDetails> findAllByEmailIdOrPhoneNumber(String custEmailorPhone, String custEmailorPhone2);

	UserDetails findAllByEmailIdAndPhoneNumberAndMerchantIdAndCustomerNameAndPaymentOptionAndPaymentCode(String emailId,
			String phoneNumber, String merchantId, String customerName, String paymentOption, String paymentCode);

	UserDetails findAllByEmailIdAndPhoneNumberAndPaymentCodeAndMerchantId(String string, String string2, String string3,
			String merchantID);

	UserDetails findAllByEmailIdAndPhoneNumberAndVpaUPIAndMerchantId(String string, String string2, String string3,
			String merchantID);

	UserDetails findById(long userID);

	@Query(value = "select distinct customer_name customerName,email_id emailId,phone_number phoneNumber, "
			+ "card_number cardNumber,vpaupi vpaUpi,payment_code paymentCode "
			+ "from user_details where email_id= :emailId or phone_number= :phoneNumber",
			nativeQuery = true)
	List<IUserDetails> getDistinctUserDetails(@Param("emailId") String emailId, @Param("phoneNumber") String phoneNumber);
	
	@Query(value = "select distinct customer_name customerName,email_id emailId,phone_number phoneNumber, "
			+ "card_number cardNumber,vpaupi vpaUpi,payment_code paymentCode "
			+ "from user_details ",
			nativeQuery = true)
	List<IUserDetails> getDistinctUserDetailsAll();

}
