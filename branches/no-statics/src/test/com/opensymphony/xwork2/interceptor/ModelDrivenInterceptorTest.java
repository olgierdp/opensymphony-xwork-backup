/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import junit.framework.TestCase;

import java.util.Date;


/**
 * @author $Author$
 * @version $Revision$
 */
public class ModelDrivenInterceptorTest extends XWorkTestCase {

    Action action;
    Mock mockActionInvocation;
    ModelDrivenInterceptor modelDrivenInterceptor;
    Object model;


    public void testModelDrivenGetsPushedOntoStack() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        action = new ModelDrivenAction();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getStack", stack);
        mockActionInvocation.expectAndReturn("invoke", "foo");

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());

        Object topOfStack = stack.pop();
        assertEquals("our model should be on the top of the stack", model, topOfStack);
    }

    public void testStackNotModifedForNormalAction() throws Exception {
        action = new ActionSupport();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("invoke", "foo");

        // nothing should happen
        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
    }

    protected void setUp() throws Exception {
        super.setUp();
        mockActionInvocation = new Mock(ActionInvocation.class);
        modelDrivenInterceptor = new ModelDrivenInterceptor();
        model = new Date(); // any object will do
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        mockActionInvocation.verify();
    }


    public class ModelDrivenAction extends ActionSupport implements ModelDriven {
        public Object getModel() {
            return model;
        }
    }
}
