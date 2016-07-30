package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class UsersControllerTest {

	private static final Logger LOGGER = LoggerUtil.getLogger(UsersControllerTest.class);

	@Autowired
	private UsersController usersController;

	@Autowired
	private UserDao userDao;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Before
	public void before() {
		request = new MockHttpServletRequest();
		// request.setServerName("www.example.com");
		request.setRequestURI(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN);
		// request.setQueryString("param1=value1&param");

		response = new MockHttpServletResponse();

	}

	@After
	public void after() {
		userLogout();
	}

	@Test
	public void login() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		ResponseEntity<String> emptyResponse = usersController.login(request, "{}");
		assertEquals(HttpStatus.UNAUTHORIZED, emptyResponse.getStatusCode());

		ResponseEntity<String> nullResponse = usersController.login(request, null);
		assertEquals(HttpStatus.BAD_REQUEST, nullResponse.getStatusCode());

		User newUser = new User().setLogin("login").setPassword("password");

		ResponseEntity<String> loginResponse = usersController.login(request, JsonUtils.getJson(newUser));
		assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());

		adminLogin();

	}

	@Test
	public void logout() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		User adminUser = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);

		ResponseEntity<String> adminResponse = usersController.login(request, JsonUtils.getJson(adminUser));
		LOGGER.info("Response is %s", adminResponse.getBody());
		assertEquals(HttpStatus.ACCEPTED, adminResponse.getStatusCode());

		ResponseEntity<String> logoutResponse = userLogout();
		assertEquals(HttpStatus.ACCEPTED, logoutResponse.getStatusCode());

		// second attempt
		logoutResponse = userLogout();
		assertEquals(HttpStatus.BAD_REQUEST, logoutResponse.getStatusCode());

	}

	@Test
	public void createUser() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		User newUser = new User();
		newUser.setSurname("surmane").setName("name").setLogin("login").setPassword("password");

		ResponseEntity<String> createUserResponse = usersController.createUser(request, JsonUtils.getJson(newUser));
		assertEquals(HttpStatus.UNAUTHORIZED, createUserResponse.getStatusCode());

		adminLogin();
		createUserResponse = usersController.createUser(request, JsonUtils.getJson(newUser));
		assertEquals(HttpStatus.CREATED, createUserResponse.getStatusCode());
		
		
		User userFromResponse = JsonUtils.fromJson(User.class, createUserResponse.getBody());
		assertNotNull(userFromResponse.getId());
		assertEquals(newUser.getLogin(), userFromResponse.getLogin());

		userDao.delete(userFromResponse);
	}

	@Test
	public void updateUser() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		User user = new User();
		user.setSurname("oldSurmane");
		userDao.save(user);

		user.setSurname("newSurname");

		ResponseEntity<String> updateUserResponse = usersController.updateUser(request, JsonUtils.getJson(user));
		assertEquals(HttpStatus.UNAUTHORIZED, updateUserResponse.getStatusCode());

		adminLogin();
		updateUserResponse = usersController.updateUser(request, JsonUtils.getJson(user));
		assertEquals(HttpStatus.OK, updateUserResponse.getStatusCode());

		User userFromResponse = JsonUtils.fromJson(User.class, updateUserResponse.getBody());

		assertEquals("newSurname", userFromResponse.getSurname());

		userDao.delete(user);
	}

	@Test
	public void deleteUser() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		User user = new User();
		userDao.save(user);
		Long userId = user.getId();

		assertNotNull(userDao.get(userId));

		ResponseEntity<String> deleteUserResponse = usersController.deleteUser(request, userId);
		assertEquals(HttpStatus.UNAUTHORIZED, deleteUserResponse.getStatusCode());
		
		adminLogin();
		deleteUserResponse = usersController.deleteUser(request, userId);
		assertEquals(HttpStatus.OK, deleteUserResponse.getStatusCode());
		
		assertNull(userDao.get(userId));

	}

	private ResponseEntity<String> userLogout() {
		ResponseEntity<String> logoutResponse;
		logoutResponse = usersController.logout(request, response);
		LOGGER.info("Response is %s", logoutResponse.getBody());
		return logoutResponse;
	}

	public void adminLogin() {
		User adminUser = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);

		ResponseEntity<String> adminResponse = usersController.login(request, JsonUtils.getJson(adminUser));
		LOGGER.info("Response is %s", adminResponse.getBody());
		assertEquals(HttpStatus.ACCEPTED, adminResponse.getStatusCode());
	}
}
