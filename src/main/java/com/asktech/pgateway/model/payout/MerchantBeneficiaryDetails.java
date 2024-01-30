package com.asktech.pgateway.model.payout;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.asktech.pgateway.model.AbstractTimeStampAndId;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name="merchant_beneficiary_details")
public class MerchantBeneficiaryDetails extends AbstractTimeStampAndId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="merchant_id")
	private String merchantId;
	@Column(name="disbrushment_walletid")
	private String disBruseMentWalletId;
	@Column(name="beneficiary_name")
	private String beneficiaryName;
	@Column(name="beneficiary_account_id")
	private String beneficiaryAccountId;
	@Column(name="beneficiary_ifsc_code")
	private String beneficiaryIFSCCode;
	@Column(name="beneficiary_micr_code")
	private String beneficiaryMICRCode;
	@Column(name="beneficiary_bank_name")
	private String beneficiaryBankName;
	@Column(name="account_validation_flag")
	private String accountValidationFlag;
	private String createdBy;
	private String status;
	private String modifiedBy;
	@Column(name="request_Data" , columnDefinition = "TEXT")
	private String requestData;
	@Column(name="update_request_Data" , columnDefinition = "TEXT")
	private String updateRequestData;
	private String orderId;
	private String merchantOrderId;
	
}
