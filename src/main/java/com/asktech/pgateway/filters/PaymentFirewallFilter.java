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

import com.asktech.pgateway.util.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter implementation class PayoutMerchantApiFilter
 */
@Component
public class PaymentFirewallFilter implements Filter {
	static Logger logger = LoggerFactory.getLogger(PaymentFirewallFilter.class);


	/**
	 * Default constructor.
	 */
	public PaymentFirewallFilter() {
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
		boolean error = false;
		// String ipaddress = GeneralUtils.getClientIp(req);
		String ipaddress = Utility.getClientIp(req);
		logger.info("Recieved IP::"+ipaddress);
		String headparameters = "";
		Enumeration<String> head = req.getHeaderNames();
		while (head.hasMoreElements()) {
			String param = head.nextElement();
			// System.out.println(param +"::"+ req.getHeader(param));
			headparameters = headparameters + "~" + param + "::" + req.getHeader(param);
			// logger.info(param +"::"+ req.getHeader(param));
		}
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

		String postbody = "";

		String pguri = req.getRequestURI();
		logger.info(pguri);

		logger.info(ipaddress + "|" + req.getRemoteAddr() + "|" + req.getRemoteHost() + "|" + pguri + "|"
				+ req.getHeader(HttpHeaders.REFERER) + "|" + req.getHeader("user-agent") + "|" + req.getHeader("host")
				+ "|" + req.getMethod() + "|" + postbody + "|" + headparameters + "|" + paraParameters + "|"
				+ attrParameters);
		chain.doFilter(request, response);

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {

		// TODO Auto-generated method stub
	}

}
