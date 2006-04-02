/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: description -->
 * This interceptor logs the the start and end of the execution an action (in English-only, not internationalized).
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
public class LoggingInterceptor extends AroundInterceptor {
    private static final Log log = LogFactory.getLog(LoggingInterceptor.class);
    private static final String FINISH_MESSAGE = "Finishing execution stack for action ";
    private static final String START_MESSAGE = "Starting execution stack for action ";

    protected void after(ActionInvocation invocation, String result) throws Exception {
        logMessage(invocation, FINISH_MESSAGE);
    }

    protected void before(ActionInvocation invocation) throws Exception {
        logMessage(invocation, START_MESSAGE);
    }

    private void logMessage(ActionInvocation invocation, String baseMessage) {
        if (log.isInfoEnabled()) {
            StringBuffer message = new StringBuffer(baseMessage);
            String namespace = invocation.getProxy().getNamespace();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                message.append(namespace).append("/");
            }

            message.append(invocation.getProxy().getActionName());
            log.info(message.toString());
        }
    }

}
