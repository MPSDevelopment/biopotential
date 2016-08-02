package com.mpsdevelopment.biopotential.server.db.advice;

import com.mpsdevelopment.biopotential.server.utils.SecurityUtils;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class ProtectedApiSessionAdvice implements MethodInterceptor {
	// MethodBeforeAdvice, AfterReturningAdvice,

	private static final Logger LOGGER = LoggerUtil.getLogger(ProtectedApiSessionAdvice.class);

	@Autowired
	private SecurityUtils securityUtils;

	// @Override
	// public void before(Method method, Object[] args, Object o) throws
	// Throwable {
	// if (method.isAnnotationPresent(ProtectedApi.class)) {
	// LOGGER.info("Method %s Invoked before aop method", method.getName());
	// if (args[0] instanceof HttpServletRequest) {
	//
	// } else {
	// throw new IncorrectProtectedApiMethodException("There is no servlet
	// request as a first parameter");
	// }
	// }
	// }
	//
	// @Override
	// public void afterReturning(Object o, Method method, Object[] args, Object
	// o2) throws Throwable {
	// if (method.isAnnotationPresent(ProtectedApi.class)) {
	// LOGGER.info("Method %s Invoked after aop method", method.getName());
	//
	// }
	// }

	// @Override
	// public Object intercept(Object o, Method method, Object[] args,
	// MethodProxy methodInvocation) throws Throwable {
	//
	// if (method.isAnnotationPresent(ProtectedApi.class)) {
	// LOGGER.info("Method %s Invoked around aop method", method.getName());
	// if (args[0] instanceof HttpServletRequest) {
	// return methodInvocation.invoke(o, args);
	// } else {
	// throw new IncorrectProtectedApiMethodException("There is no servlet
	// request as a first parameter");
	// }
	// }
	//
	// return methodInvocation.invoke(o, args);
	// }

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (invocation.getMethod().isAnnotationPresent(ProtectedApi.class)) {
			LOGGER.info("Method %s Invoked around aop method", invocation.getMethod().getName());
//			if (invocation.getArguments()[0] instanceof HttpServletRequest) {

				ResponseEntity<String> checkAuthorizationResponseEntity = securityUtils.checkAuthorization();
				if (checkAuthorizationResponseEntity != null) {
					return checkAuthorizationResponseEntity;
				}
				else {
					return invocation.proceed();
				}
				
//			} else {
//				throw new IncorrectProtectedApiMethodException("There is no servlet request as a first parameter in method ", invocation.getMethod().getName());
//			}
		}
		return invocation.proceed();
	}

}
