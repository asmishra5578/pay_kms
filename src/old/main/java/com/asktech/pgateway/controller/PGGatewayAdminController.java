package com.asktech.pgateway.controller;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.merchant.MerchantResponse;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.service.PGGatewayAdminService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/admin")
public class PGGatewayAdminController {

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);
	
	@Autowired
	PGGatewayAdminService pgGatewayAdminService;
	
	@PostMapping("/createMerchant")
	public ResponseEntity<?> createMerchant(@RequestBody String strCreateMerchant) throws IllegalAccessException, NoSuchAlgorithmException, ValidationExceptions{
		
		MerchantCreateResponse merchantCreateResponse = pgGatewayAdminService.createMerchant(strCreateMerchant);
		
		return ResponseEntity.ok().body(merchantCreateResponse);
	}
	
	
}
