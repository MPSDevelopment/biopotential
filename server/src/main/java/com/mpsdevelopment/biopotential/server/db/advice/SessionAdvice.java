package com.mpsdevelopment.biopotential.server.db.advice;

import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

public class SessionAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

	private static final Logger LOGGER = LoggerUtil.getLogger(SessionAdvice.class);

	@Autowired
	private SessionManager sessionManager;

	@Override
	public void before(Method method, Object[] objects, Object o) throws Throwable {
		// LOGGER.info("Invoked before aop method");
		sessionManager.openSession();
	}

	@Override
	public void afterReturning(Object o, Method method, Object[] objects, Object o2) throws Throwable {
		// LOGGER.info("Invoked after aop method");
		sessionManager.closeSession();
	}
}
