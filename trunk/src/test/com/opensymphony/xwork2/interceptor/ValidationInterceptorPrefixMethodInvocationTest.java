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
 * Test ValidationInterceptor's prefix method invocation capabilities.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ValidationInterceptorPrefixMethodInvocationTest extends XWorkTestCase {

	public void testPrefixMethodInvocation1() throws Exception {
		
		MockControl controlAction = MockControl.createControl(ValidateAction.class);
		ValidateAction mockAction = (ValidateAction) controlAction.getMock();
		mockAction.validateDoSave();
		controlAction.setVoidCallable(1);
		mockAction.validate();
		controlAction.setVoidCallable();
		
		MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
		ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
		mockActionProxy.getMethod();
		controlActionProxy.setDefaultReturnValue("save");
		mockActionProxy.getActionName();
		controlActionProxy.setDefaultReturnValue("something");
		
		MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
		ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
		mockActionInvocation.getAction();
		controlActionInvocation.setDefaultReturnValue(mockAction);
		mockActionInvocation.getProxy();
		controlActionInvocation.setDefaultReturnValue(mockActionProxy);
		mockActionInvocation.invoke();
		controlActionInvocation.setDefaultReturnValue(Action.INPUT);
		
		
		controlAction.replay();
		controlActionProxy.replay();
		controlActionInvocation.replay();
		
		ValidationInterceptor interceptor = create();
		String result = interceptor.intercept(mockActionInvocation);
		
		assertEquals(Action.INPUT, result);
		controlAction.verify();
		controlActionProxy.verify();
		controlActionInvocation.verify();
	}
	
	public void testPrefixMethodInvocation2() throws Exception {
		MockControl controlAction = MockControl.createControl(ValidateAction.class);
		ValidateAction mockAction = (ValidateAction) controlAction.getMock();
		mockAction.validateSubmit();
		controlAction.setVoidCallable(1);
		mockAction.validate();
		controlAction.setVoidCallable();
		
		MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
		ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
		mockActionProxy.getMethod();
		controlActionProxy.setDefaultReturnValue("submit");
		mockActionProxy.getActionName();
                controlActionProxy.setDefaultReturnValue("something");
		
		MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
		ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
		mockActionInvocation.getAction();
		controlActionInvocation.setDefaultReturnValue(mockAction);
		mockActionInvocation.getProxy();
		controlActionInvocation.setDefaultReturnValue(mockActionProxy);
		mockActionInvocation.invoke();
		controlActionInvocation.setReturnValue("okok");
		
		
		controlAction.replay();
		controlActionProxy.replay();
		controlActionInvocation.replay();
		
		ValidationInterceptor interceptor = create();
		String result = interceptor.intercept(mockActionInvocation);
		
		assertEquals("okok", result);
		controlAction.verify();
		controlActionProxy.verify();
		controlActionInvocation.verify();
	}
	
	protected ValidationInterceptor create() {
	    ObjectFactory objectFactory = container.getInstance(ObjectFactory.class);
	    return (ValidationInterceptor) objectFactory.buildInterceptor(
                new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), new HashMap());
	}
	
	private interface ValidateAction extends Action, Validateable, ValidationAware {
		void validateDoSave();
		void validateSubmit();
		String submit();
    }
}
