package com.mpsdevelopment.biopotential.server.db.advice;

import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@Aspect
public class AspectAnnotationSessionAdvice {

	private static final Logger LOGGER = LoggerUtil.getLogger(AspectAnnotationSessionAdvice.class);

	// @Autowired
	// private SessionManager sessionManager;

	 @Before("@annotation(com.mpsdevelopment.biopotential.server.db.advice.Adviceable)")
//	@Before("execution(public * login)")
	public void before() throws Throwable {
		LOGGER.info("Invoked before annotated aop method");
		// sessionManager.openSession();
	}

	 @After("@annotation(com.mpsdevelopment.biopotential.server.db.advice.Adviceable)")
//	@After("execution(public * login)")
	public void afterReturning() throws Throwable {
		LOGGER.info("Invoked after annotated aop method");
		// sessionManager.closeSession();
	}
}
