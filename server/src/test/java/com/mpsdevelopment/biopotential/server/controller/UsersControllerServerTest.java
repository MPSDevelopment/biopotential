package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.servlet.ServletException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml",
		"classpath:/webapp/web-context.xml" })
@Configurable
public class UsersControllerServerTest {

	private static final Logger LOGGER = LoggerUtil.getLogger(UsersControllerServerTest.class);

	@Autowired
	private ServerSettings serverSettings;

	@Autowired
	private UserDao userDao;

	private static HttpClient client;

	private static final String ENCODING_NAME = "UTF-8";
	private static final String CONTENT_TYPE_NAME = "application/json";

	@BeforeClass
	public static void beforeClass() throws ServletException, InterruptedException {
		JettyServer.getInstance().start();
		// JettyServer.getInstance().join();

		client = new HttpClient();
		client.getParams().setParameter("http.useragent", "Test Client");

	}

	@AfterClass
	public static void afterClass() throws Exception {
		JettyServer.getInstance().stop();

	}

	private HttpStatus postObject(String url, Object object)
			throws URIException, UnsupportedEncodingException, IOException, HttpException {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), url);
		LOGGER.info("Full url is %s", fullUrl);
		PostMethod method = new PostMethod();
		method.setURI(new URI(fullUrl, false, ENCODING_NAME));
		method.setRequestEntity(new StringRequestEntity(JsonUtils.getJson(object), CONTENT_TYPE_NAME, ENCODING_NAME));
		try {
			return HttpStatus.valueOf(client.executeMethod(method));
		} finally {
			method.releaseConnection();
		}
	}

	private HttpStatus get(String url) throws URIException, UnsupportedEncodingException, IOException, HttpException {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), url);
		LOGGER.info("Full url is %s", fullUrl);
		GetMethod method = new GetMethod();
		method.setURI(new URI(fullUrl, false, ENCODING_NAME));
		try {
			return HttpStatus.valueOf(client.executeMethod(method));
		} finally {
			method.releaseConnection();
		}
	}

	@After
	public void after() {
	}

	@Test
	public void checkLogin() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
			SignatureException, IOException, JWTVerifyException {

		assertEquals(HttpStatus.UNAUTHORIZED,
				postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, new User()));

		assertEquals(HttpStatus.BAD_REQUEST,
				postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, null));

		User newUser = new User().setLogin("login").setPassword("password");

		assertEquals(HttpStatus.UNAUTHORIZED,
				postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, newUser));

		adminLogin();

	}

	public void adminLogin() throws URIException, UnsupportedEncodingException, HttpException, IOException {
		User user = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);
		assertEquals(HttpStatus.ACCEPTED,
				postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, user));
	}

	public HttpStatus logout() throws URIException, UnsupportedEncodingException, HttpException, IOException {
		return get(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGOUT);
	}

	@Test
	public void checkLogout() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
			SignatureException, IOException, JWTVerifyException {

		adminLogin();

		assertEquals(HttpStatus.ACCEPTED, logout());

		// second attempt
		assertEquals(HttpStatus.BAD_REQUEST, logout());

	}

	// @Test
	// public void createUser() throws DaoException, InvalidKeyException,
	// NoSuchAlgorithmException, IllegalStateException, SignatureException,
	// IOException, JWTVerifyException {
	//
	// User newUser = new User();
	// newUser.setSurname("surmane").setName("name").setLogin("login").setPassword("password");
	//
	// ResponseEntity<String> createUserResponse =
	// usersController.createUser(request, JsonUtils.getJson(newUser));
	// assertEquals(HttpStatus.UNAUTHORIZED,
	// createUserResponse.getStatusCode());
	//
	// operatorLogin();
	// createUserResponse = usersController.createUser(request,
	// JsonUtils.getJson(newUser));
	// assertEquals(HttpStatus.UNAUTHORIZED,
	// createUserResponse.getStatusCode());
	//
	// adminLogin();
	// createUserResponse = usersController.createUser(request,
	// JsonUtils.getJson(newUser));
	// assertEquals(HttpStatus.CREATED, createUserResponse.getStatusCode());
	//
	//
	// User userFromResponse = JsonUtils.fromJson(User.class,
	// createUserResponse.getBody());
	// assertNotNull(userFromResponse.getId());
	// assertEquals(newUser.getLogin(), userFromResponse.getLogin());
	//
	// userDao.delete(userFromResponse);
	// }
	//
	// @Test
	// public void updateUser() throws DaoException, InvalidKeyException,
	// NoSuchAlgorithmException, IllegalStateException, SignatureException,
	// IOException, JWTVerifyException {
	// User user = new User();
	// user.setSurname("oldSurmane");
	// userDao.save(user);
	//
	// user.setSurname("newSurname");
	//
	// ResponseEntity<String> updateUserResponse =
	// usersController.updateUser(request, JsonUtils.getJson(user));
	// assertEquals(HttpStatus.UNAUTHORIZED,
	// updateUserResponse.getStatusCode());
	//
	// adminLogin();
	// updateUserResponse = usersController.updateUser(request,
	// JsonUtils.getJson(user));
	// assertEquals(HttpStatus.OK, updateUserResponse.getStatusCode());
	//
	// User userFromResponse = JsonUtils.fromJson(User.class,
	// updateUserResponse.getBody());
	//
	// assertEquals("newSurname", userFromResponse.getSurname());
	//
	// userDao.delete(user);
	// }
	//
	// @Test
	// public void deleteUser() throws InvalidKeyException,
	// NoSuchAlgorithmException, IllegalStateException, SignatureException,
	// IOException, JWTVerifyException {
	//
	// User user = new User();
	// userDao.save(user);
	// Long userId = user.getId();
	//
	// assertNotNull(userDao.get(userId));
	//
	// ResponseEntity<String> deleteUserResponse =
	// usersController.deleteUser(request, userId);
	// assertEquals(HttpStatus.UNAUTHORIZED,
	// deleteUserResponse.getStatusCode());
	//
	// adminLogin();
	// deleteUserResponse = usersController.deleteUser(request, userId);
	// assertEquals(HttpStatus.OK, deleteUserResponse.getStatusCode());
	//
	// assertNull(userDao.get(userId));
	//
	// }
	//
	// private ResponseEntity<String> userLogout() {
	// ResponseEntity<String> logoutResponse;
	// logoutResponse = usersController.logout(request, response);
	// LOGGER.info("Response is %s", logoutResponse.getBody());
	// return logoutResponse;
	// }

	// public void adminLogin() {
	// User adminUser = new
	// User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);
	//
	// ResponseEntity<String> adminResponse = usersController.login(request,
	// JsonUtils.getJson(adminUser));
	// LOGGER.info("Response is %s", adminResponse.getBody());
	// assertEquals(HttpStatus.ACCEPTED, adminResponse.getStatusCode());
	// }
	//
	// public void operatorLogin() {
	// User adminUser = new User().setLogin(DatabaseCreator.OPERATOR_LOGIN)
	// .setPassword(DatabaseCreator.OPERATOR_PASSWORD);
	//
	// ResponseEntity<String> adminResponse = usersController.login(request,
	// JsonUtils.getJson(adminUser));
	// LOGGER.info("Response is %s", adminResponse.getBody());
	// assertEquals(HttpStatus.ACCEPTED, adminResponse.getStatusCode());
	// }
}
