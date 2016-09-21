package com.mpsdevelopment.biopotential.server.db.advice;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.controller.ControllerAPI;
import com.mpsdevelopment.biopotential.server.controller.UsersController;
import com.mpsdevelopment.biopotential.server.db.DatabaseCreator;
import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class AnnotationSessionAdviceTest {

	@Autowired
	private UsersController usersController;
	
	@Autowired
	private SessionManager sessionManager;
	
	private MockHttpServletRequest request;

	@Before
	public void before() {
		request = new MockHttpServletRequest();
		request.setRequestURI(ControllerAPI.USER_CONTROLLER + ControllerAPI.USER_CONTROLLER_LOGIN);

	}

	@Test
	public void checkAnnotation() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		User adminUser = new User().setLogin(DatabaseCreator.ADMIN_LOGIN).setPassword(DatabaseCreator.ADMIN_PASSWORD);
		usersController.login(request, null, JsonUtils.getJson(adminUser), null);
		
		sessionManager.printStatistics();
	}

}
