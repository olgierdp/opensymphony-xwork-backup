/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ModelDriven;
import com.opensymphony.xwork.util.OgnlValueStack;


/**
 * Watches for ModelDriven Actions and adds the model from the Action on to the
 * value stack.
 * <p/>
 * <b>Note:</b>  The ModelDrivenInterceptor must come before the both
 * {@link StaticParametersInterceptor} and {@link ParametersInterceptor} if you
 * want the parameters to be applied to the model.
 *
 * @author $Author$
 * @version $Revision$
 */
public class ModelDrivenInterceptor extends AroundInterceptor {

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven) action;
            OgnlValueStack stack = invocation.getStack();
            stack.push(modelDriven.getModel());
        }
    }
}
