package com.asktech.pgateway.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.error.ErrorResponseDto;
import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.login.LoginResponseDto;
import com.asktech.pgateway.dto.login.LogoutRequestDto;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.exception.JWTException;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.security.JwtGenerator;
import com.asktech.pgateway.service.UserLoginService;
import com.asktech.pgateway.util.JwtUserValidator;
import com.asktech.pgateway.util.Utility;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import jdk.jshell.spi.ExecutionControl.UserException;

@RestController
public class UserLoginController implements CashFreeFields, ErrorValues {

	@Autowired
	JwtUserValidator jwtValidator;

	@Autowired
	UserLoginService userLoginService;

	@Autowired
	private JwtGenerator jwtGenerator;

	public UserLoginController(JwtGenerator jwtGenerator) {
		this.jwtGenerator = jwtGenerator;
	}

	static Logger logger = LoggerFactory.getLogger(UserLoginController.class);
	

	@PostMapping(value = "/user/login")
	@ApiOperation(value = "User login and send OTP at registered mobile number. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {
		ErrorResponseDto erdto = new ErrorResponseDto();
		
		if (StringUtils.isEmpty(dto.getUserNameOrEmail())) {

			erdto = Utility.populateErrorDto(FormValidationExceptionEnums.FIELED_NOT_FOUND, null, "Field can't be empty", false,
					100);
			logger.error("Registration Failed.==> ");
			return ResponseEntity.ok().body(erdto);
		}
		
		LoginResponseDto loginResponseDto = userLoginService.getUserLogin(dto);
		
		String jwt = (jwtGenerator.generate(dto));
		loginResponseDto.setJwtToken(jwt);
		

		return ResponseEntity.ok().body(loginResponseDto);
	}

	
	@PutMapping(value = "api/user/logout")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> userLogout(@RequestBody LogoutRequestDto dto)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		ErrorResponseDto erdto = new ErrorResponseDto();
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (StringUtils.isEmpty(dto.getSessionToken()) || StringUtils.isEmpty(dto.getUuid())) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		 userLoginService.userLogout(dto);
		
		sdto.getMsg().add("User Has been Successfully LoggedOut from Application");
		sdto.setSuccessCode(SuccessCode.USER_LOGGED_OUT_SUCCESSFULLY);
		return ResponseEntity.ok().body(sdto);
	}

	@PutMapping(value = "user/initialPasswordChange")
	@ApiOperation(value = "User can resend OTP, if OTP is not received. ", authorizations = {
			@Authorization(value = "apiKey") })
	public ResponseEntity<?> initialPasswordChange(@RequestParam("userNameOrEmailId") String userNameOrEmailId ,@RequestParam("password") String password)
			throws UserException, JWTException, SessionExpiredException, ValidationExceptions {
		ErrorResponseDto erdto = new ErrorResponseDto();
		SuccessResponseDto sdto = new SuccessResponseDto();
		if (userNameOrEmailId == null) {

			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		 userLoginService.initiatlPasswordChange(userNameOrEmailId, password);
		
		sdto.getMsg().add("Password change successsfully done for userId :: "+userNameOrEmailId);
		sdto.setSuccessCode(SuccessCode.RESET_PASSWORD_SUCCESS);
		return ResponseEntity.ok().body(sdto);
	}


}
