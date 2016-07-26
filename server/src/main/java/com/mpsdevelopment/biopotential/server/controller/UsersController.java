package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

@MessageMapping("/api/users")
@Controller
public class UsersController {

	private static final Logger LOGGER = LoggerUtil.getLogger(UsersController.class);

	private ServerSettings serverSettings;
	private UserDao userDao;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	public UsersController(ServerSettings serverSettings, UserDao userDao) {
		this.serverSettings = serverSettings;
		this.userDao = userDao;
	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_POST_LOGIN, method = RequestMethod.POST)
	public ResponseEntity<String> login(HttpServletRequest request, @RequestBody String json) {

		User user = JsonUtils.fromJson(User.class, json);
		if (user == null) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		String role = "";
		try {
			role = authenticateInSpringSecurity(user, request.getSession());
		} catch (UsernameNotFoundException e) {
			return new ResponseEntity<>(String.format("No user with login(%s)", user.getLogin()), null, HttpStatus.UNAUTHORIZED);
		} catch ( BadCredentialsException e) {
			return new ResponseEntity<>(String.format("Incorrect login(%s)/password", user.getLogin()), null, HttpStatus.UNAUTHORIZED);
		}

		LOGGER.info(String.format("User %s has been logged in. with role = %s", user.getLogin(), role));
		return new ResponseEntity<>(String.format("User %s has been logged in.", user.getLogin()), null, HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, method = RequestMethod.PUT, produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<String> createUser(@RequestBody String json) {

		User user = JsonUtils.fromJson(User.class, json);
		if (user == null) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		if (userDao.getByLogin(user.getLogin()) != null) {
			return new ResponseEntity<String>(JsonUtils.getJson("User with such login already exist"), null, HttpStatus.CONFLICT);
		}

		userDao.save(user);

		return new ResponseEntity<String>(JsonUtils.getJson(user), null, HttpStatus.CREATED);

	}

	private String authenticateInSpringSecurity(User user, HttpSession session) throws UsernameNotFoundException {
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());

		// Authenticate the user
		Authentication authentication = authenticationManager.authenticate(authRequest);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authentication);

		// Create a new session and add the security context.
//		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		return authentication.getAuthorities().iterator().next().toString();
	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_POST_UPDATE_USER, method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<String> updateUser(@RequestBody String json) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException, DaoException {

		User updatedUser = JsonUtils.fromJson(User.class, json);
		if (updatedUser == null) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		Long userID = updatedUser.getId();
		User oldUser = userDao.get(userID);
		BeanUtils.copyProperties(updatedUser, oldUser);

		userDao.update(oldUser);

		return new ResponseEntity<String>(JsonUtils.getJson(oldUser), null, HttpStatus.OK);

	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_DELETE_USER, method = RequestMethod.DELETE, produces = { "application/json; charset=UTF-8" })
	public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		User user = userDao.get(id);
		if (user == null) {
			return new ResponseEntity<String>(JsonUtils.getJson(new String("There is no user with such id")), null, HttpStatus.BAD_REQUEST);
		}
		userDao.delete(user);

		return new ResponseEntity<String>(JsonUtils.getJson(new String("User was successfully deleted")), null, HttpStatus.OK);

	}
}
