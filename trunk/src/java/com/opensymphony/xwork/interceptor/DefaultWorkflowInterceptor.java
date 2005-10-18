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
 * An interceptor that does some basic validation workflow before allowing the interceptor chain to continue.
 * The order of execution in the workflow is:
 * <p/>
 * <ol>
 * <li>If the action being executed implements {@link Validateable}, the action's
 * {@link Validateable#validate() validate} method is called.</li>
 * <li>Next, if the action implements {@link ValidationAware}, the action's
 * {@link ValidationAware#hasErrors() hasErrors} method is called. If this
 * method returns true, this interceptor stops the chain from continuing and
 * immediately returns {@link Action#INPUT}</li>
 * </ol>
 * <p/>
 * <i>Note: if the action doesn't implement either interface, this interceptor effectively does nothing.</i>
 *
 * @author Jason Carreira
 */
public class DefaultWorkflowInterceptor implements Interceptor {

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

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
