
package com.asktech.pgateway.schedular;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.util.CommissionCalculator;

@Component
public class GatewaySchedulingService {

	@Autowired
	CommissionCalculator commissionCalculator;
	@Autowired
	LetzPayTransactionStatusSchedular letzPayTransactionStatusSchedular;
	
	static Logger logger = LoggerFactory.getLogger(GatewaySchedulingService.class);
	
	/*
	@Scheduled(cron="0 0/1 * * * ?")
	public void schedularLetzPayTransaction() throws IOException,  ParseException, NoSuchAlgorithmException {
		logger.info("Schedular will initiate the Processing ....");
		letzPayTransactionStatusSchedular.updateTransactionStatus();
		logger.info("Schedular END for this cycle ....");
	}
	*/
	
	/*
	@Scheduled(cron="0 0/1 * * * ?")
	public void performScheduleOperation() throws IOException,  ParseException {
		logger.info("Schedular will initiate the Processing ....");
		commissionCalculator.scheduleProcessingForCommission();
		logger.info("Schedular END for this cycle ....");
	}
	*/
	/*
	  @Scheduled(cron = "0/50 * * * * ?") 
	  public void performbltest() throws  JsonProcessingException, ParseException 
	  { 
		  m2pProcessService.testBlTr(); 
	  }
	 
	*/
	
	/*
	@Scheduled(cron = "0/10 * * * * ?")
	public void performOperation() throws UserException, ParserConfigurationException, IOException, SAXException {
		List<UserCardRegistration> user = cardRepo.findAll();
		if (!user.isEmpty()) {
			System.out.println("calling........Card");
			service.callYalamanChiliApi(user);
		}else {
			System.out.println("No Data");
		}
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void updateDailyCardsLimit() {
		List<UserCardRegistration> user = cardRepo.findAll();
		if (!user.isEmpty()) {
			Log4jLogger.saveLog("Daily Limit scheduler run==> ");
			service.updateDailyCardsLimits(user);
		}
	}

	@Scheduled(cron = "0/10 * * * * ?")
	public void generateEmbossing() throws UserException {
		List<CardOrderDetails> user = orderRepo.findByembossingFile("NEW");
		if (!user.isEmpty()) {
			service.generateEmbossingFile(user);
		}
	}

	@Scheduled(cron = "0 0/5 * * * ?")
	public void reScheuledTask() throws UserException {
		List<CardUserCardType> user = cardTypeRepo.findByProcessAndCountLessThanAndCardRegion("PENDING", 15, "ADDCARD");
		if (!user.isEmpty()) {
			user.forEach(o -> {
				o.setProcess("NEW");
				o.setCount(o.getCount() + 1);
			});
			cardTypeRepo.saveAll(user);
		}
	}
	*/
}
