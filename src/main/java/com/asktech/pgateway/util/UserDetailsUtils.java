package com.asktech.pgateway.util;

import com.asktech.pgateway.customInterface.IUserDetails;
import com.asktech.pgateway.dto.report.UserDetailsReport;

public  class UserDetailsUtils {

	public static UserDetailsReport updateUserDetails(IUserDetails iUserDetails) {
		
		UserDetailsReport userDetailsReport = new UserDetailsReport();
		
		userDetailsReport.setCustomerName(iUserDetails.getCustomerName());
		userDetailsReport.setEmailId(iUserDetails.getEmailId());
		userDetailsReport.setPhoneNumber(iUserDetails.getPhoneNumber());
		userDetailsReport.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveData(iUserDetails.getCardNumber())));
		userDetailsReport.setPaymentCode(SecurityUtils.decryptSaveData(iUserDetails.getPaymentCode()));
		userDetailsReport.setVpaUpi(Utility.maskUpiCode(SecurityUtils.decryptSaveData(iUserDetails.getVpaUpi())));
		return userDetailsReport;
	}
}
