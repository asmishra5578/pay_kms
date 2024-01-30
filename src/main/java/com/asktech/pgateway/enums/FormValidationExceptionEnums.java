package com.asktech.pgateway.enums;

public enum FormValidationExceptionEnums {
	CAPTCHA_ERROR,
	INPUT_VALIDATION_ERROR,
	EMAIL_SEND_ERROR,
	FATAL_EXCEPTION, 
	ALL_FIELDS_MANDATORY, 
	FIELED_NOT_FOUND, 
	UNICONTRAINT_VIOLATION, 
	SQLERROR_EXCEPTION,
	SIGNATURE_VERIFICATION_ERROR, 
	SIGNATURE_VERIFICATION_FAILED , 
	JSON_PARSE_EXCEPTION,	
	MERCHANT_ALREADY_EXISTS,
	PLEASE_FILL_THE_MANDATORY_FIELDS,
	MERCHANT_EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM,
	MERCHANT_PHONE_NUMBER_ALREADY_EXISTS_IN_SYSTEM,
	MERCHANT_NOT_FOUND,
	PG_SERVICE_NOT_MAPPED_WITH_MERCHANT,
	EMAIL_ID_NOT_FOUND,
	USER_STATUS_BLOCKED,
	USER_STATUS_REMOVED,
	PASSWORD_VALIDATION_ERROR,
	INITIAL_PASSWORD_CHANGE_REQUIRED,
	SESSION_NOT_FOUND,
	JWT_EXPIRED, 
	JWT_MISSING, 
	JWT_SIGNATURE_MISSING, 
	JWT_FORMATE_INVALID, 
	JWT_UNSUPPORTED, 
	SESSION_EXPIRED, 
	IDEAL_SESSION_EXPIRED, 
	JWT_NOT_VALID, 
	USER_NOT_FOUND,
	REST_TEMPLATE_NOT_CALL, 
	JWT_ILLEGAL_ARGUMENT, 
	SESSION_DEAD,
	MERCHANT_BANK_DETAILS_EXISTS,
	MERCHANT_KYC_DETAILS_PRESENT,
	MERCHANT_KYC_DETAIL_NOT_FOUND,
	KYC_STATUS,
	TICKET_STATUS,
	REFUND_STATUS,
	OTP_STATUS_VALIDATION_ERROR,
	MERCHANT_PG_APP_ID_NOT_FOUND,
	FORM_VALIDATION_FILED, MERCHANT_PG_SECRET_NOT_FOUND, MERCHANT_PG_NAME_NOT_FOUND, DUPLICATE_COMMISSON_FOR_MERCHANT, PG_VLIDATION_ERROR, 
	MERCHANT_BANK_DETILS_NOT_FOUND, EMAIL_ALREADY_EXISTS, USER_ROLE_ISSUE, PG_ALREADYCREATED, MERCHANT_PG_ASSOCIATION_EXISTS, PG_NOT_CREATED, PG_SERVICE_PRESENT, 
	PG_NOT_ACTIVE, MERCHANT_PG_ASSOCIATION_NON_EXISTS, PG_SERVICE_ASSOCIATION_NOT_FOUND, USER_STATUS, PG_SERVICES_DEFINED,MERCHANT_PG_SERVICE_NOT_FOUND,
	DUPLICATE_ORDERID, INVALID_COOKIE_EXCEPTION, UNKNOWN_OPTION, DATE_FORMAT_VALIDATION, PG_SERVICE_NOT_FOUND, MERCHANT_PG_SERVICE_NOT_ASSOCIATED, DATA_INVALID, 
	BANK_ERROR, CREATE_REQUEST_FAILED, DECRYPTION_ERROR, PASSWORD_VALIDATION, OTP_MISMATCH, OTP_EXPIRED, OTP_INVALID, FORM_VALIDATION_FAILED, COMPLAINT_TYPE_EXISTS, COMPLAINT_TYPE_NOT_EXISTS, 
	COMPLAINT_TYPE_SUBTYPE_EXISTS, COMPLAINT_TYPE_SUBTYPE_NOT_EXISTS, COMPLAINT_ID_BLANK, COMPLAINT_NOT_FOUND, COMPLAINT_ALREADY_CLOSED, COMPLAINT_NOT_CLOSED, COMPLAINT_TYPE_SUB_TYPE_STATUS, 
	EXCEPTION_IN_SMS_SENDING, MERCHANT_SERVICE_PRESENT_AS_ACTIVE, EMAIL_ID_ALREADY_EXISTS_IN_SYSTEM, CHECKSUM_MISMATCH,INVALID_IP, MERCHANT_ASSO_WITH_BUSI_ASSO, BUSINESS_ASSOCIATED_NOT_FOUND, 
	COMMISSION_WITH_MERCHANT_ALREADY_PRESENT, COMMISSION_UPDATE, COMMISSION_NOT_FOUND, TRANSACTION_NOT_FOUND, AMOUNT_NOT_MATCHED_WITH_EDITED_COMM, MERCHANT_ORDER_ID_VALIDATION, REFUND_DETAILS_EXIST, REFUND_INITIATE_FAILED, 
	REFUND_UPDATE_FAILED, PG_DEFAULT_SERVICE_SCOPE, AMOUNT_VALIDATION_ERROR, SMS_SEND_ERROR, MERCHANT_ORDER_ID_NOT_FOUND, INFORMATION_NOT_FOUND, TRY_AFTER_5_MINUTES,INVALID_FILE_FORMAT,INVALID_FILE_SIZE, FILE_NOT_FOUND,
	DATE_PARAMETER_IS_MANDATORY,DATE_FORMAT,
	E0200,
	E0201, 
	E0202, E0203, E0204, E0205, E0206, E0207, E0208, E0209, E0210,
	
	E0500, ORDER_ID_EXITS_WITH_LINK, ORDER_ID_NOT_FOUND, EXITS_RESEND_EMAIL_LINK_COUNTER, RESEND_EMAIL_NOT_POSSIBLE, RESEND_SMS_NOT_POSSIBLE, EXITS_RESEND_SMS_LINK_COUNTER, FILE_STORAGE_EXCEPTION,
	USER_NOT_EXISTS, CAPTCHA_VALIDATION_ERROR,UPLOAD_FORMATE_ERROR,FILE_TYPE_ERROR,FILE_NAME_NOT_FOUND
	;

	FormValidationExceptionEnums() {

	}
}
