/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Preparable;


/**
 * PrepareInterceptor calls prepare() on Actions which implement com.opensymphony.xwork.Preparable
 *
 * @author Jason Carreira
 * @see com.opensymphony.xwork.Preparable
 *      Date: Nov 5, 2003 2:33:11 AM
 */
public class PrepareInterceptor extends AroundInterceptor {

    protected void after(ActionInvocation dispatcher, String result) throws Exception {
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof Preparable) {
            ((Preparable) action).prepare();
        }
    }
}
