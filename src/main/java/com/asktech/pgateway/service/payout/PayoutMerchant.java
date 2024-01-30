package com.asktech.pgateway.service.payout;




import com.asktech.pgateway.constants.payout.Payout;
import com.asktech.pgateway.dto.payout.beneficiary.VerifyBankAccount;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferMerReq;
import com.asktech.pgateway.dto.payout.merchant.AccountTransferUPIMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransactionReportMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransactionRequestFilterMerReq;
import com.asktech.pgateway.dto.payout.merchant.TransferStatusReq;
import com.asktech.pgateway.dto.payout.merchant.WalletTransferMerReq;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.Unirest;



@Service
public class PayoutMerchant implements Payout{

	static Logger logger = LoggerFactory.getLogger(PayoutMerchant.class);
	@Value("${apiPayoutEndPoint.payoutUrl}")
	String payoutUrl;

	public String WalletTransfer(WalletTransferMerReq dto, String merchantid) {
		// dto.setOrderid();
		logger.info("Inside Wallet transfer Service WalletTransfer()");
		String res = Unirest.post(payoutUrl + "walletTransfer/" + merchantid).header("Content-Type", "application/json")
				.body(dto).asString().getBody();
		logger.info("Response from Payout :: "+ res);
		return res;
	}

	public String AccountTransfer(AccountTransferMerReq dto, String merchantid) {
			String res = Unirest.post(payoutUrl + "accountTransfer/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}

	public String AccountTransferUPI(AccountTransferUPIMerReq dto, String merchantid) {
		String res = Unirest.post(payoutUrl + "accountTransferUPI/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}
	public String BalanceCheck(String merchantid) {
		logger.info("Submit URL :: "+payoutUrl + "balanceCheck/" + merchantid);
		String res = Unirest.get(payoutUrl + "balanceCheck/" + merchantid).header("Content-Type", "application/json")
				.asString().getBody();
		return res;

	}

	public String TransactionReport(String merchantid, TransactionReportMerReq dto) {
		String res = Unirest.post(payoutUrl + "transactionReport/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}
	
	public String WalletReport(String merchantid, TransactionReportMerReq dto) {
		String res = Unirest.post(payoutUrl + "walletReport/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}

	public String TransactionStatus(TransferStatusReq dto, String merchantid) {
		String res = Unirest.post(payoutUrl + "transactionStatus/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}
	
	public String verifyAccount(VerifyBankAccount dto) {
		String res = Unirest.post(payoutUrl + "accountVerify")
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;
	}
	public String TransactionReportFilter(String merchantid, TransactionRequestFilterMerReq dto) {
		String res = Unirest.post(payoutUrl + "transactionReportWithFilter/" + merchantid)
				.header("Content-Type", "application/json").body(dto).asString().getBody();
		return res;

	}
	
	public String wallet7days(String merchantid) {
		String res = Unirest.get(payoutUrl + "walletReport7days/" + merchantid)
				.header("Content-Type", "application/json").asString().getBody();
		return res;

	}
	
//	public String transactionFilter(TransactionFilterReq dto) {
//		String res = Unirest.post(payoutUrl + "transactionFilter")
//				.header("Content-Type", "application/json").body(dto).asString().getBody();
//		return res;
//	}
//	
//	public String walletFilter(WalletFilterReq dto) {
//		String res = Unirest.post(payoutUrl + "walletFilter")
//				.header("Content-Type", "application/json").body(dto).asString().getBody();
//		return res;
//	}
	
}
