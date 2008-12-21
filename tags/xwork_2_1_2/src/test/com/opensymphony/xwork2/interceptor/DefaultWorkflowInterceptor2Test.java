/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.easymock.MockControl;

import java.util.HashMap;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class DefaultWorkflowInterceptor2Test extends XWorkTestCase {
	
	public void testDefaultResultNameIsReturnedWithBadValidation() throws Exception {
		ValidationFailedAction action = new ValidationFailedAction();
		
		MockControl actionProxyControl = MockControl.createControl(ActionProxy.class);
		ActionProxy actionProxy = (ActionProxy) actionProxyControl.getMock();
		actionProxy.getMethod();
		actionProxyControl.expectAndDefaultReturn(null, "execute");
		
		MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
		ActionInvocation actionInvocation = (ActionInvocation) actionInvocationControl.getMock();
		actionInvocation.getAction();
		actionInvocationControl.expectAndDefaultReturn(null, action);
		actionInvocation.getProxy();
		actionInvocationControl.expectAndDefaultReturn(null, actionProxy);
		
		actionProxyControl.replay();
		actionInvocationControl.replay();
		
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		
		String result = interceptor.intercept(actionInvocation);
		
		assertEquals(result, Action.INPUT);
		
		actionProxyControl.verify();
		actionInvocationControl.verify();
	}
	
	public void testDifferentResultNameCouldBeReturnedWithBadValidation() throws Exception {
		
		ValidationFailedAction action = new ValidationFailedAction();
		
		MockControl actionProxyControl = MockControl.createControl(ActionProxy.class);
		ActionProxy actionProxy = (ActionProxy) actionProxyControl.getMock();
		actionProxy.getMethod();
		actionProxyControl.expectAndDefaultReturn(null, "execute");
		
		MockControl actionInvocationControl = MockControl.createControl(ActionInvocation.class);
		ActionInvocation actionInvocation = (ActionInvocation) actionInvocationControl.getMock();
		actionInvocation.getAction();
		actionInvocationControl.expectAndDefaultReturn(null, action);
		actionInvocation.getProxy();
		actionInvocationControl.expectAndDefaultReturn(null, actionProxy);
		
		actionProxyControl.replay();
		actionInvocationControl.replay();
		
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		interceptor.setInputResultName("error");
		
		String result = interceptor.intercept(actionInvocation);
		
		assertEquals(result, "error");
		
		actionProxyControl.verify();
		actionInvocationControl.verify();
	}
	
	
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
		actionProxy.getActionName();
		actionProxyControl.expectAndDefaultReturn(null, "action");
		
		actionInvocationControl.replay();
		actionProxyControl.replay();
		
		ValidationInterceptor validationInterceptor = create();
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		try {
		        validationInterceptor.intercept(actionInvocation);
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
		actionProxy.getActionName();
                actionProxyControl.expectAndDefaultReturn(null, "action");
		
		actionInvocationControl.replay();
		actionProxyControl.replay();
		
		ValidationInterceptor validationInterceptor = create();
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		try {
		        validationInterceptor.intercept(actionInvocation);
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
	
	protected ValidationInterceptor create() {
        ObjectFactory objectFactory = container.getInstance(ObjectFactory.class);
        return (ValidationInterceptor) objectFactory.buildInterceptor(
                new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), new HashMap());
    }
	
	
	public class ValidationFailedAction extends ActionSupport {
		
		private static final long serialVersionUID = -2618142505271508888L;

		public ValidationFailedAction() {
			addActionError("an action error");
		}
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
