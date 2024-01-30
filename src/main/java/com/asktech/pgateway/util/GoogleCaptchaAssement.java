package com.asktech.pgateway.util;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.asktech.pgateway.controller.PGGatewayAdminController;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.Event;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis.ClassificationReason;

@Component
public class GoogleCaptchaAssement {
	
	static Logger logger = LoggerFactory.getLogger(PGGatewayAdminController.class);
	
	@Value("${googleCaptcha.projectID}")
	String projectID;
	@Value("${googleCaptcha.recaptchaSiteKey}")
	String recaptchaSiteKey;
	@Value("${googleCaptcha.recaptchaAction}")
	String recaptchaAction;
	
	public boolean verifyToken(String token) throws IOException {
		
		/*
		  String projectID = "cryptic-form-337412"; 
		  String recaptchaSiteKey =  "6Leib_gdAAAAAO1rNIu_JubU6WZTYWRH5K8cU1o9"; 
		  String recaptchaAction =   "homepage";
		 */
		return createAssessment(projectID, recaptchaSiteKey, token);
		
	}
	
	public boolean createAssessment(String projectID, String recaptchaSiteKey, String token) throws IOException {
		logger.info(token);
		try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {
			
			logger.info("Client Created");			
			Event event = Event.newBuilder().setSiteKey(recaptchaSiteKey).setToken(token).build();
			logger.info("Built Event CreateAssignment");			
			CreateAssessmentRequest createAssessmentRequest = CreateAssessmentRequest.newBuilder()
					.setParent(ProjectName.of(projectID).toString())
					.setAssessment(Assessment.newBuilder().setEvent(event).build()).build();
			logger.info("createAssessmentRequest CreateAssignment");
			Assessment response = client.createAssessment(createAssessmentRequest);
			logger.info("Response CreateAssignment");
			
			if (!response.getTokenProperties().getValid()) {
				logger.info("The CreateAssessment call failed because the token was: "
						+ response.getTokenProperties().getInvalidReason().name());
				return false;
			}
			
			float recaptchaScore = response.getRiskAnalysis().getScore();
			
			for (ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
				logger.info(reason.toString());
			}
			
			if(recaptchaScore < 0.5) {
				return false;
			}
			logger.info("The reCAPTCHA score is: " + recaptchaScore);

			return true;
		}
	}
}

