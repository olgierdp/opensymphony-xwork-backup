/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;


/**
 * <!-- START SNIPPET: description -->
 * TODO: Give a description of the Interceptor.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * TODO: Describe the paramters for this Interceptor.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * TODO: Discuss some possible extension of the Interceptor.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- TODO: Describe how the Interceptor reference will effect execution --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *      TODO: fill in the interceptor reference.
 *     &lt;interceptor-ref name=""/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * Validates an action. This interceptor extends the {@link AroundInterceptor} and implements only the
 * {@link AroundInterceptor#before(com.opensymphony.xwork.ActionInvocation)} method. This class
 * simply calls the {@link ActionValidatorManager#validate(java.lang.Object, java.lang.String)} method
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
        Object action = invocation.getAction();
        String context = invocation.getProxy().getActionName();

        if (log.isDebugEnabled()) {
            log.debug("Validating "
                    + invocation.getProxy().getNamespace() + "/" + invocation.getProxy().getActionName() + ".");
        }

        ActionValidatorManager.validate(action, context);
    }
}
