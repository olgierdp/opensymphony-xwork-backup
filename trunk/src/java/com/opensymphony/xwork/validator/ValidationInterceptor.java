/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.util.TextParseUtil;
import com.opensymphony.xwork.interceptor.AroundInterceptor;

import java.util.Set;
import java.util.Collections;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor runs the action through the standard validation framework, which in turn checks the action against
 * any validation rules (found in files such as <i>ActionClass-validation.xml</i>) and adds field-level and action-level
 * error messages (provided that the action implements {@link com.opensymphony.xwork.ValidationAware}). This interceptor
 * is often one of the last (or second to last) interceptors applied in a stack, as it assumes that all values have
 * already been set on the action.
 *
 * <p/>This interceptor does nothing if the name of the method being invoked
 * is specified in the <b>excludeMethods</b> parameter. <b>excludeMethods</b>
 * accepts a comma-delimited list of method names. For example, requests to
 * <b>foo!input.action</b> and <b>foo!back.action</b> will be skipped by this
 * interceptor if you set the <b>excludeMethods</b> parameter to "input,
 * back".
 *
 * <p/>Note that this has nothing to do with the {@link com.opensymphony.xwork.Validateable} interface and simply adds
 * error messages to the action. The workflow of the action request does not change due to this interceptor. Rather,
 * this interceptor is often used in conjuction with the <b>workflow</b> interceptor.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @see ActionValidatorManager
 * @see com.opensymphony.xwork.interceptor.DefaultWorkflowInterceptor
 */
public class ValidationInterceptor extends AroundInterceptor {

    Set excludeMethods = Collections.EMPTY_SET;

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = TextParseUtil.commaDelimitedStringToSet(excludeMethods);
    }

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    /**
     * Gets the current action and its context and calls {@link ActionValidatorManager#validate(Object, String)}.
     *
     * @param invocation the execution state of the Action.
     * @throws Exception if an error occurs validating the action.
     */
    protected void before(ActionInvocation invocation) throws Exception {
        if (excludeMethods.contains(invocation.getProxy().getMethod())) {
            log.debug("Skipping validation. Method found in exclude list.");
            return;
        }

        Object action = invocation.getAction();
        String context = invocation.getProxy().getActionName();

        if (log.isDebugEnabled()) {
            log.debug("Validating "
                    + invocation.getProxy().getNamespace() + "/" + invocation.getProxy().getActionName() + ".");
        }

        ActionValidatorManager.validate(action, context);
    }
}
