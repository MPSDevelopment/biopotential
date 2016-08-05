package com.mpsdevelopment.biopotential.server.handler;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import javax.servlet.*;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

	private static final Logger LOGGER = LoggerUtil.getLogger(AuthorizationFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

//		HttpServletResponse httpRequest = (HttpServletResponse) response;
//		httpRequest.setHeader("Access-Control-Allow-Origin", "*");
//		httpRequest.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		httpRequest.setHeader("Access-Control-Max-Age", "3600");
//		httpRequest.setHeader("Access-Control-Allow-Headers", "x-requested-with");

		// HttpServletRequest httpRequest = (HttpServletRequest) request;
		// if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
		// LOGGER.info("The request %s: '%s' is filtered", httpRequest.getMethod(), PathUtils.getUrl(httpRequest));
		// }

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}
