/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.interceptor.Interceptor;
import junit.framework.Assert;


/**
 * TestInterceptor
 *
 * @author Jason Carreira
 *         Created Apr 21, 2003 9:04:06 PM
 */
public class TestInterceptor implements Interceptor {

    public static final String DEFAULT_FOO_VALUE = "fooDefault";


    private String expectedFoo = DEFAULT_FOO_VALUE;
    private String foo = DEFAULT_FOO_VALUE;
    private boolean executed = false;


    public boolean isExecuted() {
        return executed;
    }

    public void setExpectedFoo(String expectedFoo) {
        this.expectedFoo = expectedFoo;
    }

    public String getExpectedFoo() {
        return expectedFoo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    /**
     * Called to let an interceptor clean up any resources it has allocated.
     */
    public void destroy() {
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
     *
     * @param invocation
     * @return
     * @throws Exception
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        executed = true;
        Assert.assertNotSame(DEFAULT_FOO_VALUE, foo);
        Assert.assertEquals(expectedFoo, foo);

        return invocation.invoke();
    }
}
