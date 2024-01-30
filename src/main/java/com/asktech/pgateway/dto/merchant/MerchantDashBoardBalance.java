package com.asktech.pgateway.dto.merchant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Immutable
@Table(name = "`merchant_dashboard_details`")
public class MerchantDashBoardBalance {

	@Id
	private String id;
	private String merchantId;
	private String settlementStatus;
	private Integer amount;
}
