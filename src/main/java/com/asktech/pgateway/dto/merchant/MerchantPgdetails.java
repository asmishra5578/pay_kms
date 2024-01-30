package com.asktech.pgateway.dto.merchant;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPgdetails {
	private String pgname;
	private String pgstatus;
	private List<MerchantServiceDetails> merchantservicedetails;
}
