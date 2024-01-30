package com.asktech.pgateway.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.customInterface.IMerchantDetailsReport;
import com.asktech.pgateway.customInterface.ISettlementBalanceReport;
import com.asktech.pgateway.customInterface.IUserDetails;
import com.asktech.pgateway.customInterface.payout.IMerchantWalletDetails;
import com.asktech.pgateway.dto.merchant.DashBoardDetails;
import com.asktech.pgateway.dto.merchant.MerchantRefundDto;
import com.asktech.pgateway.dto.merchant.MerchantResponse;
import com.asktech.pgateway.dto.merchant.MerchantSettlement;
import com.asktech.pgateway.dto.merchant.SettlementDetailsDto;
import com.asktech.pgateway.dto.merchant.TransactionDetailsDto;
import com.asktech.pgateway.dto.payout.beneficiary.AssociateBankDetails;
import com.asktech.pgateway.dto.payout.beneficiary.CreateBeneficiaryRequest;
import com.asktech.pgateway.dto.payout.beneficiary.DeleteBeneficiaryRequest;
import com.asktech.pgateway.dto.payout.beneficiary.VerifyBankAccount;
import com.asktech.pgateway.dto.payout.beneficiary.VerifyBankAccountResponse;
import com.asktech.pgateway.dto.report.UserDetailsReport;
import com.asktech.pgateway.dto.utility.SuccessResponseDto;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.OtpStatus;
import com.asktech.pgateway.enums.SuccessCode;
import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.exception.UserException;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.mail.MailIntegration;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.model.MerchantBankDetails;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.MerchantRequest4Customer;
import com.asktech.pgateway.model.RefundDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.payout.MerchantBeneficiaryDetails;
import com.asktech.pgateway.repository.CommissionStructureRepository;
import com.asktech.pgateway.repository.MerchantBalanceSheetRepository;
import com.asktech.pgateway.repository.MerchantBankDetailsRepository;
import com.asktech.pgateway.repository.MerchantDashBoardBalanceRepository;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGServicesRepository;
import com.asktech.pgateway.repository.MerchantRequest4CustomerRepository;
import com.asktech.pgateway.repository.RefundDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.repository.UserDetailsRepository;
import com.asktech.pgateway.repository.payout.MerchantBeneficiaryDetailsRepo;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.service.payout.PayoutMerchant;
import com.asktech.pgateway.util.EncryptSignature;
import com.asktech.pgateway.util.GeneralUtils;
import com.asktech.pgateway.util.SecurityUtils;
import com.asktech.pgateway.util.SmsCallTemplate;
import com.asktech.pgateway.util.UserDetailsUtils;
import com.asktech.pgateway.util.Utility;
import com.asktech.pgateway.util.ValidationUtils;
import com.asktech.pgateway.util.Validator;
import com.asktech.pgateway.util.payout.PayoutWalletUtilityServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentMerchantService implements ErrorValues {

	@Autowired
	RefundDetailsRepository refundDetailsRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantBalanceSheetRepository merchantBalanceSheetRepository;
	@Autowired
	MerchantDashBoardBalanceRepository merchantDashBoardBalanceRepository;
	@Autowired
	MerchantBankDetailsRepository merchantBankDetailsRepository;
	@Autowired
	MerchantPGDetailsRepository merchantPGDetailsRepository;
	@Autowired
	MerchantPGServicesRepository merchantPGServicesRepository;
	@Autowired
	CommissionStructureRepository commissionStructureRepository;
	@Autowired
	UserDetailsRepository userDetailsRepository;
	@Autowired
	MerchantRequest4CustomerRepository merchantRequest4CustomerRepository;
	@Autowired
	MailIntegration sendMail;
	@Autowired
	MerchantBeneficiaryDetailsRepo merchantBeneficiaryDetailsRepo;
	@Autowired
	PayoutWalletUtilityServices payoutWalletUtilityServices;
	@Autowired
	PayoutMerchant payoutMerchant;
	@Autowired
	SmsCallTemplate smsCallTemplate;

	ObjectMapper mapper = new ObjectMapper();

	static Logger logger = LoggerFactory.getLogger(PaymentMerchantService.class);
	@Value("${apiEndPoint}")
	String apiEndPoint;
	@Value("${apiCustomerNotifyUrl}")
	String apiCustomerNotifyUrl;
	@Value("${apiCustomerExclude}")
	String apiCustomerExclude;
	@Value("${resendEmailCounter}")
	int resendEmailCounter;
	@Value("${resendSmsCounter}")
	int resendSmsCounter;
	@Value("${smsSenderId}")
	String smsSenderId;

	public MerchantResponse merchantView(String uuid) throws ValidationExceptions {
		logger.info("merchantView In this Method.");
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantResponse merchantResponse = new MerchantResponse();

		merchantResponse.setMerchantAppId(merchantDetails.getAppID());
		merchantResponse.setMerchantEmail(merchantDetails.getMerchantEMail());
		merchantResponse.setMerchantKyc(merchantDetails.getKycStatus());
		merchantResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantResponse.setMerchantPhone(merchantDetails.getPhoneNumber());
		merchantResponse.setMerchantOtpStatus(merchantDetails.getOtpStatus());
		merchantResponse.setMerchantSecret(Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId()));

		return merchantResponse;
	}

	public SuccessResponseDto getTransactionDetails(String uuid, int pageNo, int pageRecords)
			throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		logger.info("Merchantid" + merchantDetails);

		Pageable paging = PageRequest.of(pageNo, pageRecords);
		Page<TransactionDetails> pageTuts;

		pageTuts = transactionDetailsRepository.findByMerchantIdContaining(merchantDetails.getMerchantID(), paging);
		List<TransactionDetails> listTransactionDetails = pageTuts.getContent();

		return populateTransactionDetails(listTransactionDetails);
	}

	public SuccessResponseDto getTransactionDetails(String uuid) throws ValidationExceptions {

		logger.info("merchantView In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		logger.info("Merchantid" + merchantDetails);
		List<TransactionDetails> listTransactionDetails = transactionDetailsRepository
				.findAllTopByMerchantId(merchantDetails.getMerchantID());
		/*
		 * List<TransactionDetailsDto> trdetails = new
		 * ArrayList<TransactionDetailsDto>(); for (TransactionDetails tr :
		 * listTransactionDetails) { TransactionDetailsDto trd = new
		 * TransactionDetailsDto(); trd.setMerchantId(tr.getMerchantId());
		 * trd.setAmount(Float.toString(((float) tr.getAmount() / 100)));
		 * trd.setPaymentOption(tr.getPaymentOption()); trd.setOrderID(tr.getOrderID());
		 * trd.setStatus(tr.getStatus()); trd.setPaymentMode(tr.getPaymentMode());
		 * trd.setTxtMsg(tr.getTxtMsg());
		 * trd.setTransactionTime(tr.getCreated().toString());
		 * trd.setMerchantOrderId(tr.getMerchantOrderId());
		 * trd.setMerchantReturnURL(tr.getMerchantReturnURL()); if (tr.getVpaUPI() !=
		 * null) {
		 * trd.setVpaUPI(SecurityUtils.decryptSaveData(tr.getVpaUPI()).replace("\u0000",
		 * "")); } if (tr.getPaymentCode() != null) {
		 * trd.setWalletOrBankCode(SecurityUtils.decryptSaveData(tr.getPaymentCode()).
		 * replace("\u0000", "")); } if (tr.getCardNumber() != null) {
		 * trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveData(tr.
		 * getCardNumber())) .replace("\u0000", "")); } trdetails.add(trd); }
		 */
		return populateTransactionDetails(listTransactionDetails);
	}

	public SuccessResponseDto populateTransactionDetails(List<TransactionDetails> listTransactionDetails) {
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : listTransactionDetails) {
			TransactionDetailsDto trd = new TransactionDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setAmount(Float.toString(((float) tr.getAmount() / 100)));
			trd.setPaymentOption(tr.getPaymentOption());
			trd.setOrderID(tr.getOrderID());
			trd.setStatus(tr.getStatus());
			trd.setPaymentMode(tr.getPaymentMode());
			trd.setTxtMsg(tr.getTxtMsg());
			trd.setTransactionTime(tr.getCreated().toString());
			trd.setMerchantOrderId(tr.getMerchantOrderId());
			trd.setMerchantReturnURL(tr.getMerchantReturnURL());
			trd.setOrderNote(tr.getOrderNote());
			if (tr.getVpaUPI() != null) {
				trd.setVpaUPI(SecurityUtils.decryptSaveData(tr.getVpaUPI()).replace("\u0000", ""));
			}
			if (tr.getPaymentCode() != null) {
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveData(tr.getCardNumber()))
						.replace("\u0000", ""));
			}
			trdetails.add(trd);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Transaction Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactiondetail", trdetails);	
		return sdto;
	}

	public List<MerchantSettlement> getSettleDetails(String uuid) throws ValidationExceptions {
		logger.info("getUnSettleDetails In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<ISettlementBalanceReport> merchantBalanceSheet = merchantBalanceSheetRepository
				.getSettlementBalanceSheet("SETTLED", merchantDetails.getMerchantID());
		List<MerchantSettlement> merchantBalSheet = new ArrayList<MerchantSettlement>();
		for (ISettlementBalanceReport mer : merchantBalanceSheet) {
			MerchantSettlement m = new MerchantSettlement();
			m.setAmount(mer.getAmount());
			m.setCreated(mer.getCreated());
			m.setMerchant_id(mer.getMerchant_id());
			m.setMerchant_order_id(mer.getMerchant_order_id());
			m.setSettlement_status(mer.getSettlement_status());
			m.setTr_type(mer.getTr_type());
			m.setSettledAmount(mer.getSettle_amount_to_merchant());
			if (mer.getVpaupi() != null) {
				m.setVpaupi(SecurityUtils.decryptSaveData(mer.getVpaupi()).replace("\u0000", ""));
			}
			if (mer.getPayment_code() != null) {
				m.setWalletOrBankCode(mer.getPayment_code());
			}
			if (mer.getCard_number() != null) {
				m.setCard_number(Utility.maskCardNumber(SecurityUtils.decryptSaveData(mer.getCard_number()))
						.replace("\u0000", ""));
			}

			merchantBalSheet.add(m);

		}
		return merchantBalSheet;
	}

	public List<TransactionDetailsDto> getLast3DaysTransaction(String uuid) throws ValidationExceptions {
		logger.info("getLast3DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<TransactionDetails> transactionDetails = transactionDetailsRepository
				.findLast3DaysTransaction(merchantDetails.getMerchantID());
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : transactionDetails) {
			TransactionDetailsDto trd = new TransactionDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setAmount(Float.toString(((float) tr.getAmount() / 100)));
			trd.setPaymentOption(tr.getPaymentOption());
			trd.setOrderID(tr.getOrderID());
			trd.setStatus(tr.getStatus());
			trd.setPaymentMode(tr.getPaymentMode());
			trd.setTxtMsg(tr.getTxtMsg());
			trd.setTransactionTime(tr.getCreated().toString());
			trd.setMerchantOrderId(tr.getMerchantOrderId());
			trd.setMerchantReturnURL(tr.getMerchantReturnURL());
			if (tr.getVpaUPI() != null) {
				trd.setVpaUPI(SecurityUtils.decryptSaveData(tr.getVpaUPI()).replace("\u0000", ""));
			}
			if (tr.getPaymentCode() != null) {
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			if (tr.getCardNumber() != null) {
				trd.setCardNumber(Utility.maskCardNumber(SecurityUtils.decryptSaveData(tr.getCardNumber()))
						.replace("\u0000", ""));
			}
			trdetails.add(trd);
		}
		return trdetails;
	}

	public List<MerchantBalanceSheet> getSettleDetailsLat7Days(String uuid) throws ValidationExceptions {
		logger.info("getLast7DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<MerchantBalanceSheet> merchantBalanceSheet = merchantBalanceSheetRepository
				.findLast7DaysSettleTransaction(merchantDetails.getMerchantID());

		return merchantBalanceSheet;
	}

	public List<MerchantSettlement> getUnSettleDetails(String uuid) throws ValidationExceptions {
		logger.info("getUnSettleDetails In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<ISettlementBalanceReport> merchantBalanceSheet = merchantBalanceSheetRepository
				.getSettlementBalanceSheet("PENDING", merchantDetails.getMerchantID());
		List<MerchantSettlement> merchantBalSheet = new ArrayList<MerchantSettlement>();
		for (ISettlementBalanceReport mer : merchantBalanceSheet) {
			MerchantSettlement m = new MerchantSettlement();
			m.setAmount(mer.getAmount());
			m.setCreated(mer.getCreated());
			m.setMerchant_id(mer.getMerchant_id());
			m.setMerchant_order_id(mer.getMerchant_order_id());
			m.setSettlement_status(mer.getSettlement_status());
			m.setTr_type(mer.getTr_type());

			if (mer.getVpaupi() != null) {
				m.setVpaupi(SecurityUtils.decryptSaveData(mer.getVpaupi()).replace("\u0000", ""));
			}
			if (mer.getPayment_code() != null) {
				m.setWalletOrBankCode(mer.getPayment_code());
			}
			if (mer.getCard_number() != null) {
				m.setCard_number(Utility.maskCardNumber(SecurityUtils.decryptSaveData(mer.getCard_number()))
						.replace("\u0000", ""));
			}

			merchantBalSheet.add(m);

		}
		return merchantBalSheet;
	}

	public DashBoardDetails getDashBoardBalance(String uuid) throws ValidationExceptions {
		logger.info("getDashBoardBalance In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		DashBoardDetails dashBoardDetails = new DashBoardDetails();

		String amt = "0";
		String unsettledamt = "0";
		String settled = "0";
		/*
		 * List<IMerchantTransaction> tda = transactionDetailsRepository
		 * .getTodayTrDetails(merchantDetails.getMerchantID()); float a = 0; if
		 * (tda.isEmpty()) { a = 0; } else { a = (float) tda.get(0).getAmount() / 100; }
		 */
		logger.info("Merchant ID::" + merchantDetails.getMerchantID());
		String a = transactionDetailsRepository.getTodayTr(merchantDetails.getMerchantID());

		amt = a;
		if ((amt == null)) {
			amt = "0";
		}
		logger.info("Todays Amount for dashboard :: " + amt);
		dashBoardDetails.setTodaysTransactions(amt);

		unsettledamt = merchantBalanceSheetRepository.getPendingSettlementTotal(merchantDetails.getMerchantID());

		logger.info("Unsettled Amount for dashboard :: " + unsettledamt);

		dashBoardDetails.setUnsettledAmount(unsettledamt);

		settled = merchantBalanceSheetRepository.getSettledTotal(merchantDetails.getMerchantID());
		logger.info("Settled Amount for dashboard :: " + settled);
		if (settled == null) {
			dashBoardDetails.setLastSettlements("0");
		} else {
			dashBoardDetails.setLastSettlements(settled);
		}
		return dashBoardDetails;
	}

	public MerchantBankDetails createBankDetails(MerchantBankDetails merchantBankDetails, String uuid)
			throws ValidationExceptions {

		logger.info("getDashBoardBalance In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails2 = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails2 != null) {
			throw new ValidationExceptions(MERCHANT_BANK_DETAIL_PRESENT,
					FormValidationExceptionEnums.MERCHANT_BANK_DETAILS_EXISTS);
		}
		
		logger.info("account number :"+ merchantBankDetails.getAccountNo() + merchantBankDetails.getBankIFSCCode() + merchantBankDetails.getMicrCode());
		if(!Validator.isValidAccountNumber(merchantBankDetails.getAccountNo())) {
			throw new ValidationExceptions(ACCOUNT_NUMBER_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidIfsc(merchantBankDetails.getBankIFSCCode())) {
			throw new ValidationExceptions(IFSC_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidMICR(merchantBankDetails.getMicrCode())) {
			throw new ValidationExceptions(MICR_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		merchantBankDetails.setMerchantID(merchantDetails.getMerchantID());
		merchantBankDetails.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails.setCity(merchantBankDetails.getCity());
		merchantBankDetails.setMicrCode(merchantBankDetails.getMicrCode());
		merchantBankDetailsRepository.save(merchantBankDetails);

		return merchantBankDetails;
	}

	public MerchantBankDetails getBankDetails(String uuid) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails == null) {
			throw new ValidationExceptions(BANK_DETAILS_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_BANK_DETILS_NOT_FOUND);
		}
		return merchantBankDetails;

	}

	public MerchantBankDetails updateBankDetails(String uuid, MerchantBankDetails merchantBankDetails)
			throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantBankDetails merchantBankDetails2 = merchantBankDetailsRepository
				.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails2 == null) {
			merchantBankDetails2 = new MerchantBankDetails();
			merchantBankDetails2.setMerchantID(merchantDetails.getMerchantID());
		}
		
		if(!Validator.isValidAccountNumber(merchantBankDetails.getAccountNo())) {
			throw new ValidationExceptions(ACCOUNT_NUMBER_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidIfsc(merchantBankDetails.getBankIFSCCode())) {
			throw new ValidationExceptions(IFSC_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}
		
		if(!Validator.isValidMICR(merchantBankDetails.getMicrCode())) {
			throw new ValidationExceptions(MICR_VAIDATION_FILED, FormValidationExceptionEnums.INPUT_VALIDATION_ERROR);
		}

		merchantBankDetails2.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails2.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails2.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails2.setCity(merchantBankDetails.getCity());
		merchantBankDetails2.setMicrCode(merchantBankDetails.getMicrCode());

		merchantBankDetailsRepository.save(merchantBankDetails2);

		return merchantBankDetails2;
	}
	
	public MerchantDetails updateOtpStatus(String uuid, String status)
			throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}


		if (!Validator.containsEnum(OtpStatus.class, status)) {
			throw new ValidationExceptions(OTP_STATUS, FormValidationExceptionEnums.OTP_STATUS_VALIDATION_ERROR);
		}

		merchantDetails.setOtpStatus(status);
		merchantDetails.setUpdateReason("OTP_STATUS_UPDATE");
		merchantDetails.setUpdateBy(uuid);
		return merchantDetailsRepository.save(merchantDetails);
	}

	public SuccessResponseDto getMerchantLastDaySettlement(String merchantID) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last Day Settlement Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantLastDaySettlement", merchantBalanceSheetRepository.getLastDaySettlement(merchantID));	
		return sdto;
	}

	public Object getMerchantCurrDaySettlement(String merchantID) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Current Day Settlement Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantCurrDaySettlement", merchantBalanceSheetRepository.getCurrDaySettlement(merchantID));	
		return sdto;
	}

	public Object getMerchantLast7DaySettlement(String merchantID) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last 7 Day Settlement Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantLast7DaySettlement", merchantBalanceSheetRepository.getLast7DaySettlement(merchantID));	
		return sdto;
	}

	public Object getMerchantCurrMonthSettlement(String merchantID) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Current Month Settlement Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantSettlementDetail", merchantBalanceSheetRepository.getCurrMonthSettlement(merchantID));	
		return sdto;
	}

	public Object getMerchantLastMonthSettlement(String merchantID) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last Month Settlement Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantSettlementDetail", merchantBalanceSheetRepository.getLastMonthSettlement(merchantID));	
		return sdto;
	}

	public Object getMerchantLast90DaySettlement(String merchantID) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last 90 Day Settlement Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantSettlementDetail", merchantBalanceSheetRepository.getLast90DaySettlement(merchantID));	
		return sdto;
	}

	public SuccessResponseDto getMerchantDetailsReport(String merchantId) {

		List<IMerchantDetailsReport> getMerchantDetailsReport = merchantPGServicesRepository
				.getMerchantDetailsReport(merchantId, UserStatus.ACTIVE.toString());

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Detail Report!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantReport", getMerchantDetailsReport);	
		return sdto;
	}

	public SuccessResponseDto merchantStatusTransactionLastDay(MerchantDetails user) {

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last Day Transaction!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", transactionDetailsRepository.getYesterdayTrDetails(user.getMerchantID()));	
		return sdto;
	}

	public SuccessResponseDto merchantStatusTransactionToday(MerchantDetails user) {
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Today Transaction!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", transactionDetailsRepository.getTodayTrDetails(user.getMerchantID()));	
		return sdto;
	}

	public SuccessResponseDto merchantStatusTransactionCurrMonth(MerchantDetails user) {
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Current Month Transaction!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", transactionDetailsRepository.getCurrMonthTrDetails(user.getMerchantID()));	
		return sdto;
	}

	public SuccessResponseDto merchantStatusTransactionLastMonth(MerchantDetails user) {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Last Month Transaction!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", transactionDetailsRepository.getLastMonthTrDetails(user.getMerchantID()));	
		return sdto;
	}

	public SuccessResponseDto merchantCreateApiForCustomer(String uuid, MerchantDetails merchantDetails,
			String custName, String custPhone, String custEmail, String custAmount, int linkExpiry, String orderNote,
			String source) throws ValidationExceptions, ParseException, InvalidKeyException, NoSuchAlgorithmException,
			UnsupportedEncodingException, JsonProcessingException, UserException {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		ZonedDateTime expirationTime = ZonedDateTime.now().plus(linkExpiry, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		logger.info("Expiry Time:" + date.toString());
		String orderId = Utility.getRandomId();

		if (!Validator.isValidCardUserName(custName)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidPhoneNumber(custPhone)) {
			throw new ValidationExceptions(PHONE_VAIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidEmail(custEmail)) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		if (!Validator.validDouble(custAmount)) {
			throw new ValidationExceptions(AMOUNT_VALIDATION_ERROR,
					FormValidationExceptionEnums.AMOUNT_VALIDATION_ERROR);
		}

		MerchantRequest4Customer merchantRequest4Customer = merchantRequest4CustomerRepository
				.findByOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());
		if (merchantRequest4Customer != null) {
			throw new ValidationExceptions(ORDER_ID_EXITS_WITH_LINK,
					FormValidationExceptionEnums.ORDER_ID_EXITS_WITH_LINK);
		}

		merchantRequest4Customer = new MerchantRequest4Customer();
		merchantRequest4Customer.setAmount(custAmount);
		merchantRequest4Customer.setCreatedBy(uuid);
		merchantRequest4Customer.setCustEmail(custEmail);
		merchantRequest4Customer.setCustName(custName);
		merchantRequest4Customer.setCustPhone(custPhone);
		merchantRequest4Customer.setLinkCustomer(apiEndPoint + "/customerRequest/" + Utility.randomStringGenerator(10));
		merchantRequest4Customer.setLinkExpiry(linkExpiry);
		merchantRequest4Customer.setLinkExpiryTime(date);
		merchantRequest4Customer.setStatus(UserStatus.PENDING.toString());
		merchantRequest4Customer.setMerchantId(merchantDetails.getMerchantID());
		merchantRequest4Customer.setOrderCurrency("INR");
		merchantRequest4Customer.setOrderId(orderId);
		merchantRequest4Customer.setOrderNote(orderNote);
		merchantRequest4Customer.setReturnUrl(apiCustomerNotifyUrl);
		merchantRequest4Customer.setAppId(merchantDetails.getAppID());

		parameters.put("customerEmail", custEmail);
		parameters.put("customerName", custName);
		parameters.put("customerPhone", custPhone);
		parameters.put("customerid", merchantRequest4Customer.getAppId());
		parameters.put("notifyUrl", apiCustomerNotifyUrl);
		parameters.put("orderAmount", custAmount);
		parameters.put("orderCurrency", "INR");
		parameters.put("orderid", orderId);
		parameters.put("orderNote", orderNote);
		logger.info("Generating Signature for Link");
		String data = EncryptSignature
				.encryptSignature(Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId()), parameters);
		merchantRequest4Customer.setSignature(data.trim());

		String[] arrOfStr = apiCustomerExclude.split(",");
		List<String> integerList = Arrays.asList(arrOfStr);
		if (!integerList.contains(source)) {
			merchantRequest4Customer.setSource(source);
			merchantRequest4Customer.setEmailCounter(1);
			logger.info("Inside mail block");
			sendMail.sendGeneratedMailToCustomer(custName, custEmail, custPhone,
					merchantRequest4Customer.getLinkCustomer());
			logger.info(merchantRequest4Customer.getLinkCustomer());
			String msg = URLEncoder.encode("Hi User, Eazypaymentz Analytiq Payment Link of Rs " +custAmount+ " is "+merchantRequest4Customer.getLinkCustomer(), StandardCharsets.UTF_8);
			smsCallTemplate.smsSendbyApi(msg, custPhone, smsSenderId);
		} else {
			merchantRequest4Customer.setSource(source);
		}
		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Customer Created!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("customerDetail", merchantRequest4Customer);	
		return sdto;

	}

	public MerchantRequest4Customer merchantCreateApiForCustomer(String uuid, MerchantDetails merchantDetails,
			String custName, String custPhone, String custEmail, String custAmount, int linkExpiry, String orderNote,
			String returnUrl, String orderId, String source)
			throws ValidationExceptions, ParseException, InvalidKeyException, NoSuchAlgorithmException,
			UnsupportedEncodingException, JsonProcessingException, UserException {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		ZonedDateTime expirationTime = ZonedDateTime.now().plus(linkExpiry, ChronoUnit.MINUTES);
		Date date = Date.from(expirationTime.toInstant());
		if (orderId.strip().length() < 5) {
			orderId = Utility.getRandomId();
		}

		if (!Validator.isValidCardUserName(custName)) {
			throw new ValidationExceptions(NAME_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidPhoneNumber(custPhone)) {
			throw new ValidationExceptions(PHONE_VAIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		if (!Validator.isValidEmail(custEmail)) {
			throw new ValidationExceptions(EMAIL_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		if (!Validator.validDouble(custAmount)) {
			throw new ValidationExceptions(AMOUNT_VALIDATION_ERROR,
					FormValidationExceptionEnums.AMOUNT_VALIDATION_ERROR);
		}

		MerchantRequest4Customer merchantRequest4Customer = merchantRequest4CustomerRepository
				.findByOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());
		if (merchantRequest4Customer != null) {
			throw new ValidationExceptions(ORDER_ID_EXITS_WITH_LINK,
					FormValidationExceptionEnums.ORDER_ID_EXITS_WITH_LINK);
		}

		merchantRequest4Customer = new MerchantRequest4Customer();
		merchantRequest4Customer.setAmount(custAmount);
		merchantRequest4Customer.setCreatedBy(uuid);
		merchantRequest4Customer.setCustEmail(custEmail);
		merchantRequest4Customer.setCustName(custName);
		merchantRequest4Customer.setCustPhone(custPhone);
		merchantRequest4Customer.setLinkCustomer(apiEndPoint + "/customerRequest/" + Utility.randomStringGenerator(10));
		merchantRequest4Customer.setLinkExpiry(linkExpiry);
		merchantRequest4Customer.setLinkExpiryTime(date);
		merchantRequest4Customer.setStatus(UserStatus.PENDING.toString());
		merchantRequest4Customer.setMerchantId(merchantDetails.getMerchantID());
		merchantRequest4Customer.setOrderCurrency("INR");
		merchantRequest4Customer.setOrderId(orderId);
		merchantRequest4Customer.setOrderNote(orderNote);
		merchantRequest4Customer.setReturnUrl(returnUrl);
		merchantRequest4Customer.setAppId(merchantDetails.getAppID());
		merchantRequest4Customer.setSource(source);

		parameters.put("customerEmail", custEmail);
		parameters.put("customerName", custName);
		parameters.put("customerPhone", custPhone);
		parameters.put("customerid", merchantRequest4Customer.getAppId());
		parameters.put("notifyUrl", returnUrl);
		parameters.put("orderAmount", custAmount);
		parameters.put("orderCurrency", "INR");
		parameters.put("orderid", orderId);
		parameters.put("orderNote", orderNote);
		logger.info("Generating Signature for Link with return");
		String data = EncryptSignature
				.encryptSignature(Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId()), parameters);
		merchantRequest4Customer.setSignature(data.trim());
		logger.info(apiCustomerExclude);
		String[] arrOfStr = apiCustomerExclude.split(",");
		List<String> integerList = Arrays.asList(arrOfStr);
		if (!integerList.contains(source)) {
			logger.info("Sending Mail::"+source);
			merchantRequest4Customer.setEmailCounter(1);
			sendMail.sendGeneratedMailToCustomer(custName, custEmail, custPhone,
					merchantRequest4Customer.getLinkCustomer());

			logger.info(merchantRequest4Customer.getLinkCustomer());
			String msg = URLEncoder.encode("Hi User, Eazypaymentz Analytiq Payment Link of Rs " +custAmount+ " is "+merchantRequest4Customer.getLinkCustomer(), StandardCharsets.UTF_8);
			merchantRequest4Customer.setSmsCounter(1);
			smsCallTemplate.smsSendbyApi(msg, custPhone, smsSenderId);

		} else {
			merchantRequest4Customer.setSource(source);
		}

		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		return merchantRequest4Customer;

	}

	public SuccessResponseDto merchantReEmailSend(String orderId, MerchantDetails merchantDetails) throws ValidationExceptions {

		MerchantRequest4Customer merchantRequest4Customer = merchantRequest4CustomerRepository
				.findByOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());
		if (merchantRequest4Customer == null) {
			throw new ValidationExceptions(ORDER_ID_NOT_FOUND, FormValidationExceptionEnums.ORDER_ID_NOT_FOUND);
		}

		if (merchantRequest4Customer.getEmailCounter() == 0) {
			throw new ValidationExceptions(RESEND_EMAIL_NOT_POSSIBLE,
					FormValidationExceptionEnums.RESEND_EMAIL_NOT_POSSIBLE);
		}

		if (merchantRequest4Customer.getEmailCounter() >= resendEmailCounter) {
			throw new ValidationExceptions(EXITS_RESEND_EMAIL_LINK_COUNTER,
					FormValidationExceptionEnums.EXITS_RESEND_EMAIL_LINK_COUNTER);
		}

		sendMail.sendGeneratedMailToCustomer(merchantRequest4Customer.getCustName(),
				merchantRequest4Customer.getCustEmail(), merchantRequest4Customer.getCustPhone(),
				merchantRequest4Customer.getLinkCustomer());

		merchantRequest4Customer.setEmailCounter(merchantRequest4Customer.getEmailCounter() + 1);
		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Email sent to registered email-id!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("ResendEmail", merchantRequest4Customer);		
		return sdto;
	}
	
	public SuccessResponseDto merchantReSmsSend(String orderId, MerchantDetails merchantDetails) throws ValidationExceptions, JsonProcessingException, UserException {

		MerchantRequest4Customer merchantRequest4Customer = merchantRequest4CustomerRepository
				.findByOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());
		if (merchantRequest4Customer == null) {
			throw new ValidationExceptions(ORDER_ID_NOT_FOUND, FormValidationExceptionEnums.ORDER_ID_NOT_FOUND);
		}

		if (merchantRequest4Customer.getSmsCounter() == 0) {
			throw new ValidationExceptions(RESEND_SMS_NOT_POSSIBLE,
					FormValidationExceptionEnums.RESEND_SMS_NOT_POSSIBLE);
		}

		if (merchantRequest4Customer.getSmsCounter() >= resendSmsCounter) {
			throw new ValidationExceptions(EXITS_RESEND_SMS_LINK_COUNTER,
					FormValidationExceptionEnums.EXITS_RESEND_SMS_LINK_COUNTER);
		}

		logger.info(merchantRequest4Customer.getLinkCustomer());
		String msg = URLEncoder.encode("Hi User, Eazypaymentz Analytiq Payment Link of Rs " +merchantRequest4Customer.getAmount()+ " is "+merchantRequest4Customer.getLinkCustomer(), StandardCharsets.UTF_8);
		merchantRequest4Customer.setSmsCounter(1);
		smsCallTemplate.smsSendbyApi(msg, merchantRequest4Customer.getCustPhone(), smsSenderId);

		merchantRequest4Customer.setSmsCounter(merchantRequest4Customer.getSmsCounter() + 1);
		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("SMS sent to registered Phone Number!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("ResendSms", merchantRequest4Customer);		
		return sdto;
	}

	public SuccessResponseDto merchantReSmsAndEmailSend(String orderId, MerchantDetails merchantDetails) throws ValidationExceptions, JsonProcessingException, UserException {

		MerchantRequest4Customer merchantRequest4Customer = merchantRequest4CustomerRepository
				.findByOrderIdAndMerchantId(orderId, merchantDetails.getMerchantID());
		if (merchantRequest4Customer == null) {
			throw new ValidationExceptions(ORDER_ID_NOT_FOUND, FormValidationExceptionEnums.ORDER_ID_NOT_FOUND);
		}

		if (merchantRequest4Customer.getEmailCounter() == 0) {
			throw new ValidationExceptions(RESEND_EMAIL_NOT_POSSIBLE,
					FormValidationExceptionEnums.RESEND_EMAIL_NOT_POSSIBLE);
		}

		if (merchantRequest4Customer.getEmailCounter() >= resendEmailCounter) {
			throw new ValidationExceptions(EXITS_RESEND_EMAIL_LINK_COUNTER,
					FormValidationExceptionEnums.EXITS_RESEND_EMAIL_LINK_COUNTER);
		}
		
		if (merchantRequest4Customer.getSmsCounter() == 0) {
			throw new ValidationExceptions(RESEND_SMS_NOT_POSSIBLE,
					FormValidationExceptionEnums.RESEND_SMS_NOT_POSSIBLE);
		}

		if (merchantRequest4Customer.getSmsCounter() >= resendSmsCounter) {
			throw new ValidationExceptions(EXITS_RESEND_SMS_LINK_COUNTER,
					FormValidationExceptionEnums.EXITS_RESEND_SMS_LINK_COUNTER);
		}


		sendMail.sendGeneratedMailToCustomer(merchantRequest4Customer.getCustName(),
				merchantRequest4Customer.getCustEmail(), merchantRequest4Customer.getCustPhone(),
				merchantRequest4Customer.getLinkCustomer());

		merchantRequest4Customer.setEmailCounter(merchantRequest4Customer.getEmailCounter() + 1);
		
		logger.info(merchantRequest4Customer.getLinkCustomer());
		String msg = URLEncoder.encode("Hi User, Eazypaymentz Analytiq Payment Link of Rs " +merchantRequest4Customer.getAmount()+ " is "+merchantRequest4Customer.getLinkCustomer(), StandardCharsets.UTF_8);
		merchantRequest4Customer.setSmsCounter(1);
		smsCallTemplate.smsSendbyApi(msg, merchantRequest4Customer.getCustPhone(), smsSenderId);

		merchantRequest4Customer.setSmsCounter(merchantRequest4Customer.getSmsCounter() + 1);
		merchantRequest4CustomerRepository.save(merchantRequest4Customer);
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("SMS and Email sent to registered Phone Number and Email-id!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("ResendSmsEmail", merchantRequest4Customer);		
		return sdto;
	}
	
	public SuccessResponseDto getCustomerApiRequestReport(MerchantDetails user) throws ValidationExceptions {

		String source = "";
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Customer Report!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("customerReport", merchantRequest4CustomerRepository.getCustomerRequestReport(user.getMerchantID(), source));	
		return sdto;
	}

	public Object addBeneficiaryBankAccount(MerchantDetails user, CreateBeneficiaryRequest createBeneficiaryRequest)
			throws ValidationExceptions, JsonProcessingException, ParseException {

		SuccessResponseDto sdto = new SuccessResponseDto();
		MerchantBeneficiaryDetails merchantBeneficiaryDetailsDup = merchantBeneficiaryDetailsRepo
				.findByMerchantOrderId(createBeneficiaryRequest.getMerchantOrderId());
		if (merchantBeneficiaryDetailsDup != null) {
			throw new ValidationExceptions(DUPLICATE_ORDER_ID_E0208, FormValidationExceptionEnums.E0208);
		}

		IMerchantWalletDetails iMerchantWalletDetails = merchantBeneficiaryDetailsRepo
				.getMerchatWalletDetails(user.getMerchantID());
		if (iMerchantWalletDetails == null) {
			throw new ValidationExceptions(MERCHANT_DISBRUSHMENT_ACCOUNT_NOT_FOUND_E0200,
					FormValidationExceptionEnums.E0200);
		}
		logger.info("Merchant Wallet Disbrushment Account :: " + iMerchantWalletDetails.getWalletGuuid());

		if (ValidationUtils.validateBeneficiaryAddRequest(createBeneficiaryRequest)) {
			MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo
					.findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndStatus(
							createBeneficiaryRequest.getBeneficiaryAccountNo(),
							createBeneficiaryRequest.getBeneficiaryIFSCCode(), "ACTIVE");

			if (merchantBeneficiaryDetails != null) {
				if (merchantBeneficiaryDetails.getMerchantId().equalsIgnoreCase(user.getMerchantID())) {
					if (merchantBeneficiaryDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
						throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_EXITS_E0204,
								FormValidationExceptionEnums.E0204);
					}
				}

			}
			
			sdto.getMsg().add("Account Added!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("BenificeryBankAccount", merchantBeneficiaryDetailsRepo.save(payoutWalletUtilityServices.populateMerchnatBeneficiary(user,
					createBeneficiaryRequest, iMerchantWalletDetails.getWalletGuuid())));	
			return sdto;
		}

		return null;
	}

	public SuccessResponseDto deleteBeneficiaryBankAccount(MerchantDetails user,
			DeleteBeneficiaryRequest deleteBeneficiaryRequest) throws ValidationExceptions, JsonProcessingException {

		MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo
				.findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndMerchantId(
						deleteBeneficiaryRequest.getBeneficiaryAccountNo(),
						deleteBeneficiaryRequest.getBeneficiaryIFSCCode(), user.getMerchantID());

		if (merchantBeneficiaryDetails == null) {
			logger.error("There is no records found as per Delete request .");
			throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206,
					FormValidationExceptionEnums.E0206);
		}
		if (merchantBeneficiaryDetails.getStatus().equalsIgnoreCase(UserStatus.DELETE.toString())) {
			logger.error("The beneficiary account already in deleted state.");
			throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_DELETED_E0207, FormValidationExceptionEnums.E0207);
		}

		merchantBeneficiaryDetails.setStatus(UserStatus.DELETE.toString());
		merchantBeneficiaryDetails.setModifiedBy(user.getUuid());
		merchantBeneficiaryDetails.setUpdateRequestData(Utility.convertDTO2JsonString(deleteBeneficiaryRequest));

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Account Deleted!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("BenificaryAccount", merchantBeneficiaryDetailsRepo.save(merchantBeneficiaryDetails));	
		return sdto;
	}

	public Object associateBeneficiaryBankAccount(MerchantDetails user, AssociateBankDetails associateBankDetails)
			throws ValidationExceptions, JsonProcessingException, ParseException {

		SuccessResponseDto sdto = new SuccessResponseDto();
		IMerchantWalletDetails iMerchantWalletDetails = merchantBeneficiaryDetailsRepo
				.getMerchatWalletDetails(user.getMerchantID());
		if (iMerchantWalletDetails == null) {
			throw new ValidationExceptions(MERCHANT_DISBRUSHMENT_ACCOUNT_NOT_FOUND_E0200,
					FormValidationExceptionEnums.E0200);
		}
		logger.info("Merchant Wallet Disbrushment Account :: " + iMerchantWalletDetails.getWalletGuuid());

		if (ValidationUtils.validateBeneficiaryAssocRequest(associateBankDetails)) {
			MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo
					.findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndStatus(
							associateBankDetails.getBeneficiaryAccountNo(),
							associateBankDetails.getBeneficiaryIFSCCode(), "ACTIVE");
			if (merchantBeneficiaryDetails == null) {
				throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206,
						FormValidationExceptionEnums.E0206);
			}
			if (merchantBeneficiaryDetails.getMerchantId().equalsIgnoreCase(user.getMerchantID())) {
				if (merchantBeneficiaryDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
					throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_EXITS_E0204,
							FormValidationExceptionEnums.E0204);
				}
			}

			
			sdto.getMsg().add("Account Added!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("BenificaryAccount", merchantBeneficiaryDetailsRepo.save(payoutWalletUtilityServices.associateMerchnatBeneficiary(user,
					associateBankDetails, merchantBeneficiaryDetails, iMerchantWalletDetails.getWalletGuuid())));	
			return sdto;
		}

		return null;
	}

	public Object verifyBankAccount(MerchantDetails user, VerifyBankAccount verifyBankAccount)
			throws ValidationExceptions, ParseException {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		ObjectMapper mapper = new ObjectMapper();
		MerchantBeneficiaryDetails merchantBeneficiaryDetails = merchantBeneficiaryDetailsRepo
				.findByBeneficiaryAccountIdAndBeneficiaryIFSCCodeAndMerchantIdAndStatus(
						verifyBankAccount.getBeneficiaryAccountNo(), verifyBankAccount.getBeneficiaryIFSCCode(),
						user.getMerchantID(), UserStatus.ACTIVE.toString());

		if (merchantBeneficiaryDetails == null) {
			throw new ValidationExceptions(MERCHANT_BANK_BENEFICIARY_NOT_FOUND_E0206,
					FormValidationExceptionEnums.E0206);
		}

		if (!merchantBeneficiaryDetails.getAccountValidationFlag().equalsIgnoreCase("F")) {
			throw new ValidationExceptions(BENEFICIARY_ALREADY_VERIFIED_E0209, FormValidationExceptionEnums.E0209);
		}

		verifyBankAccount.setMerchantId(user.getMerchantID());
		if (Objects.isNull(verifyBankAccount.getOrderId())) {
			verifyBankAccount.setOrderId(Utility.getRandomId());
		}

		String strVeriftAccount = payoutMerchant.verifyAccount(verifyBankAccount);
		logger.info("strVeriftAccount :: " + strVeriftAccount);
		try {
			VerifyBankAccountResponse verifyBankAccountResponse = mapper.readValue(strVeriftAccount,
					VerifyBankAccountResponse.class);
			if (verifyBankAccountResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
				merchantBeneficiaryDetails.setAccountValidationFlag("T");
			} else if (verifyBankAccountResponse.getStatus().equalsIgnoreCase("ACCEPTED")
					|| verifyBankAccountResponse.getStatus().equalsIgnoreCase("PENDING")) {
				merchantBeneficiaryDetails.setAccountValidationFlag("P");
			} else {
				merchantBeneficiaryDetails.setAccountValidationFlag("F");
			}
			merchantBeneficiaryDetailsRepo.save(merchantBeneficiaryDetails);

		} catch (Exception e) {
			sdto.getMsg().add("Account Verified!");
			sdto.setSuccessCode(SuccessCode.API_SUCCESS);
			sdto.getExtraData().put("accountVerification", strVeriftAccount);	
			return sdto;
		}
		sdto.getMsg().add("Account Verified!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("accountVerification", strVeriftAccount);	
		return sdto;
	}

	public MerchantDetails getMerchantFromAppId(String appId)
			throws JsonProcessingException, ValidationExceptions, NoSuchAlgorithmException {

		MerchantDetails merhantDetails = merchantDetailsRepository.findByAppID(appId);
		if (merhantDetails == null) {
			throw new ValidationExceptions(MERCHANT_NOT_FOUND + appId, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		logger.info("Merchant Detals :: " + Utility.convertDTO2JsonString(merhantDetails));

		return merhantDetails;
	}

	public SuccessResponseDto getCustomerDetailsReport(MerchantDetails user, String mobileNo, String emailId)
			throws ValidationExceptions {

		if (mobileNo == null && emailId == null) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		if (mobileNo.length() == 0 && emailId.length() == 0) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		List<IUserDetails> listIUserDetails = userDetailsRepository.getDistinctUserDetails(emailId, mobileNo);
		List<UserDetailsReport> listIUserDetailsUps = new ArrayList<>();

		for (IUserDetails iuserDetails : listIUserDetails) {
			listIUserDetailsUps.add(UserDetailsUtils.updateUserDetails(iuserDetails));
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Customer Report!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("customerDetail", listIUserDetailsUps);	
		return sdto;
	}

	public SuccessResponseDto getCustomerDetailsAll(MerchantDetails user) {

		List<IUserDetails> listIUserDetails = userDetailsRepository.getDistinctUserDetailsAll();
		List<UserDetailsReport> listIUserDetailsUps = new ArrayList<>();

		for (IUserDetails iuserDetails : listIUserDetails) {
			listIUserDetailsUps.add(UserDetailsUtils.updateUserDetails(iuserDetails));
		}
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Customer Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("CustomerDetail", listIUserDetailsUps);	
		return sdto;
	}
	
	public boolean validateDateFormat(String dateToValdate) throws ValidationExceptions {
   	 
   	 if(dateToValdate.isBlank() || dateToValdate.isEmpty()) {
   		 throw new ValidationExceptions(DATE_PARAMETER_IS_MANDATORY, FormValidationExceptionEnums.DATE_PARAMETER_IS_MANDATORY);
   	 }

	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    formatter.setLenient(false);
	    Date parsedDate = null;
	    try {
	        parsedDate = formatter.parse(dateToValdate);
	       // System.out.println("++validated DATE TIME ++"+formatter.format(parsedDate));

	    } catch (ParseException e) {
	        return false;
	    }
	    return true;
	}
	
	public void dateWiseValidation(String start_date, String end_date) throws ValidationExceptions {
    	boolean checkStartFormat = validateDateFormat(start_date);
		boolean checkEndFormat = validateDateFormat(end_date);
		
		if(checkStartFormat == false || checkEndFormat == false || start_date.trim().contains(" ") || end_date.trim().contains(" ")|| start_date.matches(".*[a-zA-Z]+.*") || end_date.matches(".*[a-zA-Z]+.*")) {
			throw new ValidationExceptions(DATE_FORMAT, FormValidationExceptionEnums.DATE_FORMAT);
		}
		
    }

	public Object getMerchantSettleMentReportFilterWise(MerchantDetails user, String orderId, String status, int pageNo,
			int pageRecords, String dateTo, String dateFrom) throws ValidationExceptions {

		if (orderId == null && status == null) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}
		
		if(txnParam(dateFrom)==true && txnParam(dateTo)==true) {
    		dateWiseValidation(dateFrom,dateTo);
    	}

		Pageable paging = PageRequest.of(pageNo, pageRecords);
		Page<MerchantBalanceSheet> pageTuts;

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Settlement Report !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		
		if (txnParam(status)==true && txnParam(orderId)==false && txnParam(dateFrom)==true && txnParam(dateTo)==true) {
			logger.info("Inside Status Block");
			//pageTuts = merchantBalanceSheetRepository
				//	.findByMerchantIdAndSettlementStatusContaining(user.getMerchantID(), status, paging);
			//List<MerchantBalanceSheet> listMerchantBalanceSheet = pageTuts.getContent();
			
			
			sdto.getExtraData().put("MerchantReport", populateBalanceSheet(merchantBalanceSheetRepository
					.findByMerchantIdAndSettlementStatusWithDateRange(user.getMerchantID(),status, dateFrom, dateTo)));	
			return sdto;
		}
		if (txnParam(orderId)==true && txnParam(status)==false) {
			sdto.getExtraData().put("MerchantReport", populateBalanceSheet(
					merchantBalanceSheetRepository.findByMerchantIdAndMerchantOrderId(user.getMerchantID(), orderId)));	
			return sdto;
		}
		if (txnParam(orderId)==true && txnParam(status)==true) {
			sdto.getExtraData().put("MerchantReport", populateBalanceSheet(merchantBalanceSheetRepository
					.findByMerchantIdAndMerchantOrderIdAndSettlementStatus(user.getMerchantID(), orderId, status)));	
			return sdto;
		}
		if(txnParam(dateFrom)==true && txnParam(dateTo)==true && txnParam(status)==false && txnParam(orderId)==false) {
			sdto.getExtraData().put("MerchantReport", populateBalanceSheet(merchantBalanceSheetRepository
					.getSettlementDateDetails(dateFrom, dateTo)));	
			return sdto;
		}
		else if(txnParam(dateFrom)==false && txnParam(dateTo)==true && txnParam(status)==false && txnParam(orderId)==false) {
			validateDateFormat(dateTo);
			sdto.getExtraData().put("MerchantReport", populateBalanceSheet(merchantBalanceSheetRepository
					.getSettlementDateToDetails(user.getMerchantID(), dateTo)));	
			return sdto;
		}
		else if(txnParam(dateFrom)==true && txnParam(dateTo)==false && txnParam(status)==false && txnParam(orderId)==false) {
			validateDateFormat(dateFrom);
			sdto.getExtraData().put("MerchantReport", populateBalanceSheet(merchantBalanceSheetRepository
					.getSettlementDateFromDetails(user.getMerchantID(), dateFrom)));	
			return sdto;
		}
		
		return null;
	}

	public Object getSettlementDetailsWithDateF(String merchantId, String dateFrom, String dateTo)
			throws ValidationExceptions, ParseException {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByMerchantID(merchantId);
		//List<IsettlementFilter> listTransactionDetals = new ArrayList<IsettlementFilter>();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.MERCHANT_NOT_FOUND);
		}

		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Settlement Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		
		logger.info(dateTo+ " and "+ dateFrom);
		
		if (dateTo.length() != 0) {
			logger.info(dateTo+ " and "+ dateFrom);
			sdto.getExtraData().put("SettlementDetail", merchantBalanceSheetRepository.getSettlementDateRangeCalc(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo)));	
			return sdto;

		} else {
			logger.info(dateTo+ " and "+ dateFrom+" what ");
			sdto.getExtraData().put("SettlementDetail", merchantBalanceSheetRepository.getSettlementFromCalc(merchantId,
					Utility.convertDatetoMySqlDateFormat(dateFrom)));	
			return sdto;
		}

		// return listTransactionDetals;

		// return listTransactionDetals;
	}

	public List<SettlementDetailsDto> populateBalanceSheet(List<MerchantBalanceSheet> listTransactionDetals) {

		List<SettlementDetailsDto> trdetails = new ArrayList<SettlementDetailsDto>();
		for (MerchantBalanceSheet tr : listTransactionDetals) {
			Float askcomm = (float) 0;
			Float pgcomm = (float) 0;
			Float assocomm = (float) 0;
			Float tax = (float) 0;
			Float settledamt = (float) 0;
			SettlementDetailsDto trd = new SettlementDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setMerchant_order_id(tr.getMerchantOrderId());
			trd.setTrxamount(Float.toString(((float) tr.getAmount() / 100)));
			trd.setOrder_id(tr.getOrderId());
			trd.setPg_status(tr.getPgStatus());
			if (tr.getSettlementStatus() == null) {
				trd.setSettlement_status("PENDING");
			} else {
				trd.setSettlement_status(tr.getSettlementStatus());
			}
			if (tr.getAskCommission() != null) {
				askcomm = Float.valueOf(tr.getAskCommission());
			}
			if (tr.getAssociateCommission() != null) {
				assocomm = Float.valueOf(tr.getAssociateCommission());
			}
			if (tr.getPgCommission() != null) {
				pgcomm = Float.valueOf(tr.getPgCommission());
			}
			Float service_charge = askcomm;
			logger.info("Service Charge" + String.valueOf(service_charge));
			trd.setService_charge(String.valueOf(GeneralUtils.round((float) service_charge / 118, 2)));
			tax = (float) ((float) service_charge / 1.18f) * 0.18f;
			logger.info("Tax Charge" + String.valueOf(tax));
			trd.setTax_calc(String.valueOf(GeneralUtils.round((float) tax / 100, 2)));
			logger.info("Tax Charge" + trd.getTax_calc());
			if (tr.getSettleAmountToMerchant() != null) {
				trd.setSettle_amount_to_merchant(String.valueOf((float) tr.getSettleAmountToMerchant() / 100));
			} else {
				trd.setSettle_amount_to_merchant(String.valueOf(settledamt));
			}
			trd.setSettlement_date(String.valueOf(tr.getSettlementDate()));
			trd.setTr_type(tr.getTrType());

			trdetails.add(trd);
		}

		return trdetails;
	}

	public Object getMerchantTransactionFilterWise(MerchantDetails user, String orderId, String status, int pageNo,
			int pageRecords) throws ValidationExceptions {

		if (orderId == null && status == null) {
			throw new ValidationExceptions(FORM_VALIDATION_FAILED, FormValidationExceptionEnums.FORM_VALIDATION_FAILED);
		}

		Pageable paging = PageRequest.of(pageNo, pageRecords);
		Page<TransactionDetails> pageTuts;

		if (status != null && (orderId == null || orderId == "")) {
			logger.info("Inside Status Block" + status + " merchant Id :: " + user.getMerchantID());
			pageTuts = transactionDetailsRepository.findByMerchantIdAndStatusContaining(user.getMerchantID(), status,
					paging);
			List<TransactionDetails> listTransactionDetails = pageTuts.getContent();
			// List<TransactionDetails> listTransactionDetails =
			// transactionDetailsRepository.findByMerchantIdAndStatus(user.getMerchantID(),
			// status);
			logger.info("Length :: " + listTransactionDetails.size());
			return listTransactionDetails;
		}
		if (orderId != null && (status == null || status == "")) {
			return transactionDetailsRepository.findAllByMerchantOrderIdAndMerchantId(orderId, user.getMerchantID());
		}
		if (orderId != null && status != null) {
			return transactionDetailsRepository.findAllByMerchantOrderIdAndMerchantIdAndStatus(orderId,
					user.getMerchantID(), status);
		}
		return null;
	}

	public Object getMerchantTransactionFilterWise(MerchantDetails user, String orderId, String status, int pageNo,
			int pageRecords, String dateTo, String dateFrom) throws ValidationExceptions, ParseException {

		if(txnParam(orderId)==true) {
		if( orderId.length()<8){
			throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION, FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
		}
		else {
    		if(transactionDetailsRepository.findAllByMerchantOrderId(orderId).isEmpty()) {
    			throw new ValidationExceptions(ORDER_ID_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
    		}
    	}
		}
		
		logger.info("Inside getMerchantTransactionFilterWise merchant Id :: " + user.getMerchantID());
		List<TransactionDetails> listTransactionDetals = new ArrayList<TransactionDetails>();
		if ((status.length() == 0) && (orderId.length() == 0) && (dateTo.length() == 0) && (dateFrom.length() == 0)) {
			logger.info("All null merchant Id :: " + user.getMerchantID());
			return listTransactionDetals;
		} else if ((orderId != null) && (orderId.length() > 2)) {
			logger.info("By OrderID:: " + orderId + " merchant Id :: " + user.getMerchantID());
			listTransactionDetals = transactionDetailsRepository.findAllByMerchantOrderIdAndMerchantId(orderId,
					user.getMerchantID());
		} else if ((orderId.length() == 0) && (dateTo.length() == 0) && (dateFrom.length() == 0)) {
			logger.info("By Status:: " + status + " merchant Id :: " + user.getMerchantID());
			listTransactionDetals = transactionDetailsRepository.findByMerchantIdAndStatus(user.getMerchantID(),
					status);
		} else if ((status.length() == 0) && (dateTo.length() == 0)) {
			logger.info("By FromDate:: " + dateFrom + " merchant Id :: " + user.getMerchantID());
			listTransactionDetals = transactionDetailsRepository.getTransactionDate(user.getMerchantID(),
					Utility.convertDatetoMySqlDateFormat(dateFrom));
		} else if ((status.length() == 0)) {
			logger.info("By FromDate:: " + dateFrom + " and ToDate :: " + dateTo + " merchant Id :: "
					+ user.getMerchantID());
			logger.info("Limit to 50000");
			listTransactionDetals = transactionDetailsRepository.getTransactionDateRange(user.getMerchantID(),
					Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo));
		} else if (((status != null) && (status.length() > 2)) && ((dateFrom != null) && (dateFrom.length() > 2))
				&& (dateTo.length() == 0)) {
			logger.info("By FromDate:: " + dateFrom + " and  status :: " + status + " merchant Id :: "
					+ user.getMerchantID());
			listTransactionDetals = transactionDetailsRepository.getTransactionDateAndStatus(user.getMerchantID(),
					Utility.convertDatetoMySqlDateFormat(dateFrom), status);
		} else if (((status != null) && (status.length() > 2)) && ((dateFrom != null) && (dateFrom.length() > 2))
				&& ((dateTo != null) && (dateTo.length() > 2))) {
			logger.info("By FromDate:: " + dateFrom + " and ToDate :: " + dateTo + " and status :: " + status
					+ " merchant Id :: " + user.getMerchantID());

			listTransactionDetals = transactionDetailsRepository.getTransactionDateRangeAndStatus(user.getMerchantID(),
					Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo),
					status);
		}
		
		if(listTransactionDetals.isEmpty()) {
			throw new ValidationExceptions(MERCHNT_DETAILS_NOT_MAPPED, FormValidationExceptionEnums.INFORMATION_NOT_FOUND);
		}
		
		List<TransactionDetailsDto> trdetails = new ArrayList<TransactionDetailsDto>();
		for (TransactionDetails tr : listTransactionDetals) {
			TransactionDetailsDto trd = new TransactionDetailsDto();
			trd.setMerchantId(tr.getMerchantId());
			trd.setAmount(Integer.toString(tr.getAmount()));
			trd.setPaymentOption(tr.getPaymentOption());
			trd.setOrderID(tr.getOrderID());
			trd.setStatus(tr.getStatus());
			trd.setPaymentMode(tr.getPaymentMode());
			trd.setTxtMsg(tr.getTxtMsg());
			trd.setTransactionTime(tr.getCreated().toString());
			trd.setMerchantOrderId(tr.getMerchantOrderId());
			trd.setMerchantReturnURL(tr.getMerchantReturnURL());
			trd.setOrderNote(tr.getOrderNote());
			// if (tr.getVpaUPI() != null) {
			// 	trd.setVpaUPI(Utility.maskUpiCode(SecurityUtils.decryptSaveData(tr.getVpaUPI()).replace("\u0000", "")));
			// }
			if (tr.getPaymentCode() != null) {
				trd.setWalletOrBankCode(tr.getPaymentCode());
			}
			trdetails.add(trd);
		}
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Transaction Details!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("transactionDetail", trdetails);	
		return sdto;
	}

	public Object curentMonthSettleMentMerchantWise(MerchantDetails user, String dateFrom, String dateTo)
			throws ParseException {
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Current Month Settlement!");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("MerchantDetail", merchantBalanceSheetRepository.getMerchantWiseSettlementDateWise(user.getMerchantID(),
				Utility.convertDatetoMySqlDateFormat(dateFrom), Utility.convertDatetoMySqlDateFormat(dateTo)));	
		return sdto;
	}

	public Object customerWiseTransaction(MerchantDetails user, String dateFrom, String dateTo) throws ParseException {
		logger.info("Details getMerchant " + user.getMerchantID());
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Customer Transaction Details !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("CustomerDetail", transactionDetailsRepository.getTransactionDetailsWithCustomer(user.getMerchantID(),
				Utility.convertDatetoMySqlDateFormat(dateTo), Utility.convertDatetoMySqlDateFormat(dateFrom)));	
		return sdto;

	}
	
	public boolean txnParam(String val) {
    	if( val == null || val.isBlank() || val.isEmpty() || val.trim().contains(" ")) {
			return false;
		}
    	return true;
    }
	
    public SuccessResponseDto refundRequest(String uuid, String orderId, String merchantId) throws ValidationExceptions {
		
    	if(txnParam(orderId)==true) {
    		if( orderId.length()<8){
    			throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION, FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
    		}
    		else {
        		if(transactionDetailsRepository.findAllByMerchantOrderId(orderId).isEmpty()) {
        			throw new ValidationExceptions(ORDER_ID_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
        		}
        	}
    		}
	
	   RefundDetails refundDetail = refundDetailsRepository.getAllRefundByMerchantOrderId(orderId);
	   if (refundDetail != null) {
		throw new ValidationExceptions(REFUND_DETAILS_EXIST, FormValidationExceptionEnums.REFUND_DETAILS_EXIST);
       }
	
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(
				merchantId,
				orderId,
				UserStatus.PENDING.toString()); 
		
		if(merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_INITIATE_FAILED, FormValidationExceptionEnums.REFUND_INITIATE_FAILED);
		}
		
		RefundDetails refundDetails = new RefundDetails();
		refundDetails.setAmount(String.valueOf(merchantBalanceSheet.getAmount()));
		refundDetails.setInitiatedBy(uuid);
		refundDetails.setMerchantId(merchantId);
		refundDetails.setMerchantOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setPaymentCode(merchantBalanceSheet.getPaymentCode());
		refundDetails.setPaymentMode(merchantBalanceSheet.getPaymentMode());
		refundDetails.setPaymentOption(merchantBalanceSheet.getTrType());
		refundDetails.setPgOrderId(merchantBalanceSheet.getPgOrderId());
		refundDetails.setPgStatus(merchantBalanceSheet.getPgStatus());		
		refundDetails.setRefOrderId(merchantBalanceSheet.getMerchantOrderId());
		refundDetails.setStatus(UserStatus.INITIATED.toString());
		refundDetails.setVpaUpi(merchantBalanceSheet.getVpaUPI());
		refundDetails.setUserId(merchantBalanceSheet.getUserId());
		
		merchantBalanceSheet.setSettlementStatus(UserStatus.INITIATED.toString());
		merchantBalanceSheet.setPgStatus("REFUNDED");
		merchantBalanceSheetRepository.save(merchantBalanceSheet);
		
		refundDetails = refundDetailsRepository.save(refundDetails);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Merchant Refund Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundDetails);		
		return sdto;
	}

	public SuccessResponseDto refundRequestUpdate(String uuid , String orderId, String merchantId, String status) throws ValidationExceptions {
		
		if(txnParam(orderId)==true) {
			if( orderId.length()<8){
				throw new ValidationExceptions(MERCHANT_ORDER_ID_VALIDATION, FormValidationExceptionEnums.MERCHANT_ORDER_ID_VALIDATION);
			}
			else {
	    		if(transactionDetailsRepository.findAllByMerchantOrderId(orderId).isEmpty()) {
	    			throw new ValidationExceptions(ORDER_ID_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_ORDER_ID_NOT_FOUND);
	    		}
	    	}
			}
		
		RefundDetails refundDetails = refundDetailsRepository.findByMerchantIdAndMerchantOrderIdAndStatus(merchantId,orderId,UserStatus.INITIATED.toString());
		if(refundDetails == null) {
			throw new ValidationExceptions(REFUND_UPDATE_FAILED, FormValidationExceptionEnums.REFUND_UPDATE_FAILED);
		}
		
		MerchantBalanceSheet merchantBalanceSheet = merchantBalanceSheetRepository.findAllByMerchantIdAndMerchantOrderIdAndSettlementStatus(
				merchantId,
				orderId,
				UserStatus.INITIATED.toString()); 
		if(merchantBalanceSheet == null) {
			throw new ValidationExceptions(REFUND_UPDATE_FAILED, FormValidationExceptionEnums.REFUND_UPDATE_FAILED);
		}
		
		merchantBalanceSheet.setSettlementStatus(UserStatus.CLOSED.toString());
		merchantBalanceSheetRepository.save(merchantBalanceSheet);
		
		refundDetails.setStatus(status);
		refundDetails.setUpdatedBy(uuid);
		
		SuccessResponseDto sdto = new SuccessResponseDto();
		sdto.getMsg().add("Refund Details Updated Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", refundDetailsRepository.save(refundDetails));		
		return sdto;
	}

	public SuccessResponseDto refundDetail(String merchantid) throws ValidationExceptions {
	
		SuccessResponseDto sdto = new SuccessResponseDto();
		
		List<RefundDetails> refund = refundDetailsRepository.findByMerchantId(merchantid);
		List<MerchantRefundDto> list = new ArrayList<MerchantRefundDto>();
		
		for(RefundDetails rd : refund) {
			MerchantRefundDto dto = new MerchantRefundDto();
			dto.setInitiatedBy(rd.getInitiatedBy());
			dto.setMerchantId(rd.getMerchantId());
			dto.setMerchantOrderId(rd.getMerchantOrderId());
			dto.setAmount(rd.getAmount());
			dto.setPaymentCode(rd.getPaymentCode());
			dto.setPaymentMode(rd.getPaymentMode());
			dto.setPaymentOption(rd.getPaymentOption());
			dto.setPgOrderId(rd.getPgOrderId());
			dto.setPgStatus(rd.getPgStatus());
			dto.setPgTrTime(rd.getPgTrTime());
			dto.setStatus(rd.getStatus());
			dto.setRefOrderId(rd.getRefOrderId());
			dto.setRefundMsg(rd.getRefundMsg());
			
			list.add(dto);
		}
		
		sdto.getMsg().add("Request Processed Successfully !");
		sdto.setSuccessCode(SuccessCode.API_SUCCESS);
		sdto.getExtraData().put("refundDetail", list);	
		return sdto;
	}


}
