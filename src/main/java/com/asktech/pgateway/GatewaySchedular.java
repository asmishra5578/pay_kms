package com.asktech.pgateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAutoConfiguration
@EnableScheduling
public class GatewaySchedular {
	static Logger logger = LoggerFactory.getLogger(GatewaySchedular.class);
	public static void main(String[] args) {
		logger.info("Main Application is initiated ");
		SpringApplication.run(GatewaySchedular.class, args);
	}

}
