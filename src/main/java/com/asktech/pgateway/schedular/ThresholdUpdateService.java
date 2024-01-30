package com.asktech.pgateway.schedular;

import java.util.Calendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asktech.pgateway.enums.UserStatus;
import com.asktech.pgateway.model.MerchantPGServices;
import com.asktech.pgateway.model.PGServiceDetails;
import com.asktech.pgateway.model.PGServiceThresoldCalculation;
import com.asktech.pgateway.repository.MerchantPGServicesRepository;
import com.asktech.pgateway.repository.PGServiceDetailsRepository;
import com.asktech.pgateway.repository.PGServiceThresoldCalculationRepository;

@Service
public class ThresholdUpdateService {

	@Autowired
	PGServiceThresoldCalculationRepository pgServiceThresoldCalculationRepository;
	@Autowired
	PGServiceDetailsRepository pgServiceDetailsRepository;
	@Autowired
	MerchantPGServicesRepository merchantPGServicesRepository;
	
	public void ThresholdUpdate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());		
		int day = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		for(PGServiceThresoldCalculation pgServiceThresoldCalculation :
			pgServiceThresoldCalculationRepository.findAll()) {
			
			// First nullify for Day wise Threshold
			pgServiceThresoldCalculation.setDaywiseAmount(0);
			pgServiceThresoldCalculation.setUpdatedBy("PGREVERTSYSTEM");
			
			if(dayOfWeek==1) { // For Weekly Update 
				pgServiceThresoldCalculation.setWeekwiseAmount(0);
			}
			
			if(day==1) { // For Monthly Update
				pgServiceThresoldCalculation.setMonthwiseAmount(0);
			}
			
			if(day==1 && month == 1) { // For Yearly Update
				pgServiceThresoldCalculation.setYearwiseAmount(0);
			}
			
			if(day==1 && (month == 1 || month == 4 ||month == 7 ||month == 10 )) { // For 3 monthly Update
				pgServiceThresoldCalculation.setMonth3wiseAmount(0);
			}
			if(day==1 && (month == 1 || month == 7 )) { // For 6 monthly Update
				pgServiceThresoldCalculation.setMonth6wiseAmount(0);
			}
			
			pgServiceThresoldCalculationRepository.save(pgServiceThresoldCalculation);
		}	
		updatePgServiceDetails();
	}
	
	public void updatePgServiceDetails() {
		
		for(PGServiceDetails pgServiceDetails : pgServiceDetailsRepository.findAllByStatus("SWITCHOVER")) {
			updateMerchantService(pgServiceDetails);
			pgServiceDetails.setStatus(UserStatus.ACTIVE.toString());
			pgServiceDetails.setUpdatedBy("PGREVERTSYSTEM");
			pgServiceDetailsRepository.save(pgServiceDetails);
		}
	}
	public void updateMerchantService(PGServiceDetails pgServiceDetails) {
		for(MerchantPGServices merchantPGServices : merchantPGServicesRepository.
				findAllByUpdatePgIdAndService(pgServiceDetails.getPgId(),pgServiceDetails.getPgServices())){
			
			merchantPGServices.setPgID(merchantPGServices.getUpdatePgId());
			merchantPGServices.setUpdatePgId(null);
			merchantPGServices.setUpdatedBy("PGREVERTSYSTEM");
			merchantPGServicesRepository.save(merchantPGServices);
		}
	}
}
