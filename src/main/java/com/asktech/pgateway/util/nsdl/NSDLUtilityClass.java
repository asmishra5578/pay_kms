package com.asktech.pgateway.util.nsdl;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.asktech.pgateway.dto.nsdl.NSDLAuthenticationRequest;
import com.asktech.pgateway.dto.nsdl.NSDLAuthenticationResponse;
import com.asktech.pgateway.dto.nsdl.NSDLStatusEncRequest;
import com.asktech.pgateway.dto.nsdl.NSDLStatusRequest;
import com.asktech.pgateway.dto.nsdl.NSDLStatusResponse;
import com.asktech.pgateway.dto.nsdl.NSDLStatusResponseDecode;
import com.asktech.pgateway.dto.setu.SetuErrorResponse;
import com.asktech.pgateway.model.NSDLTransactionDetails;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.TransactionDetails;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.NSDLTransactionDetailsRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.TransactionDetailsRepository;
import com.asktech.pgateway.repository.UserDetailsRepository;
import com.asktech.pgateway.security.Encryption;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Service
public class NSDLUtilityClass {

	static Logger logger = LoggerFactory.getLogger(NSDLUtilityClass.class);

	@Value("${pgEndPoints.nsdlStatusEndPoint}")
	String nsdlStatusEndPoint;

	@Autowired
	NSDLTransactionDetailsRepository nsdlTransactionDetailsRepository;

	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;
	@Autowired
	TransactionDetailsRepository transactionDetailsRepository;
	@Autowired
	MerchantPGDetailsRepository merchantPGDetailsRepository;
	@Autowired
	UserDetailsRepository userDetailsRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;

	ObjectMapper objectMapper = new ObjectMapper();

