/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class InterceptorForTestPurpose implements Interceptor {

	private String paramOne;
	private String paramTwo;
	
	public String getParamOne() { return paramOne; }
	public void setParamOne(String paramOne) { this.paramOne = paramOne; }
	
	public String getParamTwo() { return paramTwo; }
	public void setParamTwo(String paramTwo) { this.paramTwo = paramTwo; }
	
	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		return invocation.invoke();
	}
}
