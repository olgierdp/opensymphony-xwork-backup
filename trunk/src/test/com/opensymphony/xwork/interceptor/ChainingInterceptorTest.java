/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

import java.util.Date;
import java.util.HashMap;


/**
 * ChainingInterceptorTest
 *
 * @author Jason Carreira
 *         Date: Nov 22, 2003 3:11:41 PM
 */
public class ChainingInterceptorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    ActionInvocation invocation;
    ChainingInterceptor interceptor;
    Mock mockInvocation;
    OgnlValueStack stack;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testActionErrorsCanBeAddedAfterChain() throws Exception {
        SimpleAction action1 = new SimpleAction();
        SimpleAction action2 = new SimpleAction();
        action1.addActionError("foo");
        mockInvocation.matchAndReturn("getAction", action2);
        stack.push(action1);
        stack.push(action2);
        interceptor.intercept(invocation);
        assertEquals(action1.getActionErrors(), action2.getActionErrors());
        action2.addActionError("bar");
        assertEquals(1, action1.getActionErrors().size());
        assertEquals(2, action2.getActionErrors().size());
        assertTrue(action2.getActionErrors().contains("bar"));
    }

    public void testNotToManyChains() throws Exception {
        int max = 5;
        interceptor.setMaxChainDepth(max);

        TestBean bean = new TestBean();
        TestBeanAction action = new TestBeanAction();
        mockInvocation.matchAndReturn("getAction", action);
        bean.setBirth(new Date());
        bean.setName("foo");
        bean.setCount(1);
        stack.push(bean);
        stack.push(action);

        try {
            for (int i = 0; i < max; i++) {
                interceptor.intercept(invocation);
            }
        } catch (Exception e) {
            fail("should have not aborted chain");
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        stack = new OgnlValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.matchAndReturn("getStack", stack);
        mockInvocation.matchAndReturn("invoke", Action.SUCCESS);
        mockInvocation.matchAndReturn("getInvocationContext", new ActionContext(new HashMap()));
        invocation = (ActionInvocation) mockInvocation.proxy();
        interceptor = new ChainingInterceptor();
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private class TestBeanAction extends TestBean implements Action {
        public String execute() throws Exception {
            return SUCCESS;
        }
    }
}
