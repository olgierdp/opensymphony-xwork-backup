/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
*/
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.ValidationException;
import junit.framework.TestCase;

import java.util.HashMap;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Aug 15, 2005
 * Time: 9:05:05 PM
 */
public class ExceptionMappingInterceptorTest extends TestCase {

    ActionInvocation invocation;
    ExceptionMappingInterceptor interceptor;
    Mock mockInvocation;
    OgnlValueStack stack;


    public void testThrownExceptionMatching() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new XworkException("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertNotNull(stack.findValue("exception"));
        assertEquals(stack.findValue("exception"), exception);
        assertEquals(result, "spooky");
        ExceptionHolder holder = (ExceptionHolder) stack.getRoot().get(0); // is on top of the root
        assertNotNull(holder.getExceptionStack()); // to invoke the method for unit test
    }

    public void testThrownExceptionMatching2() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new ValidationException("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertNotNull(stack.findValue("exception"));
        assertEquals(stack.findValue("exception"), exception);
        assertEquals(result, "throwable");
    }

    public void testNoThrownException() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertEquals(result, Action.SUCCESS);
        assertNull(stack.findValue("exception"));
    }

    public void testThrownExceptionNoMatch() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    private void setupWithoutExceptionMappings() {
        ActionConfig actionConfig = new ActionConfig();
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy) actionProxy.proxy()));
        invocation = (ActionInvocation) mockInvocation.proxy();
    }

    protected void setUp() throws Exception {
        super.setUp();
        stack = new OgnlValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(new HashMap()));
        interceptor = new ExceptionMappingInterceptor();

    }

    private void setUpWithExceptionMappings() {
        ActionConfig actionConfig = new ActionConfig();
        actionConfig.addExceptionMapping(new ExceptionMappingConfig("xwork", "com.opensymphony.xwork.XworkException", "spooky"));
        actionConfig.addExceptionMapping(new ExceptionMappingConfig("throwable", "java.lang.Throwable", "throwable"));
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy) actionProxy.proxy()));

        invocation = (ActionInvocation) mockInvocation.proxy();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        invocation = null;
        interceptor = null;
        mockInvocation = null;
        stack = null;
    }

}
