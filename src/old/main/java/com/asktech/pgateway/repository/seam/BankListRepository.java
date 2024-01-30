package com.asktech.pgateway.repository.seam;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.seam.BankList;

public interface BankListRepository extends JpaRepository<BankList, String>{
	List<BankList> findAllByBankcode(String code);
}
