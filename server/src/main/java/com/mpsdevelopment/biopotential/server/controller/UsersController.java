package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.PersistUtils;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.db.advice.Adviceable;
import com.mpsdevelopment.biopotential.server.db.advice.ProtectedApi;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.Token;
import com.mpsdevelopment.biopotential.server.db.pojo.Token.Role;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.transfer.LoginMessage;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.biopotential.server.utils.TokenUtils;
import com.mpsdevelopment.biopotential.server.utils.UrlUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.simple.parser.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.List;

@MessageMapping(ControllerAPI.USER_CONTROLLER)
@RequestMapping(ControllerAPI.USER_CONTROLLER)
@Controller
public class UsersController {

	private static final Logger LOGGER = LoggerUtil.getLogger(UsersController.class);
	private static final String UNDEFINED_CLIENT_DATA = "undefined";

	@Autowired
	private ServerSettings serverSettings;

	@Autowired
	private UserDao userDao;

	@Autowired
	private TokenUtils tokenUtils;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PersistUtils persistUtils;

	@Autowired
	private SessionManager sessionManager;

	public UsersController() {
	}

	// @RequestMapping(value = ControllerAPI.USER_CONTROLLER_LOGIN, method = RequestMethod.POST)
	// @Adviceable
	// public ResponseEntity<String> login(HttpServletRequest request, @RequestBody String json) {
	//
	// User user = JsonUtils.fromJson(User.class, json);
	// ResponseEntity<String> response = securityUtils.authenticateInSpringSecurity(user, request.getSession());
	// if (response != null) {
	// return response;
	// }
	// return new ResponseEntity<>(String.format("User %s has been logged in.", user.getLogin()), null, HttpStatus.ACCEPTED);
	// }

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_LOGIN, method = RequestMethod.POST)
	public ResponseEntity<String> login(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = Token.COOKIE_KEY_FIELD, defaultValue = Token.COOKIE_DEFAULT_VALUE) String token, @RequestBody String json) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		User user = JsonUtils.fromJson(User.class, json);
		if (user == null) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		String cookieDomain = UrlUtils.getOriginDomain(request);
		LOGGER.info("Domain is %s", cookieDomain);

		if (StringUtils.isBlank(token)) {

			User databaseUser = checkUserInDB(user.getLogin(), user.getPassword());

			// User databaseUser = userDao.getByLoginAndPassword(user.getLogin(), user.getPassword());
			if (databaseUser == null) {
				LOGGER.info("Cannot find user with login: '%s' and password: '%s'", user.getLogin(), user.getPassword());
				return new ResponseEntity<>(JsonUtils.getJson(new LoginMessage("Login", "Incorrect login/password", null, null)), null, HttpStatus.UNAUTHORIZED);
			}

			Role role = Role.UNKNOWN;
			switch (databaseUser.getRole()) {
			case DatabaseCreator.ADMIN: {
				role = Role.ADMIN;
				break;
			}
			case DatabaseCreator.OPERATOR: {
				role = Role.OPERATOR;
				break;
			}
			case DatabaseCreator.USER: {
				role = Role.USER;
				break;
			}
			}

			token = tokenUtils.createToken(Token.TokenType.USER, databaseUser.getLogin(), role, DateUtils.addDays(new Date(), 1), null);
			setUserTokenCookie(response, token, cookieDomain);
			LOGGER.info(String.format("User %s has been logged in. with role = %s", databaseUser.getLogin(), role));
			return new ResponseEntity<>(JsonUtils.getJson(new LoginMessage("Logout", String.format("User %s has been logged in.", databaseUser.getLogin()), databaseUser.getRole(), databaseUser.getLogin())), null, HttpStatus.CREATED);
		} else {
			LOGGER.debug("Token already exists");
			return new ResponseEntity<>(JsonUtils.getJson(new LoginMessage("Logout", String.format("User %s has been already logged in.", user.getLogin()), tokenUtils.getRoleFromToken(token).toString(), tokenUtils.getUidFromToken(token))), null, HttpStatus.NOT_MODIFIED);
		}
	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_LOGOUT, method = RequestMethod.GET)
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
			setUserTokenCookie(response, null, null);
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

		if (userDao.getByLogin(user.getLogin()) != null) {
			User oldUser = userDao.get(user.getId());
			BeanUtils.copyProperties(user, oldUser);
			userDao.saveOrUpdate(oldUser);

			LOGGER.info("User changed - Id %s Gender %s", user.getId(), user.getGender());
			return new ResponseEntity<String>(JsonUtils.getJson("User with such login already exist"), null, HttpStatus.CONFLICT);
		} else {
			userDao.save(user);
			LOGGER.info("New user Id %s", user.getId());
		}

		return new ResponseEntity<String>(JsonUtils.getJson(user), null, HttpStatus.CREATED);

	}

	@Adviceable
	@ProtectedApi
	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_POST_UPDATE_USER, method = RequestMethod.POST, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> updateUser(HttpServletRequest request, @RequestBody String json) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException, DaoException {

		User user = JsonUtils.fromJson(User.class, json);
		// ResponseEntity<String> response = securityUtils.authenticateInSpringSecurity(user, request.getSession());
		// if (response != null) {
		// return response;
		// }

		Long userID = user.getId();
		User oldUser = userDao.get(userID);
		BeanUtils.copyProperties(user, oldUser);

		userDao.update(oldUser);

		return new ResponseEntity<String>(JsonUtils.getJson(oldUser), null, HttpStatus.OK);

	}

	@Adviceable
	@ProtectedApi
	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_DELETE_USER, method = RequestMethod.DELETE, produces = { ControllerAPI.PRODUCES_JSON })
	public ResponseEntity<String> deleteUser(HttpServletRequest request, @PathVariable(value = "id") Long id) throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		User user = userDao.get(id);

		/*
		 * ResponseEntity<String> response = securityUtils.authenticateInSpringSecurity(user, request.getSession()); if (response != null) { return response; }
		 */

		userDao.delete(user);

		return new ResponseEntity<String>(JsonUtils.getJson(new String("User was successfully deleted")), null, HttpStatus.OK);

	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_GET_ALL, method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<String> getAllUsers() throws ParseException {
        // TODO remove and fix this shit  ------------------
        if (persistUtils.getSessionFactory().isClosed()) {


            while (persistUtils.getSessionFactory().isClosed()) {
                persistUtils.closeSessionFactory();
                SessionFactory sessionFactory = persistUtils.configureSessionFactory();
                Session session = sessionFactory.openSession();
                sessionManager.setSession(session);
            }
        }
        // TODO remove and fix this shit ---------------------
		List<User> users = userDao.getUsers(null, null);
		LOGGER.info("Has been loaded '%s' users", users.size());
		for (User user : users) {

			LOGGER.info("User changed - Id %s Gender %s", user.getId(), user.getGender());
		}

		return new ResponseEntity<>(JsonUtils.getJson(users), null, HttpStatus.OK);
	}

	@RequestMapping(value = "/change/db/", method = RequestMethod.POST, produces = "text/json;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<String> changeDb(@RequestBody String name) {
		persistUtils.closeSessionFactory();
		persistUtils.setConfigurationDatabaseFilename(name);
		SessionFactory sessionFactory = persistUtils.configureSessionFactory();
		Session session = sessionFactory.openSession();
		sessionManager.setSession(session);

		return new ResponseEntity<>("OK", null, HttpStatus.OK);
	}

	@RequestMapping(value = ControllerAPI.USER_CONTROLLER_GET_ALL_USERS_BY_PAGE, method = RequestMethod.GET, produces = "text/json;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<String> getAllUsersByPages(@PathVariable(value = "pageSize") String pageSize, @PathVariable(value = "pageNumber") String pageNumber) throws ParseException {
		Integer pageSizeInt;
		if (pageSize == null || pageSize.isEmpty() || pageSize.equals(UNDEFINED_CLIENT_DATA)) {
			pageSizeInt = null;
		} else {
			pageSizeInt = Integer.parseInt(pageSize);
		}
		Integer pageNumberInt;
		if (pageNumber == null || pageNumber.isEmpty() || pageNumber.equals(UNDEFINED_CLIENT_DATA)) {
			pageNumberInt = null;
		} else {
			pageNumberInt = Integer.parseInt(pageNumber);
		}

		List<User> users = userDao.getUsers(pageSizeInt, pageNumberInt);
		LOGGER.info("Has been loaded '%s' users", users.size());
		return new ResponseEntity<>(JsonUtils.getJson(users), null, HttpStatus.OK);
	}

	// ------------------------------ Methods

	private User checkUserInDB(String login, String password) {
		User databaseUser = userDao.getByLogin(login);
		if (databaseUser != null) {
			boolean ok = passwordEncoder.matches(password, databaseUser.getPassword());
			if (ok) {
				LOGGER.info("  PASSWORD  OK");
				return databaseUser;
			}
		}
		LOGGER.info("  PASSWORD  FAIL");
		return null;
	}

	private void setUserTokenCookie(HttpServletResponse response, String token, String cookieDomain) {
		Cookie cookie = new Cookie(Token.COOKIE_KEY_FIELD, token);
		if (StringUtils.isNotBlank(serverSettings.getCookieHost())) {
			cookie.setDomain(serverSettings.getCookieHost());
		}
		cookie.setPath("/");
		cookie.setMaxAge(serverSettings.getCookieMaxAge());
		LOGGER.debug("Cookie is %s", cookie);
		response.addCookie(cookie);
	}

	private String stopServer(String token) throws IOException, NoSuchAlgorithmException, JWTVerifyException, InvalidKeyException, SignatureException {
		Token.Role role = null;
		try {
			role = tokenUtils.getRoleFromToken(token);
		} catch (Exception e) {
			LOGGER.error("Error while try get role from token", e);
		}
		if (role == null) {
			return "Cannot check user's permission to restart the server";
		}
		if (role.equals(Token.Role.ADMIN)) {
			try {
				JettyServer.getInstance().stop();
			} catch (Exception e) {
				LOGGER.printStackTrace(e);
			}
			System.exit(0);
			return "Restarting... ";
		} else {
			return String.format("User %s does not have permissions to restart the server", tokenUtils.getUidFromToken(token));
		}
	}

}
