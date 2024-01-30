package com.asktech.pgateway.confg;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CORSFilter implements Filter {


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        @SuppressWarnings("unused")
        HttpServletRequest request = (HttpServletRequest) req;
  //      System.out.println(GeneralUtils.getClientIp(request));
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,DELETE,POST");
        response.setHeader("Access-Control-Max-Age", "-1");
        response.setHeader("Access-Control-Allow-Headers", "*");

        chain.doFilter(req, res);

    }

    @Override
    public void init(FilterConfig arg0) {

    }

    @Override
    public void destroy() {

    }

}
