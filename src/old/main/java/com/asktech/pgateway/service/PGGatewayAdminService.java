package com.asktech.pgateway.service;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.admin.MerchantCreateRequest;
import com.asktech.pgateway.dto.admin.MerchantCreateResponse;
import com.asktech.pgateway.dto.merchant.MerchantDashBoardBalance;
import com.asktech.pgateway.dto.merchant.MerchantResponse;
import com.asktech.pgateway.enums.ApprovalStatus;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.PGServices;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.model.CommissionStructure;
import com.asktech.pgateway.model.MerchantBalanceSheet;
import com.asktech.pgateway.model.MerchantBankDetails;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.model.UserDetails;
import com.asktech.pgateway.repository.CommissionStructureRepository;
import com.asktech.pgateway.repository.MerchantBalanceSheetRepository;
import com.asktech.pgateway.repository.MerchantBankDetailsRepository;
import com.asktech.pgateway.repository.MerchantDashBoardBalanceRepository;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGServicesRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.repository.UserDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PGGatewayAdminService implements ErrorValues {

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

	ObjectMapper mapper = new ObjectMapper();

	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminService.class);

	public MerchantCreateResponse createMerchant(String merchantCreate)
			throws ValidationExceptions, IllegalAccessException, NoSuchAlgorithmException {

		MerchantCreateRequest merchantCreateRequest = new MerchantCreateRequest();
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantsDetails = new MerchantDetails();

		try {

			merchantCreateRequest = mapper.readValue(merchantCreate, MerchantCreateRequest.class);

		} catch (Exception e) {
			throw new ValidationExceptions(JSON_PARSE_ISSUE_MERCHANT_REQUEST,
					FormValidationExceptionEnums.JSON_PARSE_EXCEPTION);
		}

		if (hasBlankVariables(merchantCreateRequest)) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}

		merchantsDetails = merchantDetailsRepository.findByMerchantName(merchantCreateRequest.getMerchantName());
		if (merchantsDetails != null) {
			throw new ValidationExceptions(MERCHANT_EXITS, FormValidationExceptionEnums.MERCHANT_ALREADY_EXISTS);
		} else {
			merchantsDetails = new MerchantDetails();
			String appId = Utility.generateAppId();
			String secrecKey = Encryption.genSecretKey();

			merchantsDetails.setMerchantID(String.valueOf(Utility.getMerchantsID()));
			merchantsDetails.setKycStatus(merchantCreateRequest.getKycStatus());
			merchantsDetails.setMerchantEMail(merchantCreateRequest.getEmailId());
			merchantsDetails.setMerchantName(merchantCreateRequest.getMerchantName());
			merchantsDetails.setPhoneNumber(merchantCreateRequest.getPhoneNumber());
			merchantsDetails.setAppID(Encryption.encryptCardNumberOrExpOrCvv(appId));
			merchantsDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvv(secrecKey));
			merchantsDetails.setUuid(UUID.randomUUID().toString());

			merchantDetailsRepository.save(merchantsDetails);

			merchantsDetails = merchantDetailsRepository.findByMerchantName(merchantCreateRequest.getMerchantName());

			merchantCreateResponse.setAppId(appId);
			merchantCreateResponse.setEmailId(merchantsDetails.getMerchantEMail());
			merchantCreateResponse.setKycStatus(merchantsDetails.getKycStatus());
			merchantCreateResponse.setMerchantId(merchantsDetails.getMerchantID());
			merchantCreateResponse.setMerchantName(merchantsDetails.getMerchantName());
			merchantCreateResponse.setPhoneNumber(merchantsDetails.getPhoneNumber());
			merchantCreateResponse.setSecretId(secrecKey);

		}

		return merchantCreateResponse;
	}

	public MerchantResponse merchantView(String uuid) throws ValidationExceptions {
		logger.info("merchantView In this Method.");
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		MerchantResponse merchantResponse = new MerchantResponse();

		merchantResponse.setMerchantAppId(Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getAppID()));
		merchantResponse.setMerchantEmail(merchantDetails.getMerchantEMail());
		merchantResponse.setMerchantKyc(merchantDetails.getKycStatus());
		merchantResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantResponse.setMerchantPhone(merchantDetails.getPhoneNumber());
		merchantResponse.setMerchantSecret(Encryption.decryptCardNumberOrExpOrCvv(merchantDetails.getSecretId()));

		return merchantResponse;
	}

	public static boolean hasBlankVariables(Object obj) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			// Danger!
			String str = (String) field.get(obj);
			if (StringUtils.isEmpty(str)) {
				return true;
			}
		}
		return false;
	}

	public MerchantCreateResponse refreshSecretKey(String uuid) throws ValidationExceptions, NoSuchAlgorithmException {

		logger.info("merchantView In this Method.");
		MerchantCreateResponse merchantCreateResponse = new MerchantCreateResponse();
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		String secrecKey = Encryption.genSecretKey();
		String appId = Utility.generateAppId();

		merchantDetails.setAppID(Encryption.encryptCardNumberOrExpOrCvv(appId));
		merchantDetails.setSecretId(Encryption.encryptCardNumberOrExpOrCvv(secrecKey));
		merchantDetailsRepository.save(merchantDetails);

		merchantCreateResponse.setAppId(appId);
		merchantCreateResponse.setEmailId(merchantDetails.getMerchantEMail());
		merchantCreateResponse.setKycStatus(merchantDetails.getKycStatus());
		merchantCreateResponse.setMerchantId(merchantDetails.getMerchantID());
		merchantCreateResponse.setMerchantName(merchantDetails.getMerchantName());
		merchantCreateResponse.setPhoneNumber(merchantDetails.getPhoneNumber());
		merchantCreateResponse.setSecretId(secrecKey);

		return merchantCreateResponse;
	}

	public List<TransactionDetails> getTransactionDetails(String uuid) throws ValidationExceptions {

		logger.info("merchantView In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<TransactionDetails> transactionDetails = transactionDetailsRepository
				.findAllByMerchantId(merchantDetails.getId());

		return transactionDetails;
	}

	public List<TransactionDetails> getLast3DaysTransaction(String uuid) throws ValidationExceptions {
		logger.info("getLast3DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<TransactionDetails> transactionDetails = transactionDetailsRepository
				.findLast3DaysTransaction(merchantDetails.getMerchantID());

		return transactionDetails;
	}

	public List<MerchantBalanceSheet> getSettleDetailsLat7Days(String uuid) throws ValidationExceptions {
		logger.info("getLast3DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<MerchantBalanceSheet> merchantBalanceSheet = merchantBalanceSheetRepository
				.findLast7DaysSettleTransaction(merchantDetails.getMerchantID());

		return merchantBalanceSheet;
	}

	public List<MerchantBalanceSheet> getUnSettleDetails(String uuid) throws ValidationExceptions {
		logger.info("getLast3DaysTransaction In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<MerchantBalanceSheet> merchantBalanceSheet = merchantBalanceSheetRepository
				.findAllByMerchantIdAndSettlementStatus(merchantDetails.getMerchantID(), "PENDING");

		return merchantBalanceSheet;
	}

	public List<MerchantDashBoardBalance> getDashBoardBalance(String uuid) throws ValidationExceptions {
		logger.info("getDashBoardBalance In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		List<MerchantDashBoardBalance> merchantDashBoardBalance = merchantDashBoardBalanceRepository
				.findDashBoardDetails(merchantDetails.getMerchantID());

		return merchantDashBoardBalance;
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

		merchantBankDetails.setMerchantID(merchantDetails.getMerchantID());
		merchantBankDetails.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails.setCity(merchantBankDetails.getCity());
		merchantBankDetails.setMicrCode(merchantBankDetails.getMicrCode());
		merchantBankDetailsRepository.save(merchantBankDetails);

		return merchantBankDetails;
	}

	public MerchantPGDetails createPGDetails(String merchantPGNme, String merchantPGAppId, String merchantPGSecret,
			String uuid) throws ValidationExceptions {
		logger.info("createPGDetails In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);
		MerchantPGDetails merchantPGDetails = new MerchantPGDetails();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (merchantPGAppId == null) {
			throw new ValidationExceptions(MERCHANT_PG_APP_ID_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_PG_APP_ID_NOT_FOUND);
		}

		if (merchantPGSecret == null) {
			throw new ValidationExceptions(MERCHANT_PG_SECRET_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_PG_SECRET_NOT_FOUND);
		}

		if (merchantPGNme == null) {
			throw new ValidationExceptions(MERCHANT_PG_NAME_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_PG_NAME_NOT_FOUND);
		}

		merchantPGDetails.setMerchantID(merchantDetails.getMerchantID());
		merchantPGDetails.setMerchantPGAppId(merchantPGAppId);
		merchantPGDetails.setMerchantPGNme(merchantPGNme);
		merchantPGDetails.setMerchantPGSecret(merchantPGSecret);
		merchantPGDetails.setStatus(ApprovalStatus.NEW.toString());
		merchantPGDetailsRepository.save(merchantPGDetails);

		return merchantPGDetails;
	}

	public MerchantPGServices createPGServices(String merchantPGNme, String merchantService, String uuid)
			throws ValidationExceptions {
		logger.info("createPGDetails In this Method.");

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);
		MerchantPGServices merchantPGServices = new MerchantPGServices();

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}

		if (merchantService == null) {
			throw new ValidationExceptions(MERCHANT_PG_APP_ID_NOT_FOUND,
					FormValidationExceptionEnums.MERCHANT_PG_APP_ID_NOT_FOUND);
		}

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantPGNme(merchantPGNme);

		merchantPGServices.setMerchantID(merchantDetails.getMerchantID());
		merchantPGServices.setPgID(String.valueOf(merchantPGDetails.getId()));
		merchantPGServices.setService(merchantService);
		merchantPGServices.setStatus(ApprovalStatus.NEW.toString());

		merchantPGServicesRepository.save(merchantPGServices);

		return merchantPGServices;
	}

	public CommissionStructure createCommissionstructure(String merchantPGNme, String merchantService, int pgAmount,
			String pgCommissionType, int askAmount, String askCommissionType, String uuid) throws ValidationExceptions {

		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantPGNme(merchantPGNme);

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		if (merchantService == null) {
			throw new ValidationExceptions(MERCHANT_SERVICE_TYPE, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (pgCommissionType == null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
					FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (pgAmount <= 0) {
			throw new ValidationExceptions(MERCHANT_COMM_AMOUNT, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (askCommissionType == null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
					FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (askAmount <= 0) {
			throw new ValidationExceptions(MERCHANT_COMM_AMOUNT, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (merchantPGDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		CommissionStructure commissionStructure = commissionStructureRepository.findByMerchantIdAndPgIdAndServiceType(
				merchantDetails.getMerchantID(), String.valueOf(merchantPGDetails.getId()), merchantService);

		if (commissionStructure != null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_EXISTS,
					FormValidationExceptionEnums.DUPLICATE_COMMISSON_FOR_MERCHANT);
		}
		logger.info("Before CommissionStructure get repo ");
		commissionStructure = new CommissionStructure();

		commissionStructure.setPgAmount(pgAmount);
		commissionStructure.setPgCommissionType(pgCommissionType);
		commissionStructure.setAskAmount(askAmount);
		commissionStructure.setAskCommissionType(askCommissionType);
		commissionStructure.setMerchantId(merchantDetails.getMerchantID());
		commissionStructure.setPgId(String.valueOf(merchantPGDetails.getId()));
		commissionStructure.setServiceType(merchantService);
		commissionStructure.setStatus(ApprovalStatus.NEW.toString());

		commissionStructureRepository.save(commissionStructure);

		return commissionStructure;
	}

	public CommissionStructure createCommissionstructureAskTech(String merchantPGNme, String merchantService,int pgAmount,
			String pgCommissionType,
			int askAmount, String askCommissionType) throws ValidationExceptions {

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantPGNme(merchantPGNme);

		if (merchantService == null) {
			throw new ValidationExceptions(MERCHANT_SERVICE_TYPE, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (askCommissionType == null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_TYPE,
					FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}

		if (askAmount <= 0) {
			throw new ValidationExceptions(MERCHANT_COMM_AMOUNT, FormValidationExceptionEnums.FORM_VALIDATION_FILED);
		}
		if (merchantPGDetails == null) {
			throw new ValidationExceptions(PG_NOT_PRESENT, FormValidationExceptionEnums.PG_VLIDATION_ERROR);
		}

		CommissionStructure commissionStructure = commissionStructureRepository.checkCommissionAskTech(String.valueOf(merchantPGDetails.getId()), merchantService);

		if (commissionStructure != null) {
			throw new ValidationExceptions(MERCHANT_COMMISSION_EXISTS,
					FormValidationExceptionEnums.DUPLICATE_COMMISSON_FOR_MERCHANT);
		}
		logger.info("Before CommissionStructure get repo ");
		commissionStructure = new CommissionStructure();

		commissionStructure.setAskAmount(askAmount);
		commissionStructure.setAskCommissionType(askCommissionType);
		commissionStructure.setPgAmount(pgAmount);
		commissionStructure.setPgCommissionType(pgCommissionType);
		commissionStructure.setPgId(String.valueOf(merchantPGDetails.getId()));
		commissionStructure.setServiceType(merchantService);
		commissionStructure.setStatus(ApprovalStatus.NEW.toString());

		commissionStructureRepository.save(commissionStructure);

		return commissionStructure;
	}
	
	
	public MerchantBankDetails getBankDetails(String uuid) throws ValidationExceptions {
		
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);		

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		MerchantBankDetails merchantBankDetails = merchantBankDetailsRepository.findByMerchantID(merchantDetails.getMerchantID());
		if (merchantBankDetails == null) {
			throw new ValidationExceptions(BANK_DETAILS_NOT_FOUND, FormValidationExceptionEnums.MERCHANT_BANK_DETILS_NOT_FOUND);
		}
		return merchantBankDetails;
		
	}

	public MerchantBankDetails updateBankDetails(String uuid,MerchantBankDetails merchantBankDetails) throws ValidationExceptions {
		MerchantDetails merchantDetails = merchantDetailsRepository.findByuuid(uuid);		

		if (merchantDetails == null) {
			throw new ValidationExceptions(MERCHNT_NOT_EXISTIS, FormValidationExceptionEnums.EMAIL_ID_NOT_FOUND);
		}
		
		MerchantBankDetails merchantBankDetails2 = merchantBankDetailsRepository.findByMerchantID(merchantDetails.getMerchantID());
		
		merchantBankDetails2.setAccountNo(merchantBankDetails.getAccountNo());
		merchantBankDetails2.setBankIFSCCode(merchantBankDetails.getBankIFSCCode());
		merchantBankDetails2.setBankName(merchantBankDetails.getBankName());
		merchantBankDetails2.setCity(merchantBankDetails.getCity());
		merchantBankDetails2.setMicrCode(merchantBankDetails.getMicrCode());
		
		merchantBankDetailsRepository.save(merchantBankDetails2);
		
		return merchantBankDetails2;
	}

	public UserDetails getUserDetails(String custEmailorPhone) throws ValidationExceptions {
		
		if(custEmailorPhone == null) {
			throw new ValidationExceptions(INPUT_BLANK_VALUE, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}
		
		UserDetails userDetails = userDetailsRepository.findAllByEmailIdOrPhoneNumber(custEmailorPhone,custEmailorPhone);
		
		if(userDetails == null ) {
			throw new ValidationExceptions(USER_NOT_EXISTS, FormValidationExceptionEnums.USER_NOT_FOUND);
		}
		
		return userDetails;
	}

}
