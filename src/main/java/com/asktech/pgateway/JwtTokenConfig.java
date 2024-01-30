package com.asktech.pgateway;

import java.util.Collections;

import com.asktech.pgateway.security.CustomUserDetails;
import com.asktech.pgateway.security.JwtAuthentication;
import com.asktech.pgateway.security.JwtAuthenticationEntryPoint;
import com.asktech.pgateway.security.JwtAuthenticationTokenFilter;
import com.asktech.pgateway.security.JwtSuccessHandler;
import com.asktech.pgateway.security.RestAuthenticationFailureHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;



@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@ComponentScan
@EnableAutoConfiguration
@Configuration
public class JwtTokenConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthentication authentication;

	@Autowired
	private JwtAuthenticationEntryPoint entryPoint;

	@SuppressWarnings("unused")
	@Autowired
	private final CustomUserDetails customUserDetails;

	public JwtTokenConfig(CustomUserDetails customUserDetails) {
		this.customUserDetails = customUserDetails;
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Collections.singletonList(authentication));
	}

	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilter() {

		JwtAuthenticationTokenFilter filter = new JwtAuthenticationTokenFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(new JwtSuccessHandler());
		filter.setAuthenticationFailureHandler(new RestAuthenticationFailureHandler());
		return filter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, "/api/login").permitAll()
				.antMatchers("*/api/**").hasRole("USER").antMatchers("*/api/**").hasRole("ADMIN").and()
				.exceptionHandling().authenticationEntryPoint(entryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.headers().cacheControl();
		http.headers().frameOptions().deny().contentSecurityPolicy("frame-ancestors 'none'");
		http.headers().xssProtection();
		http.headers().addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy",
				"default-src 'none';base-uri 'none';form-action 'none'; form-action 'self'; base-uri 'self'; img-src 'self'; script-src 'self'; style-src 'self'; object-src 'none'"))
				.addHeaderWriter(new StaticHeadersWriter("X-WebKit-CSP", "default-src 'self'"))
				.addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "'nosniff'"))
				.addHeaderWriter(new StaticHeadersWriter("Referrer-Policy", "'same-origin'"))
				.addHeaderWriter(new StaticHeadersWriter("Strict-Transport-Security",
						"'max-age=63072000; includeSubdomains; preload'"));
	}
}