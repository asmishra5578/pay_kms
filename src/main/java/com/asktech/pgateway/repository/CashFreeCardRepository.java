package com.asktech.pgateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.CashfreeCards;

public interface CashFreeCardRepository extends JpaRepository<CashfreeCards, String> {

}
