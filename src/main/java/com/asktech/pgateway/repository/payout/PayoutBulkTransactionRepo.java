package com.asktech.pgateway.repository.payout;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.payout.PayoutBulkTransaction;

public interface PayoutBulkTransactionRepo extends JpaRepository<PayoutBulkTransaction, String>{

}
