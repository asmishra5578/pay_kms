package com.asktech.pgateway.dto.nsdl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetBanking {

	@JsonProperty("signature")
	private String signature;
	@JsonProperty("f_code")
	private String f_code;
	@JsonProperty("amt")
	private String amt;
	@JsonProperty("bank_txn")
	private String bank_txn;
	@JsonProperty("mer_txn")
	private String mer_txn;
	@JsonProperty("mmp_txn")
	private String mmp_txn;
	@JsonProperty("prod")
	private String prod;
	@JsonProperty("discriminator")
	private String discriminator;
}
