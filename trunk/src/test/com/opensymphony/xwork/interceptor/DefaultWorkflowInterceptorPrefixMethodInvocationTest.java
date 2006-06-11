/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import org.easymock.MockControl;
import org.easymock.internal.Range;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.Validateable;
import com.opensymphony.xwork.ValidationAware;

import junit.framework.TestCase;

/**
 * Test DefaultWorkflowInterceptor's prefix method invocation capabilities.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class DefaultWorkflowInterceptorPrefixMethodInvocationTest extends TestCase {

	public void testPrefixMethodInvocation1() throws Exception {
		
		MockControl controlAction = MockControl.createControl(ValidateAction.class);
		ValidateAction mockAction = (ValidateAction) controlAction.getMock();
		mockAction.hasErrors();
		controlAction.setReturnValue(true);
		mockAction.validateDoSave();
		controlAction.setVoidCallable(1);
		mockAction.validate();
		controlAction.setVoidCallable();
		
		MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
		ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
		mockActionProxy.getMethod();
		controlActionProxy.setDefaultReturnValue("save");
		
		MockControl controlActionInvocation = MockControl.createControl(ActionInvocation.class);
		ActionInvocation mockActionInvocation = (ActionInvocation) controlActionInvocation.getMock();
		mockActionInvocation.getAction();
		controlActionInvocation.setDefaultReturnValue(mockAction);
		mockActionInvocation.getProxy();
		controlActionInvocation.setDefaultReturnValue(mockActionProxy);
		
		
		controlAction.replay();
		controlActionProxy.replay();
		controlActionInvocation.replay();
		
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		String result = interceptor.intercept(mockActionInvocation);
		
		assertEquals(Action.INPUT, result);
		controlAction.verify();
		controlActionProxy.verify();
		controlActionInvocation.verify();
	}
	
	public void testPrefixMethodInvocation2() throws Exception {
		MockControl controlAction = MockControl.createControl(ValidateAction.class);
		ValidateAction mockAction = (ValidateAction) controlAction.getMock();
		mockAction.hasErrors();
		controlAction.setReturnValue(false);
		mockAction.validateSubmit();
		controlAction.setVoidCallable(1);
		mockAction.validate();
		controlAction.setVoidCallable();
		
		MockControl controlActionProxy = MockControl.createControl(ActionProxy.class);
		ActionProxy mockActionProxy = (ActionProxy) controlActionProxy.getMock();
		mockActionProxy.getMethod();
		controlActionProxy.setDefaultReturnValue("submit");
		
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
		
		DefaultWorkflowInterceptor interceptor = new DefaultWorkflowInterceptor();
		String result = interceptor.intercept(mockActionInvocation);
		
		assertEquals("okok", result);
		controlAction.verify();
		controlActionProxy.verify();
		controlActionInvocation.verify();
	}
	
	private interface ValidateAction extends Action, Validateable, ValidationAware {
		void validateDoSave();
		void validateSubmit();
		String submit();
    }
}
