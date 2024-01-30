package com.asktech.pgateway.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asktech.pgateway.dto.merchant.MerchantValidationFilterDto;
import com.asktech.pgateway.exception.ValidationExceptions;
import com.asktech.pgateway.service.payout.VerifyUser;
import com.asktech.pgateway.util.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter implementation class PayoutMerchantApiFilter
 */
@Component
public class PayoutMerchantApiFilter implements Filter {
	@Autowired
	VerifyUser verifyUser;
	@Value("${payoutmode}")
	String payoutmode;
	static Logger logger = LoggerFactory.getLogger(PayoutMerchantApiFilter.class);

	/**
	 * Default constructor.
	 */
	public PayoutMerchantApiFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String ipaddress = "";
		if (payoutmode.equals("DEVELOPMENT")) {
			ipaddress = "117.215.148.117";
		} else {
			ipaddress = GeneralUtils.getClientIp(req);
		//	logger.info("Recieved IP Payout::"+ipaddress);
		}
		// String ipaddress="117.215.148.117";

		String merchantid = req.getHeader("merchantid");
		String secret = req.getHeader("sec");
		String paraParameters = "";
		Enumeration<String> para = req.getParameterNames();
		while (para.hasMoreElements()) {
			String param = para.nextElement();
			// System.out.println(param +"::"+ req.getParameter(param));
			paraParameters = paraParameters + "~" + param + "::" + req.getParameter(param);
			// logger.info(param +"::"+ req.getHeader(param));
		}
		String attrParameters = "";
		Enumeration<String> attr = req.getAttributeNames();

		while (attr.hasMoreElements()) {
			String param = attr.nextElement();

			// System.out.println(param +"::"+ req.getAttribute(param));
			attrParameters = attrParameters + "~" + param + "::" + req.getAttribute(param);
			// logger.info(param +"::"+ req.getHeader(param));
		}

		logger.info("RECIEVED IP::" + ipaddress + "|" + merchantid);
		logger.info("CUST REQ::" + paraParameters + "|" + attrParameters);
		try {
			if (verifyUser.checkUser(ipaddress, merchantid, secret)) {
				chain.doFilter(request, response);
			} else {
				res.resetBuffer();
				MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();
				merchantValidationFilterDto.setStatus("404");
				merchantValidationFilterDto.setMsg("Invalid Authorization");
				res.setStatus(200);
				res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
				res.flushBuffer();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg("Invalid Data");
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
		} catch (ValidationExceptions e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg(e.getMessage());
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg("Invalid Data");
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			res.resetBuffer();
			MerchantValidationFilterDto merchantValidationFilterDto = new MerchantValidationFilterDto();

			merchantValidationFilterDto.setStatus("404");
			merchantValidationFilterDto.setMsg("Invalid Data");
			res.setStatus(200);
			res.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			res.getOutputStream().print(new ObjectMapper().writeValueAsString(merchantValidationFilterDto));
			res.flushBuffer();
			e.printStackTrace();
			e.printStackTrace();
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {

		// TODO Auto-generated method stub
	}

}
