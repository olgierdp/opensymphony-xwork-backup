/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * ConversionErrorInterceptorTest
 * @author Jason Carreira
 * Date: Nov 27, 2003 4:48:09 PM
 */
public class ConversionErrorInterceptorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected ActionContext context;
    protected ActionInvocation invocation;
    protected ConversionErrorInterceptor interceptor;
    protected Map conversionErrors;
    protected Mock mockInvocation;
    protected OgnlValueStack stack;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testFieldErrorAdded() throws Exception {
        conversionErrors.put("foo", new Long(123));

        SimpleAction action = new SimpleAction();
        stack.push(action);
        assertNull(action.getFieldErrors().get("foo"));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));
    }

    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new ConversionErrorInterceptor();
        mockInvocation = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) mockInvocation.proxy();
        stack = new OgnlValueStack();
        context = new ActionContext(stack.getContext());
        conversionErrors = new HashMap();
        context.setConversionErrors(conversionErrors);
        mockInvocation.matchAndReturn("getInvocationContext", context);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
    }
}
