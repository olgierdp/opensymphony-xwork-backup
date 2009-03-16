/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.Assert;


/**
 * NestedAction
 *
 * @author Jason Carreira
 *         Created Mar 5, 2003 3:08:19 PM
 */
public class NestedAction implements Action {

    private String nestedProperty = ActionNestingTest.NESTED_VALUE;


    public NestedAction() {
    }


    public String getNestedProperty() {
        return nestedProperty;
    }

    public String execute() throws Exception {
        Assert.fail();

        return null;
    }

    public String noStack() {
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        // Action + DefaultTextProvider on the stack
        Assert.assertEquals(2, stack.size());
        Assert.assertNull(stack.findValue(ActionNestingTest.KEY));
        Assert.assertEquals(ActionNestingTest.NESTED_VALUE, stack.findValue(ActionNestingTest.NESTED_KEY));

        return SUCCESS;
    }

    public String stack() {
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        //DefaultTextProvider, NestedActionTest pushed on by the test, and the NestedAction
        Assert.assertEquals(3, stack.size());
        Assert.assertNotNull(stack.findValue(ActionNestingTest.KEY));
        Assert.assertEquals(ActionContext.getContext().getValueStack().findValue(ActionNestingTest.KEY), ActionNestingTest.VALUE);
        Assert.assertEquals(ActionNestingTest.NESTED_VALUE, stack.findValue(ActionNestingTest.NESTED_KEY));

        return SUCCESS;
    }
}
