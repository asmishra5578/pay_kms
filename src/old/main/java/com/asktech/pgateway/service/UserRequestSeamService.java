package com.asktech.pgateway.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.dto.seam.UserRequest;
import com.asktech.pgateway.dto.seam.UserResponse;
import com.asktech.pgateway.model.seam.CustomerRequest;
import com.asktech.pgateway.repository.seam.CustomerRequestRepository;

@Service
public class UserRequestSeamService {

	static Logger logger = LoggerFactory.getLogger(UserRequestSeamService.class);
	
	@Autowired
	CustomerRequestRepository customerRequestRepository;
	
	public UserResponse userRequest(UserRequest userRequest) {
		
		CustomerRequest customerRequest = new CustomerRequest();
		
		customerRequest.setAmount(userRequest.getAmount());
		customerRequest.setUserEmail(userRequest.getUserEmail());
		customerRequest.setUserName(userRequest.getUserName());
		customerRequest.setUserPhone(userRequest.getUserPhone());
		customerRequest.setUuid(UUID.randomUUID().toString());
		
		customerRequestRepository.save(customerRequest);
		
		UserResponse userResponse =new UserResponse();
		userResponse.setAmount(userRequest.getAmount());
		userResponse.setUserEmail(userRequest.getUserEmail());
		userResponse.setUserName(userRequest.getUserName());
		userResponse.setUserPhone(userRequest.getUserPhone());
		userResponse.setUuid(customerRequest.getUuid());
		
		return userResponse;
	}
}
