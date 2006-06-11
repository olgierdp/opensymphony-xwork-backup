/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionInvocation;

/**
 * 
 * @author Philip Luppens
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class PrefixMethodInvocationUtil {
	
	private static final Log _log = LogFactory.getLog(PrefixMethodInvocationUtil.class);

	public static void invokePrefixMethod(ActionInvocation actionInvocation, String[] prefixes) throws InvocationTargetException, IllegalAccessException {
		Object action = actionInvocation.getAction();
		
		String methodName = actionInvocation.getProxy().getMethod();
		
		if (methodName == null) {
			 // TODO: clean me up
			 /* if null returns (possible according to the docs), 
			 * use the default execute - this should be a static somewhere ?
			 */
			methodName = "execute";
		}
		
		Method method = getPrefixedMethod(prefixes, methodName, action);
		if (method != null) {
			method.invoke(action, new Object[0]);
		}
	}
	
	public static Method getPrefixedMethod(String[] prefixes, String methodName, Object action) {
		assert(prefixes != null);
		String capitalizedMethodName = capitalizeMethodName(methodName);
		for (int a=0; a< prefixes.length; a++) {
			String prefixedMethodName = prefixes[a]+capitalizedMethodName;
			try {
				Method method = action.getClass().getMethod(prefixedMethodName, new Class[0]);
				return method;
			}
			catch(NoSuchMethodException e) {
				// hmm -- OK, try next prefix
				if (_log.isDebugEnabled()) {
					_log.debug("cannot find method ["+prefixedMethodName+"] in action ["+action+"]");
				}
			}
		}
		return null;
	}
	
	public static String capitalizeMethodName(String methodName) {
		assert(methodName != null);
		return methodName = methodName.substring(0, 1).toUpperCase()
							+ methodName.substring(1);
	}
}
