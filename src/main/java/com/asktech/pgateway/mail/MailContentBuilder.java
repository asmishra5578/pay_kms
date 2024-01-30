package com.asktech.pgateway.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.UserDetails;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.service.PGGatewayAdminService;

@Service
public class MailContentBuilder {

	private TemplateEngine templateEngine;
	

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminService.class);

	@Autowired
	public MailContentBuilder(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	
	public String forgetPasswordOTP(String mailText) {
		Context context = new Context();		
		context.setVariable("mailText", mailText);
		return templateEngine.process("/emailCommunication/forgetPassowrd", context);
	}

	public String createMerchant(MerchantDetails merchantDetails) {
		
		Context context = new Context();		
		context.setVariable("merchantName", merchantDetails.getMerchantName());
		context.setVariable("merchantEmail", merchantDetails.getMerchantEMail());
		context.setVariable("password", Encryption.getDecryptedPassword(merchantDetails.getPassword()));
		return templateEngine.process("/emailCommunication/welcome", context);
	}


	public String createComplaint(String mailText) {
		Context context = new Context();		
		context.setVariable("mailText", mailText);
		return templateEngine.process("/emailCommunication/createComplaint", context);
	}
	
	public String createMerchantTransaction(UserDetails userDetails , MerchantDetails merchantDetails , TransactionDetails transactionDetails) {
		
		Context context = new Context();		
		context.setVariable("merchantName", merchantDetails.getMerchantName());
		context.setVariable("orderId", transactionDetails.getMerchantOrderId());
		context.setVariable("transactionId", transactionDetails.getOrderID());
		context.setVariable("transactionTime", transactionDetails.getTxtPGTime());
		context.setVariable("amount", transactionDetails.getAmount());
		context.setVariable("custName",userDetails.getCustomerName());
		context.setVariable("custEmail",userDetails.getEmailId());
		context.setVariable("custPhone",userDetails.getPhoneNumber());
		context.setVariable("paymentMode",transactionDetails.getPaymentMode());
		return templateEngine.process("/emailCommunication/txn_status_Merchant", context);
	}


	public String createCustomerTransaction(UserDetails userDetails, TransactionDetails transactionDetails) {
		Context context = new Context();		
		
		context.setVariable("orderId", transactionDetails.getMerchantOrderId());
		context.setVariable("transactionId", transactionDetails.getOrderID());
		context.setVariable("transactionTime", transactionDetails.getTxtPGTime());
		context.setVariable("amount", transactionDetails.getAmount());
		context.setVariable("custName",userDetails.getCustomerName());
		context.setVariable("custEmail",userDetails.getEmailId());
		context.setVariable("custPhone",userDetails.getPhoneNumber());
		context.setVariable("paymentMode",transactionDetails.getPaymentMode());
		return templateEngine.process("/emailCommunication/txn_status_customer", context);
	}


	public String createCustomerMail(String link) {
		Context context = new Context();		
		
		context.setVariable("mailText", link);
		
		return templateEngine.process("emailCommunication/customerpaymentLink", context);
	}


	public String createLoginOtp(String userName, String otp) {
		Context context = new Context();				
		context.setVariable("otp", otp);		
		return templateEngine.process("emailCommunication/custLoginOtp", context);
	}
	
}
