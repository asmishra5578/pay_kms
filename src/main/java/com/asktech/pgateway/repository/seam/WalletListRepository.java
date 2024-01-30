package com.asktech.pgateway.repository.seam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.seam.WalletList;

public interface WalletListRepository extends JpaRepository<WalletList, String>{
	
	List<WalletList> findAllByPgnameAndStatus(String pgname, String status);
	WalletList findByPaymentcode(String paymentcode);
}
