/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.easymock.MockControl;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.util.HashMap;

/**
 * Test ValidationInterceptor's prefix method invocation capabilities.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ValidationInterceptorPrefixMethodInvocationTest extends XWorkTestCase {
    private ActionInvocation invocation;
    private ActionConfig config;
    private ActionProxy proxy;
    private ValidateAction action;
    private String result;
    private String method;

    public void testPrefixMethodInvocation1() throws Exception {
		method = "save";
		result = Action.INPUT;
		
		ValidationInterceptor interceptor = create();
		String result = interceptor.intercept(invocation);
		
		assertEquals(Action.INPUT, result);
	}
	
	public void testPrefixMethodInvocation2() throws Exception {
		method = "save";
		result = "okok";

		ValidationInterceptor interceptor = create();
		String result = interceptor.intercept(invocation);
		
		assertEquals("okok", result);
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        config = new ActionConfig.Builder("", "action", "").build();
        invocation = EasyMock.createNiceMock(ActionInvocation.class);
        proxy = EasyMock.createNiceMock(ActionProxy.class);
        action = EasyMock.createNiceMock(ValidateAction.class);


        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(action).anyTimes();
        EasyMock.expect(invocation.invoke()).andAnswer(new IAnswer<String>() {
            @Override
            public String answer() throws Throwable {
                return result;
            }
        }).anyTimes();

        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();
        EasyMock.expect(proxy.getMethod()).andAnswer(new IAnswer<String>() {
            public String answer() throws Throwable {
                return method;
            }
        }).anyTimes();


        EasyMock.replay(invocation);
        EasyMock.replay(action);
        EasyMock.replay(proxy);

        ActionContext contex = new ActionContext(new HashMap<String, Object>());
        ActionContext.setContext(contex);
        contex.setActionInvocation(invocation);
    }
}
