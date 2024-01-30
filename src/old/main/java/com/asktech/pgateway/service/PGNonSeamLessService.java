package com.asktech.pgateway.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.exception.SessionExpiredException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.seam.BankList;
import com.asktech.pgateway.model.seam.BankListCashfree;
import com.asktech.pgateway.model.seam.CustomerRequest;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.seam.BankListCashfreeRepository;
import com.asktech.pgateway.repository.seam.BankListRepository;
import com.asktech.pgateway.repository.seam.CustomerRequestRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.FormValidations;
import com.asktech.pgateway.util.GeneralUtils;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.asktech.pgateway.constants.Fields;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;

@Service
public class PGNonSeamLessService implements ErrorValues, Fields {
	long EXPIRATION_TIME = 60 * 24;
	
	int totalyears = 10; 
	
	@Value("${idealSessionTimeOut}")
	long IDEAL_EXPIRATION_TIME;
	
	@Autowired
	CustomerRequestRepository customerRequestRepository;
	
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	
	@Autowired
	BankListRepository bankList;
	
	@Autowired
	BankListCashfreeRepository bankListCashfree;
	
	static Logger logger = LoggerFactory.getLogger(PGNonSeamLessService.class);
	
	
	public String getRequestProcess(MultiValueMap<String, String> formData, Model model) throws ValidationExceptions, JsonProcessingException {
		String sessionToken  = "";
		
		if (FormValidations.FormFieldValidation(formData)) {
			String useruuid = UUID.randomUUID().toString();
			String userName = formData.get(Fields.CUSTOMERNAME).get(0);
			String userPhone = formData.get(Fields.CUSTOMERPHONE).get(0);
			String userEmail = formData.get(Fields.CUSTOMEREMAIL).get(0);
			Integer amount = Integer.valueOf(formData.get(Fields.ORDERAMOUNT).get(0));
			String returnUrl = formData.get(Fields.NOTIFYURL).get(0);
			String orderId = formData.get(Fields.ORDERID).get(0);
			String customerId = formData.get(Fields.CUSTOMERID).get(0);
			String orderCurrency = formData.get(Fields.ORDERCURRENCY).get(0);	
			String orderNote  = "";
			if(formData.containsKey(Fields.ORDERNOTE)) {
				orderNote = formData.get(Fields.ORDERNOTE).get(0);
			}
			MerchantDetails merhantDetails = getMerchantFromAppId(formData.get(CUSTOMERID).get(0));
			
			if (!FormValidations.isVerifySignature(formData,
					Encryption.decryptCardNumberOrExpOrCvv(merhantDetails.getSecretId()))) {

				logger.info("Going to validate the Signature");
				throw new ValidationExceptions(SIGNATURE_MISMATCH,
						FormValidationExceptionEnums.SIGNATURE_VERIFICATION_FAILED);
			}
			String sessionStatus = "Valid";

			sessionToken = Encryption.getSHA256Hash(
					useruuid + "|" + userName + "|" + userPhone + "|" + userEmail + "|" + amount.toString());
			ZonedDateTime expirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MINUTES);
			Date sessionExpiryDate = Date.from(expirationTime.toInstant());

			ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
					ChronoUnit.MINUTES);
			Date idealSessionExpiry = Date.from(idealExpirationTime.toInstant());

			CustomerRequest customerRequest = new CustomerRequest();
			customerRequest.setUuid(useruuid);
			customerRequest.setUserName(userName);
			customerRequest.setUserPhone(userPhone);
			customerRequest.setUserEmail(userEmail);
			customerRequest.setAmount(amount);
			customerRequest.setSessionStatus(sessionStatus);
			customerRequest.setSessionExpiryDate(sessionExpiryDate);
			customerRequest.setIdealSessionExpiry(idealSessionExpiry);
			customerRequest.setSessionToken(sessionToken);
			customerRequest.setReturnUrl(returnUrl);
			customerRequest.setOrderId(orderId);
			customerRequest.setCustomerId(customerId);
			customerRequest.setOrderCurrency(orderCurrency);
			customerRequest.setOrderNote(orderNote);
			try {
			customerRequestRepository.save(customerRequest);
			}catch(Exception e) {
				if(e.getMessage().contains("ConstraintViolationException")) {
					logger.info(e.getMessage());
					throw new ValidationExceptions(DUPLICATE_ORDERID,
							FormValidationExceptionEnums.DUPLICATE_ORDERID);
				}else {
					logger.info(e.getMessage());
				} 
			}
			List<BankList> bankdetails = bankList.findAll();
			model.addAttribute("banklist", bankdetails);
			
			int year = Calendar.getInstance().get(Calendar.YEAR);
			
			ArrayList<String> yearlist = new ArrayList<String>();
			
			for(int i=0; i <=  totalyears; i++) {
				String ayear = String.valueOf(year); 				
				yearlist.add(ayear);
				year = year + 1;
			}

