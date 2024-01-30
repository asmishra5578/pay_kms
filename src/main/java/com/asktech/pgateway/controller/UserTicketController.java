package com.asktech.pgateway.controller;

import java.text.ParseException;
import java.util.List;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.ticket.ComplaintTypeAndSubTypeResponse;
import com.asktech.pgateway.dto.ticket.TicketCreateRequest;
import com.asktech.pgateway.dto.ticket.TicketDetailsResponse;
import com.asktech.pgateway.dto.ticket.TicketUpdateRequest;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.TicketComplaintDetails;
import com.asktech.pgateway.model.UserAdminDetails;
import com.asktech.pgateway.security.JwtGenerator;
import com.asktech.pgateway.service.TicketingService;
import com.asktech.pgateway.util.CommissionCalculator;
import com.asktech.pgateway.util.JwtUserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class UserTicketController implements ErrorValues {

	@Autowired
	JwtUserValidator jwtValidator;

	@Autowired
	TicketingService ticketingService;
	@Autowired
	private JwtGenerator jwtGenerator;
	@Autowired
	private JwtUserValidator jwtUserValidator;
	@Autowired
	CommissionCalculator commissionCalculator;

	public UserTicketController(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}

	static Logger logger = LoggerFactory.getLogger(UserTicketController.class);

	@GetMapping(value = "/api/merchant/getComplaintTypeAndComplaintSubTpye")
	@ApiOperation(value = "Get ComplaintType And ComplaintSubTpye", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getComplaintSubTypeCategory(@RequestParam("uuid") String uuid)
			throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions {

		MerchantDetails merchantDetails = jwtUserValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("User Validation done :: " + merchantDetails.getMerchantEMail());

		List<ComplaintTypeAndSubTypeResponse> ticketComplaintType = ticketingService
				.getComplaintTypeAndSubTypeCategory(uuid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("complaintDetail", ticketComplaintType);
		return ResponseEntity.ok().body(sdto);
	}

	@PostMapping(value = "/api/merchant/createComplaint")
	@ApiOperation(value = "Create API for raised complaint ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> createComplaint(@RequestParam("uuid") String uuid,
			@RequestBody TicketCreateRequest ticketCreateRequest) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtUserValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("User Validation done :: " + merchantDetails.getMerchantEMail());

		TicketComplaintDetails ticketComplaintType = ticketingService.createTicket(uuid, ticketCreateRequest,
				merchantDetails);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("complaintDetail", ticketComplaintType);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "/api/merchant/updateTicket")
	@ApiOperation(value = "Create API for update raised complaint", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> updateTicketMerchant(@RequestParam("uuid") String uuid,
			@RequestBody TicketUpdateRequest ticketUpdateRequest) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtUserValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("User Validation done :: " + merchantDetails.getMerchantEMail());

		TicketDetailsResponse ticketComplaintType = ticketingService.updateTicketMerchant(uuid, ticketUpdateRequest,
				merchantDetails);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("complaintDetail", ticketComplaintType);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/merchant/getAll/complaintDetails")
	@ApiOperation(value = "Create API for get complaintDetails", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getComplaintDetails(
			@RequestParam("uuid") String uuid) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtUserValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("User Validation done :: " + merchantDetails.getMerchantEMail());

		List<TicketComplaintDetails> ticketComplaintType = ticketingService.getTicketDetails(uuid);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("complaintDetail", ticketComplaintType);
		return ResponseEntity.ok().body(sdto);
	}

	@GetMapping(value = "/api/merchant/complaintDetails")
	@ApiOperation(value = "Create API for get complaintDetails using complaintId ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> getComplaintDetailsUsingComplaintId(
			@RequestParam("uuid") String uuid, @RequestParam("complaintId") String complaintId) throws UserException, JWTException,
			SessionExpiredException, ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = jwtUserValidator.validatebyJwtMerchantDetails(uuid);
		logger.info("User Validation done :: " + merchantDetails.getMerchantEMail());

		TicketDetailsResponse ticketComplaintType = ticketingService.getComplaintDetailsUsingComplaintId(uuid, complaintId);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Request Processed Successfully!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("complaintDetail", ticketComplaintType);
		return ResponseEntity.ok().body(sdto);
	}
}
