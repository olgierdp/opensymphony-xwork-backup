/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;

import java.util.Iterator;
import java.util.List;


/**
 * ValidationInterceptor
 *
 * Created : Jan 19, 2003 3:56:42 PM
 *
 * @author Jason Carreira
 */
public class ValidationInterceptor extends AroundInterceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Action action = invocation.getAction();
        String context = invocation.getProxy().getActionName();

        if (log.isDebugEnabled()) {
            log.debug("Validating " + invocation.getProxy().getNamespace() + invocation.getProxy().getActionName() + ".");
        }

        ActionValidatorManager.validate(action, context);
    }
}
