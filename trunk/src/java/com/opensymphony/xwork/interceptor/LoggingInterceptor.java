/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * LoggingInterceptor
 *
 * Created : Jan 9, 2003 1:23:03 AM
 *
 * @author Jason Carreira
 */
public class LoggingInterceptor extends AroundInterceptor {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log log = LogFactory.getLog(LoggingInterceptor.class);
    private static final String FINISH_MESSAGE = "Finishing execution stack for action ";
    private static final String START_MESSAGE = "Starting execution stack for action ";

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LoggingInterceptor)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return 10;
    }

    protected void after(ActionInvocation invocation, String result) throws Exception {
        logMessage(invocation, FINISH_MESSAGE);
    }

    protected void before(ActionInvocation invocation) throws Exception {
        logMessage(invocation, START_MESSAGE);
    }

    private void logMessage(ActionInvocation invocation, String baseMessage) {
        StringBuffer message = new StringBuffer(baseMessage);
        String namespace = invocation.getProxy().getNamespace();
        if ((namespace != null) && (namespace.trim().length() > 0)) {
            message.append(namespace).append("/");
        }
        message.append(invocation.getProxy().getActionName());
        log.info(message.toString());
    }
}
