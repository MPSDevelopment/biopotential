package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.db.advice.Adviceable;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@MessageMapping("/api/users")
@Controller
public class UsersController {

	private static final Logger LOGGER = LoggerUtil.getLogger(UsersController.class);

	@Autowired
	private ServerSettings serverSettings;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthenticationManager authenticationManager;

	public UsersController() {
	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_LOGIN, method = RequestMethod.POST)
	@Adviceable
	public ResponseEntity<String> login(HttpServletRequest request, @RequestBody String json) {

		User user = JsonUtils.fromJson(User.class, json);
		ResponseEntity<String> response = authenticateInSpringSecurity(user, request.getSession());
		if (response != null) {
			return response;
		}
		return new ResponseEntity<>(String.format("User %s has been logged in.", user.getLogin()), null, HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_LOGOUT, method = RequestMethod.GET)
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		} else {
			return new ResponseEntity<>("User was not logged in", null, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:/login?logout";// You can redirect wherever you want, but generally it's a good practice to show login screen again.
		return new ResponseEntity<>("User has been logged out.", null, HttpStatus.ACCEPTED);
	}

	@Adviceable
	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, method = RequestMethod.PUT, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> createUser(HttpServletRequest request, @RequestBody String json) {

		User user = JsonUtils.fromJson(User.class, json);

		ResponseEntity<String> response = authenticateInSpringSecurity(user, request.getSession());
		if (response != null) {
			return response;
		}

		if (userDao.getByLogin(user.getLogin()) != null) {
			return new ResponseEntity<String>(JsonUtils.getJson("User with such login already exist"), null, HttpStatus.CONFLICT);
		}

		userDao.save(user);

		return new ResponseEntity<String>(JsonUtils.getJson(user), null, HttpStatus.CREATED);

	}

	@Adviceable
	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_POST_UPDATE_USER, method = RequestMethod.POST, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> updateUser(HttpServletRequest request, @RequestBody String json)
			throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException, DaoException {

		User user = JsonUtils.fromJson(User.class, json);
		ResponseEntity<String> response = authenticateInSpringSecurity(user, request.getSession());
		if (response != null) {
			return response;
		}

		Long userID = user.getId();
		User oldUser = userDao.get(userID);
		BeanUtils.copyProperties(user, oldUser);

		userDao.update(oldUser);

		return new ResponseEntity<String>(JsonUtils.getJson(oldUser), null, HttpStatus.OK);

	}

	@Adviceable
	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_DELETE_USER, method = RequestMethod.DELETE, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> deleteUser(HttpServletRequest request, @PathVariable(value = "id") Long id)
			throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		User user = userDao.get(id);

		ResponseEntity<String> response = authenticateInSpringSecurity(user, request.getSession());
		if (response != null) {
			return response;
		}

		userDao.delete(user);

		return new ResponseEntity<String>(JsonUtils.getJson(new String("User was successfully deleted")), null, HttpStatus.OK);

	}

	private ResponseEntity<String> authenticateInSpringSecurity(User user, HttpSession session) {
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
}
