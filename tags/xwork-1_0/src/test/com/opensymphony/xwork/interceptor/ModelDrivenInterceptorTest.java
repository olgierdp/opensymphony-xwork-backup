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


/**
 * @author $Author$
 * @version $Revision$
 */
public class ModelDrivenInterceptorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    Action action;
    Mock mockActionInvocation;
    Mock mockActionProxy;
    ModelDrivenInterceptor modelDrivenInterceptor;
    Object model;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testModelDrivenGetsPushedOntoStack() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        action = new ModelDrivenAction();
        mockActionProxy.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getStack", stack);

        modelDrivenInterceptor.before((ActionInvocation) mockActionInvocation.proxy());

        Object topOfStack = stack.pop();
        assertEquals("our model should be on the top of the stack", model, topOfStack);
    }

    public void testStackNotModifedForNormalAction() throws Exception {
        action = new ActionSupport();
        mockActionProxy.expectAndReturn("getAction", action);

        // nothing should happen
        modelDrivenInterceptor.before((ActionInvocation) mockActionInvocation.proxy());
    }

    protected void setUp() throws Exception {
        mockActionProxy = new Mock(ActionProxy.class);
        mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionInvocation.expectAndReturn("getProxy", mockActionProxy.proxy());
        modelDrivenInterceptor = new ModelDrivenInterceptor();
        model = new Date(); // any object will do
    }

    protected void tearDown() throws Exception {
        mockActionProxy.verify();
        mockActionInvocation.verify();
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public class ModelDrivenAction extends ActionSupport implements ModelDriven {
        public Object getModel() {
            return model;
        }
    }
}
