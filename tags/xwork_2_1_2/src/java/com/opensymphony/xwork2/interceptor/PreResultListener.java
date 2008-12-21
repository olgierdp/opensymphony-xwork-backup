/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;


/**
 * PreResultListeners may be registered with an {@link ActionInvocation} to get a callback after the
 * {@link com.opensymphony.xwork2.Action} has been executed but before the {@link com.opensymphony.xwork2.Result}
 * is executed.
 *
 * @author Jason Carreira
 */
public interface PreResultListener {

    /**
     * This callback method will be called after the {@link com.opensymphony.xwork2.Action} execution and
     * before the {@link com.opensymphony.xwork2.Result} execution.
     *
     * @param invocation  the action invocation
     * @param resultCode  the result code returned by the action (eg. <code>success</code>).
     */
    void beforeResult(ActionInvocation invocation, String resultCode);

}
