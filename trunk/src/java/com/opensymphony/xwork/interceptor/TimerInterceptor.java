/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * TimerInterceptor
 *
 * Created : Jan 9, 2003 1:15:53 AM
 *
 * @author Jason Carreira
 */
public class TimerInterceptor implements Interceptor {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(TimerInterceptor.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Called to let an interceptor clean up any resources it has allocated.
    */
    public void destroy() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TimerInterceptor)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return 10;
    }

    /**
    * Called after an Interceptor is created, but before any requests are processed using the intercept() methodName. This
    * gives the Interceptor a chance to initialize any needed resources.
    */
    public void init() {
    }

    /**
    * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
    * request by the DefaultActionInvocation or to short-circuit the processing and just return a String return code.
    */
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
}
