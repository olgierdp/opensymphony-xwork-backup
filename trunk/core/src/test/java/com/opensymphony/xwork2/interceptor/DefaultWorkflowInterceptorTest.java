/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.validator.ValidationInterceptor;

import java.util.HashMap;


/**
 * Unit test for {@link DefaultWorkflowInterceptor}.
 *
 * @author Jason Carreira
 */
public class DefaultWorkflowInterceptorTest extends XWorkTestCase {

    DefaultWorkflowInterceptor interceptor;
    private ActionInvocation invocation;
    private Mock actionMock;
    private Mock invocationMock;
    private Action action;
    private ActionProxy proxy;
    private Mock proxyMock;
    private ActionConfig config;


    public void testInvokesActionInvocationIfNoErrors() throws Exception {
        final String result = "testing123";
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.intercept(invocation);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testReturnsInputWithoutExecutingIfHasErrors() throws Exception {
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", Action.INPUT);
        invocationMock.expectAndReturn("invoke", Action.INPUT);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.intercept(invocation);
        assertEquals(Action.INPUT, interceptor.intercept(invocation));
    }

    public void testExcludesMethod() throws Exception {
        interceptor.setExcludeMethods("execute");
        final String result = "testing123";
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("invoke", result);
        proxyMock.expectAndReturn("getMethod", "execute");        
        invocationMock.expectAndReturn("invoke", result);
                
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute");
        interceptor.setExcludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testExcludesMethodWithWildCard() throws Exception {
        interceptor.setExcludeMethods("*");
        final String result = "testing123";
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.intercept(invocation);
        validationInterceptor.setExcludeMethods("*");
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesMethodWithWildcard() throws Exception {
        interceptor.setIncludeMethods("*");
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }


    public void testIncludesMethod() throws Exception {
        interceptor.setIncludeMethods("execute");
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethod() throws Exception {
        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("execute");
        
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute,input,validate");
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodAllWildCarded() throws Exception {
        interceptor.setExcludeMethods("*");
        interceptor.setIncludeMethods("*");
        
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("*");
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithExcludeWildcard() throws Exception {
        interceptor.setExcludeMethods("*");
        interceptor.setIncludeMethods("execute");
        
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("*");
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithIncludeWildcardAndNoMatches() throws Exception {
        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("*");
        
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute,input,validate");
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithIncludeWildcard() throws Exception {
        interceptor.setExcludeMethods("input,validate");
        interceptor.setIncludeMethods("*");
        
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        actionMock.expect("validate");
        invocationMock.expectAndReturn("invoke", result);
        
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        actionMock.expectAndReturn("hasErrors", false);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("input,validate");
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testNoValidateAction() throws Exception {
        actionMock = new Mock(Action.class);
        action = (Action) actionMock.proxy();

        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("execute");
        
        final String result = "testing123";
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getConfig", config);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("getAction", action);
        invocationMock.expectAndReturn("getProxy", proxy);
        proxyMock.expectAndReturn("getMethod", "execute");
        invocationMock.expectAndReturn("invoke", result);
        invocationMock.expectAndReturn("invoke", result);
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute,input,validate");
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        config = new ActionConfig.Builder("", "name", "").build();
        actionMock = new Mock(ValidateAction.class);
        action = (ValidateAction) actionMock.proxy();
        invocationMock = new Mock(ActionInvocation.class);
        interceptor = new DefaultWorkflowInterceptor();
        proxyMock = new Mock(ActionProxy.class);
        proxy = (ActionProxy) proxyMock.proxy();
        invocationMock.expectAndReturn("getProxy", proxy);
        invocation = (ActionInvocation) invocationMock.proxy();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        actionMock.verify();
        invocationMock.verify();
    }
    
    protected ValidationInterceptor create() {
        ObjectFactory objectFactory = container.getInstance(ObjectFactory.class);
        return (ValidationInterceptor) objectFactory.buildInterceptor(
                new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), new HashMap());
    }

    
    

    private interface ValidateAction extends Action, Validateable, ValidationAware {
    }
}
