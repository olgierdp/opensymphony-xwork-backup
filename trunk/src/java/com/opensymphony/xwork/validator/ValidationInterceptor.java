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
        List validators = ActionValidatorManager.getValidators(invocation.getAction().getClass(), invocation.getProxy().getActionName());
        Action action = invocation.getAction();

        if (log.isDebugEnabled()) {
            log.debug("Validating " + invocation.getProxy().getNamespace() + invocation.getProxy().getActionName() + " with " + validators.size() + " validators.");
        }

        for (Iterator iterator = validators.iterator(); iterator.hasNext();) {
            Validator validator = (Validator) iterator.next();

            if (log.isDebugEnabled()) {
                log.debug("Running validator: " + validator);
            }

            validator.validate(action);
        }
    }
}
