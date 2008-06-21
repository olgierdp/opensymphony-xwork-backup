/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * <!-- START SNIPPET: description -->
 * This interceptor logs the start and end of the execution an action (in English-only, not internationalized).
 * <br/>
 * <b>Note:</b>: This interceptor will log at <tt>INFO</tt> level.
 * <p/>
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * There are no parameters for this interceptor.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * There are no obvious extensions to the existing interceptor.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- prints out a message before and after the immediate action execution --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="logger"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;!-- prints out a message before any more interceptors continue and after they have finished --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="logger"/&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 */
public class LoggingInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String FINISH_MESSAGE = "Finishing execution stack for action ";
    private static final String START_MESSAGE = "Starting execution stack for action ";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        logMessage(invocation, START_MESSAGE);
        String result = invocation.invoke();
        logMessage(invocation, FINISH_MESSAGE);
        return result;
    }

    private void logMessage(ActionInvocation invocation, String baseMessage) {
        if (LOG.isInfoEnabled()) {
            StringBuilder message = new StringBuilder(baseMessage);
            String namespace = invocation.getProxy().getNamespace();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                message.append(namespace).append("/");
            }

            message.append(invocation.getProxy().getActionName());
            LOG.info(message.toString());
        }
    }

}
