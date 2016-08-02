package com.mpsdevelopment.biopotential.server.db.advice;

import com.mpsdevelopment.biopotential.server.db.SessionManager;
import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;


public class ProtectedApiSessionAdvice implements MethodInterceptor {
// MethodBeforeAdvice, AfterReturningAdvice, 
	
	private static final Logger LOGGER = LoggerUtil.getLogger(ProtectedApiSessionAdvice.class);

//	@Override
//	public void before(Method method, Object[] args, Object o) throws Throwable {
//		if (method.isAnnotationPresent(ProtectedApi.class)) {
//			LOGGER.info("Method %s Invoked before aop method", method.getName());
//			if (args[0] instanceof HttpServletRequest) {
//
//			} else {
//				throw new IncorrectProtectedApiMethodException("There is no servlet request as a first parameter");
//			}
//		}
//	}
//
//	@Override
//	public void afterReturning(Object o, Method method, Object[] args, Object o2) throws Throwable {
//		if (method.isAnnotationPresent(ProtectedApi.class)) {
//			LOGGER.info("Method %s Invoked after aop method", method.getName());
//
//		}
//	}

	@Override
	public Object intercept(Object o, Method method, Object[] args, MethodProxy methodInvocation) throws Throwable {
		
		if (method.isAnnotationPresent(ProtectedApi.class)) {
			LOGGER.info("Method %s Invoked around aop method", method.getName());
			if (args[0] instanceof HttpServletRequest) {
				return methodInvocation.invoke(o, args);
			} else {
				throw new IncorrectProtectedApiMethodException("There is no servlet request as a first parameter");
			}	
		}
		
		return methodInvocation.invoke(o, args);
	}
	
	
}
