/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;


/**
 * PreResultListeners may be registered with an ActionInvocation to get a callback after the Action has been executed
 * but before the Result is executed.
 * @author Jason Carreira
 * Date: Nov 13, 2003 10:55:02 PM
 */
public interface PreResultListener {
    //~ Methods ////////////////////////////////////////////////////////////////

    void beforeResult(ActionInvocation invocation, String resultCode);
}
