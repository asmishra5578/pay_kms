package com.asktech.pgateway.filters;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MerchantApiFilterConfig {
	
	 @Autowired
	 private PayoutMerchantApiFilter payoutMerchantApiFilter;
	 
	@Bean
	public FilterRegistrationBean<PayoutMerchantApiFilter> payoutMerchantFilterConfig() {
		FilterRegistrationBean<PayoutMerchantApiFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(payoutMerchantApiFilter);

        registrationBean.addUrlPatterns("/merchant/*");

        return registrationBean;

    }
}
