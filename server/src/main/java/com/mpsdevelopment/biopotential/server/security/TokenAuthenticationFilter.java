package com.mpsdevelopment.biopotential.server.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mpsdevelopment.biopotential.server.db.pojo.Token;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class TokenAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final Logger LOGGER = LoggerUtil.getLogger(TokenAuthenticationFilter.class);

	public TokenAuthenticationFilter() {

		setAuthenticationSuccessHandler((request, response, authentication) -> {
			LOGGER.info("GO SuccessHandler !!!");
			// SecurityContextHolder.getContext().setAuthentication(authentication);
			// authentication.setAuthenticated(false);
			// SecurityContextHolder.getContext().setAuthentication(null);
		});
		setAuthenticationFailureHandler((request, response, authenticationException) -> {
			LOGGER.info("GO FailureHandler !!!");
			response.getOutputStream().print(authenticationException.getMessage());
		});
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("  Start work filter");
		Authentication authResult = null;
		try {
			authResult = attemptAuthentication(request, response);
		} catch (AuthenticationException failed) {
			try {
				unsuccessfulAuthentication(request, response, failed);
			} catch (IOException e) {
				LOGGER.error(" requiresAuthentication %s", e);
			} catch (ServletException e) {
				LOGGER.error(" requiresAuthentication %s", e);
			}
		}
		try {
			successfulAuthentication(request, response, null, authResult);
		} catch (IOException e) {
			LOGGER.error(" requiresAuthentication %s", e);
		} catch (ServletException e) {
			LOGGER.error(" requiresAuthentication %s", e);
		}
		return false;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.info("Start attempt Authentication!!!");
		Cookie[] cookies = request.getCookies();
//		 LOGGER.info("COOKIES = %s ", JsonUtils.getJson(cookies));
		String token = null;
		boolean badCredential = false;

		if (cookies == null) {
			badCredential = true;
		} else {
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (name.equals(Token.COOKIE_KEY_FIELD)) {
					token = cookies[i].getValue();
				}
			}
			if (StringUtils.isBlank(token)) {
				badCredential = true;
			}
		}

		if (badCredential) {
			TokenAuthentication authentication = new TokenAuthentication(null);
			authentication.setAuthenticated(false);
			return authentication;
		}

		TokenAuthentication tokenAuthentication = new TokenAuthentication(token);
		 LOGGER.info("Start creating AUTH ");
		Authentication authentication = getAuthenticationManager().authenticate(tokenAuthentication);
		 LOGGER.info("RETURN ATH = %s ", authentication);
		return authentication;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		super.doFilter(req, res, chain);
		// LOGGER.info("DO filter !!! %s ", chain);
	}

}