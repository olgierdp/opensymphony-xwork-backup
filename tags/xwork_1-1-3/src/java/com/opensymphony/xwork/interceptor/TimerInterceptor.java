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
 * This interceptor logs the amount of time in milliseconds. In order for this interceptor to work properly, the
 * logging framework must be set to at least the
 * <a href="http://jakarta.apache.org/commons/logging/api/org/apache/commons/logging/Log.html">INFO</a> level.
 * This interceptor relies on the
 * <a href="http://jakarta.apache.org/commons/logging/">Commons Logging API</a> to report its execution-time value.
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
 * &lt;!-- records only the action's execution time --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;interceptor-ref name="timer"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;!-- records action's execution time as well as other interceptors--&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="timer"/&gt;
 *     &lt;interceptor-ref name="completeStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 */
public class TimerInterceptor implements Interceptor {
    private static final Log log = LogFactory.getLog(TimerInterceptor.class);

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        if (log.isInfoEnabled()) {
            long startTime = System.currentTimeMillis();
            String result = invocation.invoke();
            long executionTime = System.currentTimeMillis() - startTime;
            String namespace = invocation.getProxy().getNamespace();
            StringBuffer message = new StringBuffer("Processed action ");

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                message.append(namespace).append("/");
            }

            message.append(invocation.getProxy().getActionName());
            message.append(" in ").append(executionTime).append("ms.");
            log.info(message.toString());

            return result;
        } else {
            return invocation.invoke();
        }
    }

    public int hashCode() {
        return 11;
    }

    public boolean equals(Object obj) {
        return obj instanceof TimerInterceptor;
    }
}
