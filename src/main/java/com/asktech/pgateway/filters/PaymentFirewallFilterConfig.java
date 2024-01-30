package com.asktech.pgateway.filters;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentFirewallFilterConfig {
	
	 @Autowired
	 private PaymentFirewallFilter PaymentFirewallFilter;
	 
	@Bean
	public FilterRegistrationBean<PaymentFirewallFilter> PaymentFirewallFilterConfig() {
		FilterRegistrationBean<PaymentFirewallFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(PaymentFirewallFilter);

        registrationBean.addUrlPatterns("/*");

        return registrationBean;

    }
}
