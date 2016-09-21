package com.mpsdevelopment.biopotential.server.utils;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class SecurityUtils {

	private static final Logger LOGGER = LoggerUtil.getLogger(SecurityUtils.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	public ResponseEntity<String> authenticateInSpringSecurity(User user, HttpSession session) {
		try {
			String role = authenticateInSpringSecurityInner(user, session);
			LOGGER.info(String.format("User %s has been logged in. with role = %s", user.getLogin(), role));
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(String.format("No user with login(%s)", user.getLogin()), null, HttpStatus.UNAUTHORIZED);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(String.format("Incorrect login(%s)/password", user.getLogin()), null, HttpStatus.UNAUTHORIZED);
		} catch (NullPointerException e) {
			return new ResponseEntity<>("User is empty", null, HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	private String authenticateInSpringSecurityInner(User user, HttpSession session) throws UsernameNotFoundException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			if (user == null) {
				throw new NullPointerException("User is null");
			}

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());

			// Authenticate the user
			authentication = authenticationManager.authenticate(authRequest);
		} else {
			LOGGER.info("User %s has been authentificated", authentication.getPrincipal());
		}

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authentication);

		// Create a new session and add the security context.
		// session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		return authentication.getAuthorities().iterator().next().toString();
	}

	public ResponseEntity<String> checkAuthorization() {
		Authentication authentication = null;
		try {
			authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null) {
				return new ResponseEntity<>("Not authorized", null, HttpStatus.UNAUTHORIZED);
			}

			String role = authentication.getAuthorities().iterator().next().toString();

			LOGGER.info("Role is %s", role);
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(String.format("No user with login(%s)", authentication == null ? null : authentication.getName()), null, HttpStatus.UNAUTHORIZED);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(String.format("Incorrect login(%s)/password", authentication == null ? null : authentication.getName()), null, HttpStatus.UNAUTHORIZED);
		} catch (NullPointerException e) {
			return new ResponseEntity<String>("User is empty", null, HttpStatus.BAD_REQUEST);
		}
		return null;
	}
}
