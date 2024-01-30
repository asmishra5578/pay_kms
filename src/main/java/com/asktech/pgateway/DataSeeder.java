package com.asktech.pgateway;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.enums.UserTypes;
import com.asktech.pgateway.model.UserAdminDetails;
import com.asktech.pgateway.repository.UserAdminDetailsRepository;
import com.asktech.pgateway.security.Encryption;

@Component
public class DataSeeder implements CommandLineRunner {

	@Value("${superAdminConfig.email}")
	String email;
	@Value("${superAdminConfig.phoneNo}")
	String phoneNumber;
	@Value("${superAdminConfig.companyName}")
	String companyName;
	@Value("${superAdminConfig.address1}")
	String address1;
	@Value("${superAdminConfig.address2}")
	String address2;
	@Value("${superAdminConfig.address3}")
	String address3;
	@Value("${superAdminConfig.country}")
	String country;
	@Value("${superAdminConfig.pincode}")
	String pincode;

	@Autowired
	UserAdminDetailsRepository repo;

	@Override
	public void run(String... args) throws Exception {
		UserAdminDetails user;
		user = repo.findByEmailId(email);
		if (user == null) {

			user = new UserAdminDetails();
			user.setUuid(UUID.randomUUID().toString());
			user.setUserName("SuperAdmin");
			user.setEmailId(email);
			user.setUserId(email);
			user.setUserStatus(UserStatus.ACTIVE.toString());
			user.setCompantName(companyName);
			user.setPhoneNumber(phoneNumber);
			user.setAddress1(address1);
			user.setAddress2(address2);
			user.setAddress3(address3);
			user.setCountry(country);
			user.setUserType(UserTypes.SUPER.toString());
			user.setPassword(Encryption.getEncryptedPassword("Welcome#123"));

			repo.save(user);

		}

	}

}