			model.addAttribute("yearlist", yearlist);
			model.addAttribute("amount", orderCurrency+" "+formData.get(ORDERAMOUNT).get(0));
			model.addAttribute("name", formData.get(CUSTOMERNAME).get(0));
		}
		return sessionToken;
	}
	
	public String getSubmitPayment(MultiValueMap<String, String> formData, Model model, String sessionToken) throws SessionExpiredException, ValidationExceptions {
		CustomerRequest customerRequest = customerRequestRepository.findAllBySessionToken(sessionToken);
		
		checkSession(customerRequest);		
		
		String dt = "";
		try {
			dt = SecurityUtils.decryptRSABase64(formData.get("data").get(0),"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIIZV0u6eHUqPS0EjDtdBh3vR/DLOvgxQQ5TBUR8ZerYFjVu16rSACuLd3zTxae5U7fqUE1nYL0v/z5+C82Oq96FYeA32kRpcxzXDXaXemMUHs5608ITrxPTyoO2wKEwlkN1H/1qomTUFGiZOEq2n2Bo1iC9yIKtAkbmznsliAxFAgMBAAECgYB+2/j8lgLZtOtcV1/qjuXlEKtkyLdixpx6PEgZpPe4jSbyyXexUP7rdx53cQT+bL+OygOtxo1VTVUl+cDGm0VIzIUHIAH9iW64r7K0yXqZqG3LpEtFLXA1/Dbd+Ul8UCWwvsjmnNmsC68Jt0HzX9wAA8MZKRUq89Yj9JLp6RZYYQJBAPg2DzkAby6gp7vXS1Xt90gO4+rLCw3a5/JBRTLokH9u9CZjoSme4IGJ3vSQ0wPLAm7EKQCF069oZkxMCfW3LP0CQQCGLnUt2B2/avq4DQORgnlI9zw/Xd0KVXxUqeFAgJCejtT2zHVrmY1QeUhPG8gdW7JMiDpC5cT9w9QWC6HAOKLpAkAYogTQu2JNVlRPKAap+HvaAuBLpOrr7RWnzSJ48uukOfaw+KI95y6QrIYb72OBtNwA8ia/joh7l/jPCZzTbeJhAkBCWTqL/q9G9Ykf9R9slg2O7OGXm7wu3fJqks3U7T2ViZ74okT1faoIvs/ofh5Hlg3mFf5pEeCEco6uj/XdbKPxAkAUYuPrBcH9P6zUK8R29+cP61CAuOoAhzqY1BgsN37Aa2i1gQvluGxt30MHTaDUdxrf3+cVPBlCWVi95a80X82l");
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(dt);
		String[] details = dt.split(Pattern.quote("|"));
		String page = "";
		for(String detail : details) {
			System.out.println(detail);
		}
		switch (details[0]) {
		case "bank":
			page = createBankProcessor(details);
			break;
		case "card":
			break;
		case "upi":
			break;
		case "wallet":
			break;
		default:
			throw new ValidationExceptions(UNKNOWN_OPTION,
					FormValidationExceptionEnums.UNKNOWN_OPTION);
		}
			
		updateSession(customerRequest);
		return sessionToken;
	}

	public String setSession() {
		String trxid = GeneralUtils.getTrxId().toUpperCase();
		return trxid;
	}
	public MerchantDetails getMerchantFromAppId(String appId) throws JsonProcessingException, ValidationExceptions {

		MerchantDetails merhantDetails = merchantDetailsRepository.findByAppID(Encryption.encryptCardNumberOrExpOrCvv(appId));
		logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merhantDetails));

		if (merhantDetails == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND + appId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		return merhantDetails;
	}
	
	private String createBankProcessor(String[] details) {
		String bankcode = details[2];
		String custname = details[1];
		List<BankListCashfree> bls = bankListCashfree.findAllByBankcodemap(bankcode);
		
		return "bankpage";
	}
	
	private void checkSession(CustomerRequest customerRequest) throws SessionExpiredException {
		Calendar cal = Calendar.getInstance();
		Date dat = cal.getTime();
		if(customerRequest.getSessionExpiryDate().before(dat)) {
			throw new SessionExpiredException(
					"Session Expired",
					FormValidationExceptionEnums.SESSION_EXPIRED);
		}
		if(customerRequest.getIdealSessionExpiry().before(dat)) {
			throw new SessionExpiredException(
					"Session Expired",
					FormValidationExceptionEnums.IDEAL_SESSION_EXPIRED);
		}
	}
	
	private void updateSession(CustomerRequest customerRequest) {
		ZonedDateTime idealExpirationTime = ZonedDateTime.now(ZoneOffset.UTC).plus(IDEAL_EXPIRATION_TIME,
				ChronoUnit.MINUTES);
		Date idealDate = Date.from(idealExpirationTime.toInstant());
		customerRequest.setIdealSessionExpiry(idealDate);
		customerRequestRepository.save(customerRequest);
		
	}
}
