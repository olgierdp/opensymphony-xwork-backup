/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Serializable;


/**
 * ActionInvocation
 * @author Jason Carreira
 * Created Jun 9, 2003 11:37:27 AM
 */
public interface ActionInvocation extends Serializable {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Get the Action associated with this ActionInvocation
    * @return
    */
    Action getAction();

    boolean isExecuted();

    /**
    * Gets the ActionContext associated with this ActionInvocation. The ActionProxy is
    * responsible for setting this ActionContext onto the ThreadLocal before invoking
    * the ActionInvocation and resetting the old ActionContext afterwards.
    * @return
    */
    ActionContext getInvocationContext();

    ActionProxy getProxy();

    /**
    * If the DefaultActionInvocation has been executed before and the Result is an instance of ActionChainResult, this method
    * will walk down the chain of ActionChainResults until it finds a non-chain result, which will be returned. If the
    * DefaultActionInvocation's result has not been executed before, the Result instance will be created and populated with
    * the result params.
    * @return a Result instance
    * @throws java.lang.Exception
    */
    Result getResult() throws Exception;

    /**
    * Gets the result code returned from this ActionInvocation
    * @return
    */
    String getResultCode();

    OgnlValueStack getStack();

    /**
    * Register a com.opensymphony.xwork.interceptor.PreResultListener to be notified after the Action is executed and before the
    * Result is executed. The ActionInvocation implementation must guarantee that listeners will be called in the order
    * in which they are registered. Listener registration and execution does not need to be thread-safe.
    * @param listener
    */
    void addPreResultListener(PreResultListener listener);

    /**
    * Invokes the next step in processing this ActionInvocation. If there are more
    * Interceptors, this will call the next one. If Interceptors choose not to short-circuit
    * ActionInvocation processing and return their own return code, they will call
    * invoke() to allow the next Interceptor to execute. If there are no more Interceptors
    * to be applied, the Action is executed.
    * @return
    * @throws Exception
    */
    String invoke() throws Exception;
}
