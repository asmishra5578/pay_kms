package com.asktech.pgateway.schedular;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.MerchantPGDetails;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.PGConfigurationDetails;
import com.asktech.pgateway.model.PGServiceDetails;
import com.asktech.pgateway.model.PGServiceThresoldCalculation;
import com.asktech.pgateway.repository.MerchantPGDetailsRepository;
import com.asktech.pgateway.repository.MerchantPGServicesRepository;
import com.asktech.pgateway.repository.PGConfigurationDetailsRepository;
import com.asktech.pgateway.repository.PGServiceDetailsRepository;
import com.asktech.pgateway.repository.PGServiceThresoldCalculationRepository;
import com.asktech.pgateway.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class SwitchPgServiceDynamically {

	@Autowired
	PGServiceThresoldCalculationRepository pgServiceThresoldCalculationRepository;
	@Autowired
	PGServiceDetailsRepository pgServiceDetailsRepository;
	@Autowired
	MerchantPGServicesRepository merchantPGServicesRepository;
	@Autowired
	MerchantPGDetailsRepository merchantPGDetailsRepository;
	@Autowired
	PGConfigurationDetailsRepository pgConfigurationDetailsRepository;

	static Logger logger = LoggerFactory.getLogger(SwitchPgServiceDynamically.class);

	public void checkThresholdLimit() throws JsonProcessingException {

		List<PGServiceDetails> listPGServiceDetails = pgServiceDetailsRepository
				.findAllByStatus(UserStatus.ACTIVE.toString());
		for (PGServiceDetails pgServiceDetails : listPGServiceDetails) {
			PGServiceThresoldCalculation pgServiceThresoldCalculation = pgServiceThresoldCalculationRepository
					.findByPgIdAndServiceType(pgServiceDetails.getPgId(), pgServiceDetails.getPgServices());

			if (pgServiceThresoldCalculation == null) {
				pgServiceThresoldCalculation = new PGServiceThresoldCalculation();
				pgServiceThresoldCalculation.setCreatedBy("SYSTEM");
				pgServiceThresoldCalculation.setDaywiseAmount(0);
				pgServiceThresoldCalculation.setMonth3wiseAmount(0);
				pgServiceThresoldCalculation.setMonth6wiseAmount(0);
				pgServiceThresoldCalculation.setMonthwiseAmount(0);
				pgServiceThresoldCalculation.setPgId(pgServiceDetails.getPgId());
				pgServiceThresoldCalculation.setServiceType(pgServiceDetails.getPgServices());
				pgServiceThresoldCalculation.setWeekwiseAmount(0);
				pgServiceThresoldCalculation.setYearwiseAmount(0);
				pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation);
			}

			if (!checkThresholdStatus(pgServiceDetails, pgServiceThresoldCalculation)) {
				PGServiceDetails pgServiceDetailsSwitchOver = getNextEligiblePGService(pgServiceDetails);

				logger.info("pgServiceDetailsSwitchOver :: "+Utility.convertDTO2JsonString(pgServiceDetailsSwitchOver));
				if (pgServiceDetailsSwitchOver != null) {
					switchOverProcessor(pgServiceDetails,pgServiceDetailsSwitchOver);
				}
			}
		}
	}

	public void switchOverProcessor(PGServiceDetails pgServiceDetails, PGServiceDetails pgServiceDetailsSwitchOver) {

		for (MerchantPGServices merchantPGServices : merchantPGServicesRepository.findAllByPgIDAndServiceAndStatus(
				pgServiceDetails.getPgId(), pgServiceDetails.getPgServices(), pgServiceDetails.getStatus())) {

			updateMerchantPgDetails(merchantPGServices, pgServiceDetailsSwitchOver);
			updateMerchantPgService(merchantPGServices, pgServiceDetailsSwitchOver);			
		}
		updatePGServiceDetails(pgServiceDetails);
	}

	public void updatePGServiceDetails(PGServiceDetails pgServiceDetails) {
		pgServiceDetails.setStatus("SWITCHOVER");
		pgServiceDetailsRepository.save(pgServiceDetails);
	}
	
	public void updateMerchantPgService(MerchantPGServices merchantPGServices,
			PGServiceDetails pgServiceDetailsSwitchOver) {

		merchantPGServices.setUpdatePgId(merchantPGServices.getPgID());
		merchantPGServices.setPgID(pgServiceDetailsSwitchOver.getPgId());
		merchantPGServices.setUpdatedBy("PGSWITCHSYSTEM");
		merchantPGServicesRepository.save(merchantPGServices);
	}

	public void updateMerchantPgDetails(MerchantPGServices merchantPGServices,
			PGServiceDetails pgServiceDetailsSwitchOver) {

		MerchantPGDetails merchantPGDetails = merchantPGDetailsRepository.findByMerchantIDAndMerchantPGId(
				merchantPGServices.getMerchantID(), pgServiceDetailsSwitchOver.getPgId());
		if (merchantPGDetails == null) {

			PGConfigurationDetails pgConfigurationDetails = pgConfigurationDetailsRepository
					.findByPgUuid(pgServiceDetailsSwitchOver.getPgId());

			merchantPGDetails = new MerchantPGDetails();
			merchantPGDetails.setCreatedBy("PGSWITCHSYSTEM");
			merchantPGDetails.setMerchantID(merchantPGServices.getMerchantID());
			merchantPGDetails.setMerchantPGAppId(pgConfigurationDetails.getPgAppId());
			merchantPGDetails.setMerchantPGName(pgConfigurationDetails.getPgName());
			merchantPGDetails.setMerchantPGSaltKey(pgConfigurationDetails.getPgSaltKey());
			merchantPGDetails.setMerchantPGSecret(pgConfigurationDetails.getPgSecret());
			merchantPGDetails.setReason("SwitchOverDueToThresholdBreeze");
			merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
			merchantPGDetails.setUpdatedBy("PGSWITCHSYSTEM");
			merchantPGDetailsRepository.save(merchantPGDetails);

		} else {
			if (!merchantPGDetails.getStatus().equalsIgnoreCase(UserStatus.ACTIVE.toString())) {
				merchantPGDetails.setStatus(UserStatus.ACTIVE.toString());
				merchantPGDetails.setUpdatedBy("PGSWITCHSYSTEM");
				merchantPGDetailsRepository.save(merchantPGDetails);
			}
		}

	}

	public PGServiceDetails getNextEligiblePGService(PGServiceDetails pgServiceDetails) {

		PGServiceDetails pgServiceDetailsSwitchOver = pgServiceDetailsRepository
				.getNextServices(pgServiceDetails.getPgServices(), pgServiceDetails.getPgId());

		if (pgServiceDetailsSwitchOver == null) {
			logger.error("No Active PgServices found .. , Please escalate to Admin and Customer.");
			return null;
		}

		return pgServiceDetailsSwitchOver;
	}

	public boolean checkThresholdStatus(PGServiceDetails pgServiceDetails,
			PGServiceThresoldCalculation pgServiceThresoldCalculation) throws JsonProcessingException {
		
		logger.info("Input pgServiceDetails() :: " + Utility.convertDTO2JsonString(pgServiceDetails));
		if((pgServiceDetails.getThresoldDay() != 0) && 
				((pgServiceDetails.getThresoldDay() * .99) < pgServiceThresoldCalculation.getDaywiseAmount())) {
			
			logger.info("inside pgServiceDetails.getThresoldDay() != 0");
			return  false;
		}
		
		if ((pgServiceDetails.getThresoldWeek() != 0) && 
				(pgServiceDetails.getThresoldWeek() * .99) < pgServiceThresoldCalculation.getWeekwiseAmount()) {
			logger.info("pgServiceDetails.getThresoldWeek() != 0");
			return  false;
		}
		if ((pgServiceDetails.getThresoldMonth() != 0) && 
				(pgServiceDetails.getThresoldMonth() * .99) < pgServiceThresoldCalculation.getMonthwiseAmount()) {
			logger.info("pgServiceDetails.getThresoldMonth() != 0");
			return  false;
		}
		if ((pgServiceDetails.getThresold3Month() != 0) && 
				(pgServiceDetails.getThresold3Month() * .99) < pgServiceThresoldCalculation.getMonth3wiseAmount()) {
			logger.info("pgServiceDetails.getThresold3Month() != 0");
			return  false;
		}
		if ((pgServiceDetails.getThresold6Month() != 0) && 
				(pgServiceDetails.getThresold6Month() * .99) < pgServiceThresoldCalculation.getMonth6wiseAmount()) {
			logger.info("pgServiceDetails.getThresold6Month() != 0");
			return  false;
		}
		if ((pgServiceDetails.getThresoldYear() != 0) && 
				(pgServiceDetails.getThresoldYear() * .99) < pgServiceThresoldCalculation.getYearwiseAmount()) {
			logger.info("pgServiceDetails.getThresoldYear() != 0");
			return  false;
		}
		
		logger.info("Output pgServiceDetails() :: " + Utility.convertDTO2JsonString(pgServiceDetails));
		return true;
	}
}