	public NSDLAuthenticationResponse populateAuthenticationResponse(
			NSDLAuthenticationRequest nsdlAuthenticationRequest, String encData, String endPoint, String orderId)
			throws JsonProcessingException {

		nsdlAuthenticationRequest.setEncData(encData);

		logger.info("Request JSON :: " + Utility.convertDTO2JsonString(nsdlAuthenticationRequest));

		HttpResponse<NSDLAuthenticationResponse> nsdlAuthenticationResponse = Unirest.post(endPoint)
				.header("Content-Type", "application/json").body(nsdlAuthenticationRequest)
				.asObject(NSDLAuthenticationResponse.class).ifFailure(SetuErrorResponse.class, r -> {
					SetuErrorResponse e = r.getBody();
					try {
						logger.info("NSDL Authentication Request Response Error::" + Utility.convertDTO2JsonString(e));
					} catch (JsonProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

		logger.info(
				"Authentication Response :: " + Utility.convertDTO2JsonString(nsdlAuthenticationResponse.getBody()));

		populateNSDLTransaction(nsdlAuthenticationRequest, nsdlAuthenticationResponse.getBody(), orderId);

		return nsdlAuthenticationResponse.getBody();
	}

	private void populateNSDLTransaction(NSDLAuthenticationRequest nsdlAuthenticationRequest,
			NSDLAuthenticationResponse nsdlAuthenticationResponse, String orderId) {

		NSDLTransactionDetails nsdlTransactionDetails = nsdlTransactionDetailsRepository
				.findByOrderId(nsdlAuthenticationRequest.getOrderId());
		if (nsdlTransactionDetails == null) {
			nsdlTransactionDetails = new NSDLTransactionDetails();
			nsdlTransactionDetails.setAuthEncData(nsdlAuthenticationResponse.getEncData());
			nsdlTransactionDetails.setAuthErrorCode(nsdlAuthenticationResponse.getErrorCode());
			nsdlTransactionDetails.setAuthErrorMessage(nsdlAuthenticationResponse.getErrorMessage());
			nsdlTransactionDetails.setAuthTerminalId(nsdlAuthenticationResponse.getTerminalId());
			nsdlTransactionDetails.setBankId(nsdlAuthenticationResponse.getBankId());
			nsdlTransactionDetails.setMerchantId(nsdlAuthenticationResponse.getMerchantId());
			nsdlTransactionDetails.setOrderId(nsdlAuthenticationResponse.getOrderId());
			nsdlTransactionDetails.setPgId(nsdlAuthenticationResponse.getPgId());

		} else {

		}
		nsdlTransactionDetailsRepository.save(nsdlTransactionDetails);

	}

	public NSDLStatusResponseDecode getUPIStatus(String orderId, NSDLTransactionDetails nsdlTransactionDetails,
			TransactionDetails transactionDetails, PGConfigurationDetails pgConfigurationDetails) throws Exception {

		NSDLStatusResponseDecode nsdlStatusResponseDecode = new NSDLStatusResponseDecode();

		logger.info("transactionDetails :: " + Utility.convertDTO2JsonString(transactionDetails));
		Map<Object, Object> nsdlParams = new LinkedHashMap<>();
		nsdlParams.put("BankId", nsdlTransactionDetails.getBankId());
		nsdlParams.put("MerchantId", nsdlTransactionDetails.getMerchantId());
		nsdlParams.put("TerminalId", nsdlTransactionDetails.getAuthTerminalId());
		nsdlParams.put("OrderId", orderId);
		nsdlParams.put("AccessCode", pgConfigurationDetails.getPgAddInfo3());
		nsdlParams.put("TxnType", "Status");

		String secureHash = DataHashing.getHashValue(nsdlParams, pgConfigurationDetails.getPgSaltKey());

		NSDLStatusEncRequest nsdlStatusEncRequest = new NSDLStatusEncRequest();
		nsdlStatusEncRequest.setSecureHash(secureHash);
		nsdlStatusEncRequest.setAccessCode(pgConfigurationDetails.getPgAddInfo3());
		nsdlStatusEncRequest.setBankId(nsdlTransactionDetails.getBankId());
		nsdlStatusEncRequest.setMerchantId(nsdlTransactionDetails.getMerchantId());
		nsdlStatusEncRequest.setOrderId(orderId);
		nsdlStatusEncRequest.setTerminalId(nsdlTransactionDetails.getAuthTerminalId());
		nsdlStatusEncRequest.setTxnType("Status");
		nsdlStatusEncRequest.setVpa(transactionDetails.getVpaUPI());

		String encData = DataEncryptionDecryption.encrypt(Utility.convertDTO2JsonString(nsdlStatusEncRequest),
				Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));

		NSDLStatusRequest nsdlStatusRequest = new NSDLStatusRequest();
		nsdlStatusRequest.setBankId(nsdlTransactionDetails.getBankId());
		nsdlStatusRequest.setEncData(encData);
		nsdlStatusRequest.setMerchantId(nsdlTransactionDetails.getMerchantId());
		nsdlStatusRequest.setOrderId(orderId);
		nsdlStatusRequest.setTerminalId(nsdlTransactionDetails.getAuthTerminalId());

		// logger.info("Enc Request ::
		// "+Utility.convertDTO2JsonString(nsdlStatusRequest));
		nsdlStatusResponseDecode = populateStatusResponse(nsdlStatusRequest, pgConfigurationDetails);

		// logger.info(Utility.convertDTO2JsonString(nsdlStatusResponseDecode));

		return nsdlStatusResponseDecode;
	}

	private NSDLStatusResponseDecode populateStatusResponse(NSDLStatusRequest nsdlStatusRequest,
			PGConfigurationDetails pgConfigurationDetails)
			throws JsonParseException, JsonMappingException, IOException {

		HttpResponse<NSDLStatusResponse> nsdlStatusResponse = Unirest.post(nsdlStatusEndPoint)
				.header("Content-Type", "application/json").body(nsdlStatusRequest).asObject(NSDLStatusResponse.class)
				.ifFailure(SetuErrorResponse.class, r -> {
					SetuErrorResponse e = r.getBody();
					try {
						logger.info("NSDL Authentication Request Response Error::" + Utility.convertDTO2JsonString(e));
					} catch (JsonProcessingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

		// logger.info("Response :: " + nsdlStatusResponse.getBody());

		String decData = DataEncryptionDecryption.decrypt(nsdlStatusResponse.getBody().getEncData(),
				Encryption.decryptCardNumberOrExpOrCvv(pgConfigurationDetails.getPgSecret()));
		// logger.info("DecData :: " + decData);
		// logger.info("NSDLStatusResponseDecode ::
		// "+Utility.convertDTO2JsonString(nsdlStatusResponseDecode));
		return objectMapper.readValue(decData, NSDLStatusResponseDecode.class);
	}

	public NSDLStatusResponseDecode getUPIDBStatusDetails(String orderId, boolean statusUpd) throws Exception {

		NSDLTransactionDetails nsdlTransactionDetails = nsdlTransactionDetailsRepository.findByOrderId(orderId);
		PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
				.findByPgAppId(nsdlTransactionDetails.getMerchantId());
		TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderID(orderId);

		NSDLStatusResponseDecode nsdlStatusResponseDecode = getUPIStatus(orderId, nsdlTransactionDetails,
				transactionDetails, pgConfigurationDetails);
		logger.info("nsdlStatusResponseDecode" + Utility.convertDTO2JsonString(nsdlStatusResponseDecode));
		if (nsdlStatusResponseDecode.getResponseMessage().contains("Pending")) {
			nsdlStatusResponseDecode.setStatus("PENDING");
		} else {
			if (nsdlStatusResponseDecode.getResponseCode().equals("00")) {
				nsdlStatusResponseDecode.setStatus("SUCCESS");
			} else if (nsdlStatusResponseDecode.getResponseCode().equals("VERPAYOPT02")) {
				nsdlStatusResponseDecode.setStatus(transactionDetails.getStatus());
			} else {
				nsdlStatusResponseDecode.setStatus("FAILED");
			}
		}
		if (statusUpd) {

			transactionDetails.setStatus(nsdlStatusResponseDecode.getStatus());
			transactionDetails.setPgOrderID(nsdlTransactionDetails.getPgId());
			transactionDetails.setTxtMsg(nsdlStatusResponseDecode.getResponseMessage());
			transactionDetails.setTxtPGTime(Utility.populateDbTime());

			nsdlTransactionDetails.setResponseText(nsdlStatusResponseDecode.getResponseMessage());
			nsdlTransactionDetails.setApprovalCode(nsdlStatusResponseDecode.getAccessCode());
			nsdlTransactionDetails.setResponceCode(nsdlStatusResponseDecode.getResponseCode());
			nsdlTransactionDetails.setRetailerOrderId(nsdlStatusResponseDecode.getRetRefNo());

			transactionDetailsRepository.save(transactionDetails);
			nsdlTransactionDetailsRepository.save(nsdlTransactionDetails);

		}

		return nsdlStatusResponseDecode;
	}

}
