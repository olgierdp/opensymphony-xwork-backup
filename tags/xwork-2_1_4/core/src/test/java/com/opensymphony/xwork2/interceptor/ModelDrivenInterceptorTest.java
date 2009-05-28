/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.ConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.util.ValueStack;

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
    PreResultListener preResultListener;


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

    public void testModelDrivenUpdatedAndGetsPushedOntoStack() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        action = new ModelDrivenAction();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.matchAndReturn("getStack", stack);
        mockActionInvocation.expectAndReturn("invoke", "foo");
        mockActionInvocation.expect("addPreResultListener", new ConstraintMatcher() {

            public boolean matches(Object[] objects) {
                preResultListener = (PreResultListener) objects[0];
                return true;
            }

            public Object[] getConstraints() {
                return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        modelDrivenInterceptor.setRefreshModelBeforeResult(true);

        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        assertNotNull(preResultListener);
        model = "this is my model";
        preResultListener.beforeResult((ActionInvocation) mockActionInvocation.proxy(), "success");

        Object topOfStack = stack.pop();
        assertEquals("our model should be on the top of the stack", model, topOfStack);
        assertEquals(1, stack.getRoot().size());
    }

    public void testStackNotModifedForNormalAction() throws Exception {
        action = new ActionSupport();
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("invoke", "foo");

        // nothing should happen
        modelDrivenInterceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockActionInvocation = new Mock(ActionInvocation.class);
        modelDrivenInterceptor = new ModelDrivenInterceptor();
        model = new Date(); // any object will do
    }

    @Override
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
