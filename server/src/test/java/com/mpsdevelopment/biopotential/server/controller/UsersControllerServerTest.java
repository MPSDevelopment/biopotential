package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.JettyServer;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.pojo.Token.Role;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.settings.ServerSettings;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.apache.commons.
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.print.URIException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class UsersControllerServerTest {

	private static final Logger LOGGER = LoggerUtil.getLogger(UsersControllerServerTest.class);

	@Autowired
	private ServerSettings serverSettings;

	private static HttpClient client;

	private static DefaultHttpClient httpClient;

	private static final String ENCODING_NAME = "UTF-8";
	private static final String CONTENT_TYPE_NAME = "application/json";

	@BeforeClass
	public static void beforeClass() throws ServletException, InterruptedException {
		JettyServer.getInstance().start();

		client = new DefaultHttpClient();
		client.getParams().setParameter("http.useragent", "Test Client");

		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager mgr = client.getConnectionManager();
		HttpParams params = client.getParams();
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		httpClient = new DefaultHttpClient(mgr, params);
		LOGGER.info("DEVICES HTTP CLIENT is %s ", httpClient);

	}

	@AfterClass
	public static void afterClass() throws Exception {
		JettyServer.getInstance().stop();

	}

	/*private HttpStatus postObject(String url, Object object) throws URIException, UnsupportedEncodingException, IOException, HttpException {
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
	}*/

	public String executePutRequest(String uri, String body) {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), uri);
		HttpPut request = new HttpPut(fullUrl);
		String json = null;
		HttpResponse response = null;
		if (StringUtils.isNotBlank(body)) {
			StringEntity entity;
			try {
				entity = new StringEntity(body, ENCODING_NAME);
				entity.setContentType(CONTENT_TYPE_NAME);
				request.setEntity(entity);
				response = httpClient.execute(request);
				json = getContextResponse(response);
				LOGGER.debug(" PUT Request JSON - %s", json);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public String executePostRequest(String uri, String body) {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), uri);
		HttpPost request = new HttpPost(fullUrl);
		String json = null;
		HttpResponse response = null;
		if (StringUtils.isNotBlank(body)) {
			StringEntity entity;
			try {
				entity = new StringEntity(body, ENCODING_NAME);
				entity.setContentType(CONTENT_TYPE_NAME);
				request.setEntity(entity);
				response = httpClient.execute(request);
				json = getContextResponse(response);
				LOGGER.debug(" POST Request JSON - %s", json);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public String executeGetRequest(String uri) {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), uri);
		HttpGet request = new HttpGet(fullUrl);
		String json = null;
		HttpResponse response = null;
		try {
			response = httpClient.execute(request);
			json = getContextResponse(response);
			LOGGER.debug(" New Request JSON - %s", json);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	public String executeDeleteRequest(String uri) {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), uri);
		HttpDelete request = new HttpDelete(fullUrl);
		String json = null;
		HttpResponse response;
		try {
			response = httpClient.execute(request);
			json = getContextResponse(response);
			LOGGER.debug(" Delete responce JSON - %s", json);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	/*private HttpStatus putObject(String url, Object object) throws URIException, UnsupportedEncodingException, IOException, HttpException {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), url);
		LOGGER.info("Full url is %s", fullUrl);
		PutMethod method = new PutMethod();
		method.setURI(new URI(fullUrl, false, ENCODING_NAME));
		method.setRequestEntity(new StringRequestEntity(JsonUtils.getJson(object), CONTENT_TYPE_NAME, ENCODING_NAME));
		try {
			return HttpStatus.valueOf(client.executeMethod(method));
		} finally {
			method.releaseConnection();
		}
	}*/

	/*private HttpStatus deleteObject(String url) throws URIException, UnsupportedEncodingException, IOException, HttpException {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), url);
		LOGGER.info("Full url is %s", fullUrl);
		DeleteMethod method = new DeleteMethod();
		method.setURI(new URI(fullUrl, false, ENCODING_NAME));
		try {
			return HttpStatus.valueOf(client.executeMethod(method));
		} finally {
			method.releaseConnection();
		}
	}*/

	/*private HttpStatus get(String url) throws URIException, UnsupportedEncodingException, IOException, HttpException {
		String fullUrl = String.format("http://%s:%s%s", serverSettings.getHost(), serverSettings.getPort(), url);
		LOGGER.info("Full url is %s", fullUrl);
		GetMethod method = new GetMethod();
		method.setURI(new URI(fullUrl, false, ENCODING_NAME));
		try {
			return HttpStatus.valueOf(client.executeMethod(method));
		} finally {
			method.releaseConnection();
		}
	}*/

	@After
	public void after() {
	}

	/*@Test
	public void checkAuthorithation() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		assertEquals(HttpStatus.UNAUTHORIZED, get(ControllerAPI.USER_CONTROLLER + "/all"));

		User admin = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);
		User operator = new User().setLogin(DatabaseCreator.OPERATOR_LOGIN).setPassword(DatabaseCreator.OPERATOR_PASSWORD);


		postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, operator);

		assertEquals(HttpStatus.FORBIDDEN, get(ControllerAPI.USER_CONTROLLER + "/all"));

		assertEquals(HttpStatus.ACCEPTED, logout());

		postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, admin);

		assertEquals(HttpStatus.OK, get(ControllerAPI.USER_CONTROLLER + "/all"));

		assertEquals(HttpStatus.ACCEPTED, logout());

	}*/

	@Test
	public void checkCreateDeleteUser() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		User admin = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD).setRole(Role.ADMIN.name());
		User operator = new User().setLogin(DatabaseCreator.OPERATOR_LOGIN).setPassword(DatabaseCreator.OPERATOR_PASSWORD).setRole(Role.OPERATOR.name());
		User user = new User().setLogin("user").setPassword("user").setRole(Role.USER.name());
		User user1 = new User().setLogin("user1").setPassword("user1").setRole(Role.USER.name());

		executePostRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, JsonUtils.getJson(operator));

		String createdUser = executePutRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_PUT_CREATE_USER, JsonUtils.getJson(user1));

		LOGGER.info("  Created USER = %s", createdUser);

		user1 = JsonUtils.fromJson(User.class, createdUser);

		LOGGER.info("  Created USER   id = %s", user1.getId());

		executeGetRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGOUT);

		executePostRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, JsonUtils.getJson(user));

		executeDeleteRequest(ControllerAPI.USER_CONTROLLER + "/remove/" + user1.getId());

		executeGetRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGOUT);

		executePostRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, JsonUtils.getJson(admin));

		executeDeleteRequest(ControllerAPI.USER_CONTROLLER + "/remove/" + user1.getId());

		executeGetRequest(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGOUT);
	}

	/*public void adminLogin() throws URIException, UnsupportedEncodingException, HttpException, IOException {
		User user = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD).setRole(Role.ADMIN.name());
		assertEquals(HttpStatus.CREATED, postObject(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN, user));

	}

	public HttpStatus logout() throws URIException, UnsupportedEncodingException, HttpException, IOException {
		return get(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGOUT);
	}

	@Test
	public void checkLogout() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {

		adminLogin();

		assertEquals(HttpStatus.ACCEPTED, logout());

		// second attempt
		assertEquals(HttpStatus.UNAUTHORIZED, logout());

	}*/

	private String getContextResponse(HttpResponse response) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(response.getEntity().getContent(), writer, "UTF-8");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

}
