/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * AroundInterceptor
 *
 * @author Jason Carreira
 */
public abstract class AroundInterceptor implements Interceptor {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected Log log = LogFactory.getLog(this.getClass());

    //~ Methods ////////////////////////////////////////////////////////////////

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;

        before(invocation);
        result = invocation.invoke();
        after(invocation, result);

        return result;
    }

    protected abstract void after(ActionInvocation dispatcher, String result) throws Exception;

    protected abstract void before(ActionInvocation invocation) throws Exception;
}
