package com.asktech.pgateway.repository.payout;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asktech.pgateway.model.payout.PayoutApiUserDetails;

public interface PayoutApiUserDetailsRepo extends JpaRepository<PayoutApiUserDetails, String>{
  List<PayoutApiUserDetails> findAllByMerchantIdAndTokenAndWhitelistedip(String merchantid, String token, String whitelistedip);

  PayoutApiUserDetails findAllByMerchantIdAndToken(String merchantid, String secret);
}
