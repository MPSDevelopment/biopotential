package com.mpsdevelopment.biopotential.server.db.advice;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import java.lang.reflect.Method;

public class LogableAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

	private static final Logger LOGGER = LoggerUtil.getLogger(LogableAdvice.class);

	private long t;

	@Override
	public void before(Method method, Object[] objects, Object o) throws Throwable {
		if (method.isAnnotationPresent(Logable.class)) {
			// LOGGER.info("Method %s Invoked before aop method", method.getName());
			t = System.nanoTime();
		}
	}

	@Override
	public void afterReturning(Object o, Method method, Object[] objects, Object o2) throws Throwable {
		if (method.isAnnotationPresent(Logable.class)) {
			LOGGER.info("Method %s takes %d ns", method.getName(), System.nanoTime() - t);
		}
	}
}
