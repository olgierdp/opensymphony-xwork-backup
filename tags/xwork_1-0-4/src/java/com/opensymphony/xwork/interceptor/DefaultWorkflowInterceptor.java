/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Validateable;
import com.opensymphony.xwork.ValidationAware;


/**
 * An interceptor that does some basic validation workflow before allowing the interceptor chain to continue.
 * The order of execution in the workflow is:
 *
 * <ol>
 *  <li>If the action being executed implements {@link Validateable}, the action's
 *      {@link Validateable#validate() validate} method is called.</li>
 *  <li>Next, if the action implements {@link ValidationAware}, the action's
 *      {@link ValidationAware#hasErrors() hasErrors} method is called. If this
 *      method returns true, this interceptor stops the chain from continuing and
 *      immediately returns {@link Action#INPUT}</li>
 * </ol>
 *
 * <i>Note: if the action doesn't implement either interface, this interceptor effectively does nothing.</i>
 *
 * @author Jason Carreira
 */
public class DefaultWorkflowInterceptor implements Interceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Action action = invocation.getAction();

        if (action instanceof Validateable) {
            Validateable validateable = (Validateable) action;
            validateable.validate();
        }

        if (action instanceof ValidationAware) {
            ValidationAware validationAwareAction = (ValidationAware) action;

            if (validationAwareAction.hasErrors()) {
                return Action.INPUT;
            }
        }

        return invocation.invoke();
    }
}
