package com.asktech.pgateway.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.constants.EmailTextConstants;
import com.asktech.pgateway.constants.ErrorValues;
import com.asktech.pgateway.dto.merchant.MerchantKycDetailsRequest;
import com.asktech.pgateway.dto.ticket.ComplaintTypeAndSubTypeResponse;
import com.asktech.pgateway.dto.ticket.TicketCreateRequest;
import com.asktech.pgateway.dto.ticket.TicketDetailsResponse;
import com.asktech.pgateway.dto.ticket.TicketUpdateRequest;
import com.asktech.pgateway.enums.ComplaintStatus;
import com.asktech.pgateway.enums.FormValidationExceptionEnums;
import com.asktech.pgateway.enums.TicketStatus;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.mail.MailIntegration;
import com.asktech.pgateway.model.MerchantDetails;
import com.asktech.pgateway.model.TicketComplaintDetails;
import com.asktech.pgateway.model.TicketComplaintSubType;
import com.asktech.pgateway.model.TicketComplaintType;
import com.asktech.pgateway.model.TicketTransactionDetails;
import com.asktech.pgateway.repository.MerchantDetailsRepository;
import com.asktech.pgateway.repository.TicketComplaintDetailsRepository;
import com.asktech.pgateway.repository.TicketComplaintSubTypeRepository;
import com.asktech.pgateway.repository.TicketComplaintTypeRepository;
import com.asktech.pgateway.repository.TicketTransactionDetailsRepository;
import com.asktech.pgateway.repository.UserAdminDetailsRepository;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketingService implements ErrorValues {

	@Autowired
	TicketComplaintDetailsRepository ticketComplaintDetailsRepository;
	@Autowired
	TicketComplaintSubTypeRepository ticketComplaintSubTypeRepository;
	@Autowired
	TicketComplaintTypeRepository ticketComplaintTypeRepository;
	@Autowired
	TicketTransactionDetailsRepository ticketTransactionDetailsRepository;
	@Autowired
	MerchantDetailsRepository merchantDetailsRepository;
	@Autowired
	UserAdminDetailsRepository userAdminDetailsRepository;
	@Autowired
	MailIntegration sendMail;

	ObjectMapper mapper = new ObjectMapper();

	public List<ComplaintTypeAndSubTypeResponse> getComplaintTypeAndSubTypeCategory(String uuid)
			throws ValidationExceptions {
		List<ComplaintTypeAndSubTypeResponse> resDto = new ArrayList<>();
		List<TicketComplaintType> ticketComplaintType = ticketComplaintTypeRepository.findAll();
		if (ticketComplaintType.isEmpty()) {
			throw new ValidationExceptions(COMPLAINT_TYPE_NOT_EXISTS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_NOT_EXISTS);
		}
		ticketComplaintType.forEach(o -> {
			ComplaintTypeAndSubTypeResponse res = new ComplaintTypeAndSubTypeResponse();
			List<TicketComplaintSubType> ticketComplaintSubType = ticketComplaintSubTypeRepository
					.findBycommType(o.getCommType());
			if (!ticketComplaintSubType.isEmpty()) {
				ticketComplaintSubType.forEach(ob -> {
					res.getTicketComplaintSubType().add(ob.getCommSubType());
				});
			}
			res.setTicketComplaintType(o.getCommType());
			resDto.add(res);
		});
		return resDto;
	}

	public TicketComplaintDetails createTicket(String uuid, TicketCreateRequest request,
			MerchantDetails merchantDetails) throws ParseException, ValidationExceptions {
		TicketComplaintSubType ticketComplaintSubType = ticketComplaintSubTypeRepository
				.findByCommTypeAndCommSubTypeAndStatus(
						request.getComplaintType(), request.getComplaintSubType(), ComplaintStatus.ACTIVE.toString());

		if (ticketComplaintSubType == null) {
			throw new ValidationExceptions(COMPLAINT_TYPE_SUB_TYPE_STATUS,
					FormValidationExceptionEnums.COMPLAINT_TYPE_SUB_TYPE_STATUS);
		}

		TicketComplaintDetails ticketComplaintDetails = new TicketComplaintDetails();
		String commId = "COMM_" + Utility.getRandomId();

		ticketComplaintDetails.setCommCounter(0);
		ticketComplaintDetails.setCommSubType(request.getComplaintSubType());
		ticketComplaintDetails.setCommType(request.getComplaintType());
		ticketComplaintDetails.setComplaintId(commId);
		ticketComplaintDetails.setComplaintText(request.getComplaintText());
		ticketComplaintDetails.setStatus(TicketStatus.OPEN.toString());
		ticketComplaintDetails.setCreatedBy(uuid);
		ticketComplaintDetails.setPendingWith(merchantDetails.getCreatedBy());

		TicketTransactionDetails ticketTransactionDetails = new TicketTransactionDetails();
		ticketTransactionDetails.setComplaintId(ticketComplaintDetails.getComplaintId());
		ticketTransactionDetails.setComplaintText(ticketComplaintDetails.getComplaintText());
		ticketTransactionDetails.setStatus(ticketComplaintDetails.getStatus());
		ticketTransactionDetails.setUpdatedBy(uuid);

		ticketTransactionDetailsRepository.save(ticketTransactionDetails);
		ticketComplaintDetailsRepository.save(ticketComplaintDetails);

		sendMail.sendMailCreateComplaint(merchantDetails.getMerchantEMail(),
				EmailTextConstants.CREATE_COMPLAINT + ticketTransactionDetails.getComplaintId(),
				"Complaint No :: " + ticketTransactionDetails.getComplaintId());

		return ticketComplaintDetailsRepository.findByComplaintId(ticketComplaintDetails.getComplaintId());
	}

	public TicketDetailsResponse updateTicketMerchant(String uuid, TicketUpdateRequest ticketUpdateRequest,
			MerchantDetails merchantDetails) throws ParseException, ValidationExceptions {

		return updateTicket(uuid, ticketUpdateRequest, merchantDetails.getCreatedBy());

	}

	public TicketDetailsResponse updateTicket(String uuid, TicketUpdateRequest request, String pendingWith)
			throws ValidationExceptions {
		if (request.getComplaintId().isEmpty() || request.getStatus().isEmpty()) {
			throw new ValidationExceptions(ALL_FIELDS_MANDATORY, FormValidationExceptionEnums.ALL_FIELDS_MANDATORY);
		}

		TicketComplaintDetails ticketComplaintDetails = ticketComplaintDetailsRepository
				.findByComplaintId(request.getComplaintId());
		if (ticketComplaintDetails == null) {
			throw new ValidationExceptions(COMPLAINT_NOT_FOUND, FormValidationExceptionEnums.COMPLAINT_NOT_FOUND);
		}

		if (ticketComplaintDetails.getStatus().equalsIgnoreCase(TicketStatus.CLOSED.toString())) {
			throw new ValidationExceptions(COMPLAINT_ALREADY_CLOSED,
					FormValidationExceptionEnums.COMPLAINT_ALREADY_CLOSED);
		}

		if (pendingWith == null) {
			ticketComplaintDetails.setPendingWith(ticketComplaintDetails.getCreatedBy());
		} else {
			ticketComplaintDetails.setPendingWith(pendingWith);
		}

		ticketComplaintDetails.setCommCounter(ticketComplaintDetails.getCommCounter() + 1);
		ticketComplaintDetails.setComplaintText(request.getComplaintText());
		ticketComplaintDetails.setStatus(request.getStatus());
		ticketComplaintDetailsRepository.save(ticketComplaintDetails);

		TicketTransactionDetails ticketTransactionDetails = new TicketTransactionDetails();
		ticketTransactionDetails.setComplaintId(ticketComplaintDetails.getComplaintId());
		ticketTransactionDetails.setComplaintText(ticketComplaintDetails.getComplaintText());
		ticketTransactionDetails.setStatus(ticketComplaintDetails.getStatus());
		ticketTransactionDetails.setUpdatedBy(uuid);

		ticketTransactionDetailsRepository.save(ticketTransactionDetails);

		return getTicketResponse(ticketComplaintDetails);
	}

	public TicketDetailsResponse getTicketResponse(TicketComplaintDetails ticketComplaintDetails) {

		TicketDetailsResponse ticketDetailsResponse = new TicketDetailsResponse();
		List<TicketTransactionDetails> listTransactionDetails = ticketTransactionDetailsRepository
				.findAllByComplaintIdOrderByIdAsc(ticketComplaintDetails.getComplaintId());

		ticketDetailsResponse.setCommCounter(ticketComplaintDetails.getCommCounter());
		ticketDetailsResponse.setCommSubType(ticketComplaintDetails.getCommSubType());
		ticketDetailsResponse.setCommType(ticketComplaintDetails.getCommType());
		ticketDetailsResponse.setComplaintId(ticketComplaintDetails.getComplaintId());
		ticketDetailsResponse.setStatus(ticketComplaintDetails.getStatus());
		ticketDetailsResponse.setUpdatedBy(ticketComplaintDetails.getUpdatedBy());
		ticketDetailsResponse.setListTicketTransactionDetails(listTransactionDetails);

		return ticketDetailsResponse;
	}

	public List<TicketComplaintDetails> getTicketDetails(String uuid) {

		return ticketComplaintDetailsRepository.findBycreatedBy(uuid);
	}

    public TicketDetailsResponse getComplaintDetailsUsingComplaintId(String uuid, String complaintId) throws ValidationExceptions {
		TicketComplaintDetails ticketComplaintDetails = ticketComplaintDetailsRepository
				.findByComplaintId(complaintId);
		if (ticketComplaintDetails == null) {
			throw new ValidationExceptions(COMPLAINT_NOT_FOUND, FormValidationExceptionEnums.COMPLAINT_NOT_FOUND);
		}
        return getTicketResponse(ticketComplaintDetails);
    }

}
