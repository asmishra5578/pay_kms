package com.asktech.pgateway.service.merchantApi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.TransactionDetailsRepository;

@Service
public class UpdateTransactionStatus {
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	static Logger logger = LoggerFactory.getLogger(UpdateTransactionStatus.class);

	@Async
	public void updateTransaction(String orderId, String status, String msg, String merchantId) {
		logger.info("Update transaction status " + orderId + "|" + status + "|" + msg + "|" + merchantId);
		List<TransactionDetails> tr = transactionDetailsRepository.findAllByMerchantOrderIdAndMerchantId(orderId,
				merchantId);

		for (TransactionDetails t : tr) {
			t.setStatus(status);
			if (msg != null) {
				t.setTxtMsg(msg);
			}
			transactionDetailsRepository.save(t);
		}

	}


	@Async
	public void updateTransactionWithOrderId() {


	}

}
