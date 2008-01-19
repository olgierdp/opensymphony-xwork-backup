/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import org.easymock.MockControl;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionSupport;

import junit.framework.TestCase;

/**
 * Another test case DefaultWorkflowInterceptor.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class DefaultWorkflowInterceptor2Test extends TestCase {

	public void testValidateXXXThrowsException() throws Exception {
		
		ValidateXXXAction action = new ValidateXXXAction();
		
		MockControl actionProxyControl = MockControl.createControl(ActionProxy.class);
		ActionProxy actionProxy = (ActionProxy) actionProxyControl.getMock();
		actionProxy.getMethod();
		actionProxyControl.expectAndDefaultReturn(null, "execute");
		
		
		MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
		ActionInvocation actionInvocation = (ActionInvocation) actionInvocationControl.getMock();
		actionInvocation.invoke();
		actionInvocationControl.expectAndDefaultReturn(null, "test");
		actionInvocation.getProxy();
		actionInvocationControl.expectAndDefaultReturn(null, actionProxy);
		actionInvocation.getAction();
		actionInvocationControl.expectAndDefaultReturn(null, action);
		
		actionInvocationControl.replay();
		actionProxyControl.replay();
		
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		try {
			interceptor.intercept(actionInvocation);
			fail();
		}
		catch(Exception e) {
			assertTrue(true);
		}
		
		assertTrue(action.isExecuted);
		actionInvocationControl.verify();
		actionProxyControl.verify();
	}
	
	
	public void testValidateDoXXXThowsException() throws Exception {
		ValidateDoXXXAction action = new ValidateDoXXXAction();
		
		MockControl actionProxyControl = MockControl.createControl(ActionProxy.class);
		ActionProxy actionProxy = (ActionProxy) actionProxyControl.getMock();
		actionProxy.getMethod();
		actionProxyControl.expectAndDefaultReturn(null, "execute");
		
		
		MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
		ActionInvocation actionInvocation = (ActionInvocation) actionInvocationControl.getMock();
		actionInvocation.invoke();
		actionInvocationControl.expectAndDefaultReturn(null, "test");
		actionInvocation.getProxy();
		actionInvocationControl.expectAndDefaultReturn(null, actionProxy);
		actionInvocation.getAction();
		actionInvocationControl.expectAndDefaultReturn(null, action);
		
		actionInvocationControl.replay();
		actionProxyControl.replay();
		
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		try {
			interceptor.intercept(actionInvocation);
			fail();
		}
		catch(Exception e) {
			assertTrue(true);
		}
		
		assertTrue(action.isExecuted);
		actionInvocationControl.verify();
		actionProxyControl.verify();
	}
	
	
	public class ValidateXXXAction extends ActionSupport {
		
		private static final long serialVersionUID = 1161896580929473760L;
		
		public boolean isExecuted = false;
		public void validateExecute() throws Exception {
			isExecuted = true;
			throw new Exception("testing");
		}
	}
	
	public class ValidateDoXXXAction extends ActionSupport {
		
		private static final long serialVersionUID = 2923102033415402386L;
		
		public boolean isExecuted = false;
		public void validateDoExecute() throws Exception {
			isExecuted = true;
			throw new Exception ("testing");
		}
	}
}
