package com.mpsdevelopment.biopotential.server.controller;

import com.auth0.jwt.JWTVerifyException;
import com.mpsdevelopment.biopotential.server.db.dao.DaoException;
import com.mpsdevelopment.biopotential.server.db.dao.UserDao;
import com.mpsdevelopment.biopotential.server.db.pojo.User;
import com.mpsdevelopment.biopotential.server.utils.JsonUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class UsersControllerTest {

	@Autowired
	private UsersController usersController;

	@Autowired
	private UserDao userDao;

	@Test
	public void createUser() throws DaoException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, SignatureException, IOException, JWTVerifyException {
		User newUser = new User();
		newUser.setSurname("surmane").setName("name").setLogin("login").setPassword("password");

		ResponseEntity<String> createUserResponse = usersController.createUser(JsonUtils.getJson(newUser));
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

		ResponseEntity<String> updateUserResponse = usersController.updateUser(JsonUtils.getJson(user));

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

		ResponseEntity<String> deleteUserResponse = usersController.deleteUser(userId);

		assertEquals(HttpStatus.OK, deleteUserResponse.getStatusCode());

		assertNull(userDao.get(userId));

	}
}
