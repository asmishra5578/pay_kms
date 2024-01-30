package com.asktech.pgateway.constants;

public interface ErrorValues {
	String SQL_ERROR = "SQL Error:";
	String SQL_DUPLICATE_ID = "SQL Exception, Duplicate Order ID";
	String SAVE_SUCCESS = "Saved Success";
	String ALL_FIELDS_MANDATORY = "All Fields Mandatory";
	String FORM_VALIDATION_FAILED = "Form Validation Failed:";
	String SIGNATURE_MISMATCH = "Signature Mismatch";
	String JSON_PARSE_ISSUE_MERCHANT_REQUEST = "Merchant Request is not in Proper Format";
	String INPUT_BLANK_VALUE = "Input should not content blank value";
	String MERCHANT_EXITS = "Provided Merchant Name already Present in System";
	String MERCHANT_NOT_FOUND = "Merchant not found / Mapped with provided AppID - ";
	String MERCHNT_NOT_EXISTIS = "Merchant not found / Mapped with provided emailid";
	String MERCHANT_PG_SERVICE_NO_MAPPED = "Merchant not mapped with provided PG Service ";
	String MERCHANT_PG_CONFIG_NOT_FOUND = "Merchant mapped with PG Service but not mapped with PG Details like (Appid and Secret Keys).";
	String MERCHANT_PG_APP_ID_NOT_FOUND = "Merchant PG APP id not found in request .";
	String MERCHANT_PG_SECRET_NOT_FOUND = "Merchant PG Secret id not found in request .";
	String MERCHANT_PG_NAME_NOT_FOUND = "Merchant Name id not found in request .";
	String PG_NOT_PRESENT = "Provided PG name not found in system.";
	String BANK_DETAILS_NOT_FOUND = "Merchnat Bank Details not found in system.";
	
	String EMAIL_VALIDATION_FAILED = "Input EMAIL is not proper as per Email Validator. Proceed with proper Email id.";
	String NAME_VALIDATION_FAILED = "Input Customer Name / Name validation failed .";
	String PHONE_VAIDATION_FILED = "Input Mobile not format is not valid";
	String EMAIL_ID_NOT_FOUND = "Input Email Id not Exists in System , please login with proper EMAIL id / Register as a merchant.";
	String USER_STATUS_BLOCKED = "User Status is Blocked in system , Contact Admistrator.";
	String USER_STATUS_REMOVED = "User is Removed from system , Contact Admistrator.";
	String PASSWORD_CANT_BE_BLANK = "User Input password can't be NULL";
	String PASSWORD_MISMATCH = "Input password is not correct as per system Information.";
	String SESSION_NOT_FOUND = "User Session not active / found .";
	String USER_NOT_EXISTS = "User does not exist";
	String INITIAL_PASSWORD_CHANGE_REQUEST = "Initial password change required , please check the password . ";
	
	String MERCHANT_BANK_DETAIL_PRESENT = "Merchant Bank details already exists , client can be update the bank details from Update option.";
	String MERCHANT_SERVICE_TYPE = "Service Type blank will not acceptable in system.";
	String MERCHANT_COMMISSION_TYPE = "CommissionType blank will not acceptable in system.";
	String MERCHANT_COMM_AMOUNT = "Commission strucure Fixed / Floating amount can't be blank .";
	String MERCHANT_COMMISSION_EXISTS = "Comission structure is avalable for the merchant with service ID and PG Details..";
	String DUPLICATE_ORDERID = "Order Id Already used";
	String INVALID_COOKIE = "Invalid Cookie Exception";
	String UNKNOWN_OPTION = "Invalid Option";
}
