/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.util.OgnlValueStack;


/**
 * @author $Author$
 * @version $Revision$
 */
public class ModelDrivenInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Action action = invocation.getProxy().getAction();

        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven) action;
            OgnlValueStack stack = invocation.getStack();
            stack.push(modelDriven.getModel());
        }
    }
}
