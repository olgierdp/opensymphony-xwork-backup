/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.mock.MockActionInvocation;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for {@link ConversionErrorInterceptor}.
 *
 * @author Jason Carreira
 */
public class ConversionErrorInterceptorTest extends XWorkTestCase {

    protected ActionContext context;
    protected ActionInvocation invocation;
    protected ConversionErrorInterceptor interceptor;
    protected Map conversionErrors;
    protected Mock mockInvocation;
    protected OgnlValueStack stack;


    public void testFieldErrorAdded() throws Exception {
        conversionErrors.put("foo", new Long(123));

        SimpleAction action = new SimpleAction();
        mockInvocation.expectAndReturn("getAction", action);
        stack.push(action);
        mockInvocation.matchAndReturn("getAction", action);
        assertNull(action.getFieldErrors().get("foo"));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));
    }

    public void testFieldErrorWithMapKeyAdded() throws Exception {
        String fieldName = "foo['1'].intValue";
        conversionErrors.put(fieldName, "bar");
        ActionSupport action = new ActionSupport();
        mockInvocation.expectAndReturn("getAction", action);
        stack.push(action);
        mockInvocation.matchAndReturn("getAction", action);
        assertNull(action.getFieldErrors().get(fieldName));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors()); // This fails!
        assertNotNull(action.getFieldErrors().get(fieldName));
    }

    public void testWithPreResultListener() throws Exception {
        conversionErrors.put("foo", "Hello");

        ActionContext ac = new ActionContext(stack.getContext());
        ac.setConversionErrors(conversionErrors);
        ac.setValueStack(stack);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setInvocationContext(ac);
        mai.setStack(stack);
        SimpleAction action = new SimpleAction();
        action.setFoo(55);
        mai.setAction(action);
        stack.push(action);
        assertNull(action.getFieldErrors().get("foo"));
        assertEquals(new Integer(55), stack.findValue("foo"));

        interceptor.intercept(mai);

        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));

        assertEquals("Hello", stack.findValue("foo")); // assume that the original value is reset
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
        mockInvocation.expect("addPreResultListener", C.isA(PreResultListener.class));
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
    }
}
