/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;


/**
 * Validates an action. This interceptor extends the {@link AroundInterceptor} and implements only the
 * {@link AroundInterceptor#before(com.opensymphony.xwork.ActionInvocation)} method. This class
 * simply class the {@link ActionValidatorManager#validate(java.lang.Object, java.lang.String)} method
 * with the given Action and its context.
 *
 * @author Jason Carreira
 */
public class ValidationInterceptor extends AroundInterceptor {

    /**
     * Does nothing in this implementation.
     */
    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    /**
     * Gets the current action and its context and calls
     * {@link ActionValidatorManager#validate(java.lang.Object, java.lang.String)}.
     *
     * @param invocation the execution state of the Action.
     * @throws Exception if an error occurs validating the action.
     */
    protected void before(ActionInvocation invocation) throws Exception {
        Action action = invocation.getAction();
        String context = invocation.getProxy().getActionName();

        if (log.isDebugEnabled()) {
            log.debug("Validating "
                    + invocation.getProxy().getNamespace() + "/" + invocation.getProxy().getActionName() + ".");
        }

        ActionValidatorManager.validate(action, context);
    }
}
