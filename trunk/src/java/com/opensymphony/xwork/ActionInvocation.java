/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.OgnlValueStack;

import java.io.Serializable;


/**
 * ActionInvocation
 * @author Jason Carreira
 * Created Jun 9, 2003 11:37:27 AM
 */
public interface ActionInvocation extends Serializable {
    //~ Methods ////////////////////////////////////////////////////////////////

    Action getAction();

    boolean isExecuted();

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

    String getResultCode();

    OgnlValueStack getStack();

    String invoke() throws Exception;
}
