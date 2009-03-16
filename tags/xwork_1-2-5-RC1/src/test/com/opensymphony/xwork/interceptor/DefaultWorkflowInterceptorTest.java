/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.mock.MockActionProxy;
import junit.framework.TestCase;


/**
 * Unit test for {@link DefaultWorkflowInterceptor}.
 *
 * @author Jason Carreira
 */
public class DefaultWorkflowInterceptorTest extends TestCase {

    DefaultWorkflowInterceptor interceptor;
    private ActionInvocation invocation;
    private Mock actionMock;
    private Mock invocationMock;
    private Action action;
    private ActionProxy proxy;


    public void testInvokesActionInvocationIfNoErrors() throws Exception {
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testReturnsInputWithoutExecutingIfHasErrors() throws Exception {
        actionMock.expectAndReturn("hasErrors", true);
        actionMock.expect("validate");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(Action.INPUT, interceptor.intercept(invocation));
    }

    public void testExcludesMethod() throws Exception {
        interceptor.setExcludeMethods("execute");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testExcludesMethodWithWildCard() throws Exception {
        interceptor.setExcludeMethods("*");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesMethodWithWildcard() throws Exception {
        interceptor.setIncludeMethods("*");
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }


    public void testIncludesMethod() throws Exception {
        interceptor.setIncludeMethods("execute");
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethod() throws Exception {
        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("execute");
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodAllWildCarded() throws Exception {
        interceptor.setExcludeMethods("*");
        interceptor.setIncludeMethods("*");
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithExcludeWildcard() throws Exception {
        interceptor.setExcludeMethods("*");
        interceptor.setIncludeMethods("execute");
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithIncludeWildcardAndNoMatches() throws Exception {
        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("*");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithIncludeWildcard() throws Exception {
        interceptor.setExcludeMethods("input,validate");
        interceptor.setIncludeMethods("*");
        actionMock.expectAndReturn("hasErrors", false);
        actionMock.expect("validate");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testNoValidateAction() throws Exception {
        actionMock = new Mock(Action.class);
        action = (Action) actionMock.proxy();

        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("execute");
        final String result = "testing123";
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("getAction", action);
        assertEquals(result, interceptor.intercept(invocation));
    }
    
    
    protected void setUp() throws Exception {
        super.setUp();
        actionMock = new Mock(ValidateAction.class);
        action = (ValidateAction) actionMock.proxy();
        invocationMock = new Mock(ActionInvocation.class);
        proxy = new MockActionProxy();
        proxy.setMethod("execute");
        invocationMock.expectAndReturn("getProxy", proxy);
        invocation = (ActionInvocation) invocationMock.proxy();
        interceptor = new DefaultWorkflowInterceptor();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        actionMock.verify();
        invocationMock.verify();
    }

    
    

    private interface ValidateAction extends Action, Validateable, ValidationAware {
    }
}
