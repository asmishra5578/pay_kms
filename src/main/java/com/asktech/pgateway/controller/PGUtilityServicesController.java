package com.asktech.pgateway.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.service.PGUtilityServices;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/utility")
public class PGUtilityServicesController implements ErrorValues{

	static Logger logger = LoggerFactory.getLogger(PGUtilityServicesController.class);
	
	@Autowired
	PGUtilityServices pgUtilityServices;
	
	@GetMapping("/getCardBinCheck")
	@ApiOperation(value = "Get Merchant details from Merchant Credentials.", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getCardBinCheck(@RequestParam("cardBin") String cardBin) throws IOException
			{

		logger.info("In the controller");

		return ResponseEntity.ok().body(pgUtilityServices.getcardBinDetails(cardBin));
	}
}
