/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.test.SimpleAction2;

import junit.framework.TestCase;

import java.util.List;


/**
 * ActionValidatorManagerTest
 * @author Jason Carreira
 * Created Jun 9, 2003 11:03:01 AM
 */
public class ActionValidatorManagerTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    ActionInvocation invocation;
    ActionProxy actionProxy;
    Mock mockActionInvocation;
    Mock mockActionProxy;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testBuildValidatorKey() {
        String validatorKey = ActionValidatorManager.buildValidatorKey(invocation);
        assertEquals("/namespace/validationAlias", validatorKey);
        mockActionInvocation.verify();
        mockActionProxy.verify();
    }

    public void testSameAliasWithDifferentNamespace() {
        Action action = new SimpleAction2();
        mockActionInvocation.expectAndReturn("getAction", action);

        //        mockActionProxy.expectAndReturn("getNamespace", "/namespace");
        //        mockActionProxy.expectAndReturn("getActionName", "validationAlias");
        mockActionInvocation.expectAndReturn("getProxy", actionProxy);
        mockActionProxy.expectAndReturn("getActionName", "validationAlias");

        List validatorList = ActionValidatorManager.getValidators(invocation);

        // setup another call, with a different namespace. If it doesn't realize that there should be different
        // validation configurations for the different namespaces, then some of these expected calls won't be made
        // and things should fail.
        mockActionInvocation.expectAndReturn("getProxy", actionProxy);
        mockActionProxy.expectAndReturn("getNamespace", "/namespace2");
        mockActionProxy.expectAndReturn("getActionName", "validationAlias");
        mockActionInvocation.expectAndReturn("getAction", action);
        mockActionInvocation.expectAndReturn("getProxy", actionProxy);
        mockActionProxy.expectAndReturn("getActionName", "validationAlias");

        List validatorList2 = ActionValidatorManager.getValidators(invocation);
        assertNotSame(validatorList, validatorList2);
        mockActionInvocation.verify();
        mockActionProxy.verify();
    }

    protected void setUp() {
        mockActionInvocation = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) mockActionInvocation.proxy();
        mockActionProxy = new Mock(ActionProxy.class);
        actionProxy = (ActionProxy) mockActionProxy.proxy();
        mockActionInvocation.expectAndReturn("getProxy", actionProxy);
        mockActionProxy.expectAndReturn("getNamespace", "/namespace");
        mockActionProxy.expectAndReturn("getActionName", "validationAlias");
    }
}
