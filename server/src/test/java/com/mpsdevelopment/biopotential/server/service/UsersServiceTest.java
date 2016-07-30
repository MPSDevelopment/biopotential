package com.mpsdevelopment.biopotential.server.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mpsdevelopment.biopotential.server.db.SessionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = { "classpath:/webapp/app-context-test.xml", "classpath:/webapp/web-context.xml" })
@Configurable
public class UsersServiceTest {

	@Autowired
	private SessionManager sessionManager;

	@Test
	public void check() throws InterruptedException {
		Thread.sleep(20000);
		Assert.assertTrue(sessionManager.getOpenedSessions() - sessionManager.getClosedSessions() <= 2);
	}
}
