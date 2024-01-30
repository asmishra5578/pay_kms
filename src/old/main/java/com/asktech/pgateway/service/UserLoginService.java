package com.asktech.pgateway.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.constants.cashfree.CashFreeFields;
import com.asktech.pgateway.dto.login.LoginRequestDto;
import com.asktech.pgateway.dto.login.LoginResponseDto;
import com.asktech.pgateway.dto.login.LogoutRequestDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.UserSession;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.security.Encryption;

import jdk.jshell.spi.ExecutionControl.UserException;

@Service
public class UserLoginService implements CashFreeFields, ErrorValues {

	@Autowired
	MerchantDetailsRepository repo;

	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;

	long EXPIRATION_TIME = 60 * 24;

	static Logger logger = LoggerFactory.getLogger(UserLoginService.class);

	public LoginResponseDto getUserLogin(LoginRequestDto dto)
			throws UserException, NoSuchAlgorithmException, IOException, ValidationExceptions {
		
		
		if (dto.getPassword().isEmpty()) {
			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantDetails user = repo.findByMerchantEMail(dto.getUserNameOrEmail());
		if (user == null) {

			throw new ValidationExceptions(EMAIL_ID_NOT_FOUND, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);

		}
		
		if(user.getInitialPwdChange()==null) {
			throw new ValidationExceptions(INITIAL_PASSWORD_CHANGE_REQUEST,
					FormValidationExceptionEnums.INITIAL_PASSWORD_CHANGE_REQUIRED);
		}
		
		if (user.getUserStatus().equals(UserStatus.BLOCKED)) {
			throw new ValidationExceptions(USER_STATUS_BLOCKED, FormValidationExceptionEnums.USER_STATUS_BLOCKED);

		}
		if (user.getUserStatus().equals(UserStatus.DELETE)) {

			throw new ValidationExceptions(USER_STATUS_REMOVED, FormValidationExceptionEnums.USER_STATUS_REMOVED);
		}
		
		
		
		logger.info("Input Password :: " + dto.getPassword());
		if (user.getPassword() != null) {
			if (dto.getPassword() == null) {
				throw new ValidationExceptions(PASSWORD_CANT_BE_BLANK,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);

			} else if (!user.getPassword().equals(Encryption.getEncryptedPassword(dto.getPassword()))) {
				throw new ValidationExceptions(PASSWORD_MISMATCH,
						FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
			}
		} else {
			throw new ValidationExceptions(PASSWORD_MISMATCH, FormValidationExceptionEnums.PASSWORD_VALIDATION_ERROR);
		}

		if (user.getUserSession() != null) {
			user.getUserSession().setSessionStatus(0);
		}

		UserSession session = new UserSession();
		if (user.getUserSession() != null) {
			session = user.getUserSession();
		}
		session.setSessionStatus(1);
		session.setUserAgent(dto.getUserAgent());
		session.setIpAddress(dto.getIpAddress());
		session.setUser(user);
		String hash = Encryption.getSHA256Hash(UUID.randomUUID().toString() + user.getMerchantEMail());
		session.setSessionToken(hash);
		ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		session.setSessionExpiryDate(date);
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		session.setIdealSessionExpiry(idealDate);
		user.setUserSession(session);
		repo.save(user);

		LoginResponseDto loginResponseDto = new LoginResponseDto();

		loginResponseDto.setUuid(user.getUuid());
		loginResponseDto.setEmail(user.getMerchantEMail());
		loginResponseDto.setPhoneNumber(user.getPhoneNumber());
		loginResponseDto.setSessionStatus(user.getUserSession().getSessionStatus());
		loginResponseDto.setSessionToken(user.getUserSession().getSessionToken());
		loginResponseDto.setSessionExpiryDate(user.getUserSession().getSessionExpiryDate());

		return loginResponseDto;
	}

	public void userLogout(LogoutRequestDto dto) throws UserException, ValidationExceptions {

		MerchantDetails user = repo.findByuuid(dto.getUuid());

		if (!(user.getUserSession().getSessionToken()).equals(dto.getSessionToken())) {
			logger.error("Session Token does not Exist");

			throw new ValidationExceptions(SESSION_NOT_FOUND, FormValidationExceptionEnums.SESSION_NOT_FOUND);
		}
		user.getUserSession().setSessionStatus(0);
		repo.save(user);
	}
	
	public void initiatlPasswordChange(String userNameOrEmailId, String password) throws ValidationExceptions {
		
		MerchantDetails user = repo.findByMerchantEMail(userNameOrEmailId);

		if (user==null) {
			logger.error("User ot found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPassword(password));
		user.setInitialPwdChange("Y");
		user.setUserStatus(UserStatus.ACTIVE.toString());
		
		repo.save(user);
		
	}

	public void passwordChange(String userNameOrEmailId, String password) throws ValidationExceptions {
		MerchantDetails user = repo.findByMerchantEMail(userNameOrEmailId);

		if (user==null) {
			logger.error("User ot found ..." + userNameOrEmailId);

			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		user.setPassword(Encryption.getEncryptedPassword(password));
		
		
		repo.save(user);
		
	}
	
	

}
