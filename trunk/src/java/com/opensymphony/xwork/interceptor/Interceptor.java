/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;


/**
 * An interceptor is a stateless class that follows the interceptor pattern, as found in {@link  javax.filter.Filter}
 * and in AOP languages.
 * <p/>
 * Interceptors <b>must</b> be stateless and not assume that a new instance will be created for each request or Action.
 * Interceptors may choose to either short-circuit the {@link ActionInvocation} execution and return a return code
 * (such as {@link com.opensymphony.xwork.Action.SUCCESS}), or it may choose to do some processing before
 * and/or after delegating the rest of the procesing using {@link ActionInvocation#invoke()}.
 *
 * @author Jason Carreira
 */
public interface Interceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Called to let an interceptor clean up any resources it has allocated.
     */
    void destroy();

    /**
     * Called after an interceptor is created, but before any requests are processed using
     * {@link #intercept(com.opensymphony.xwork.ActionInvocation) intercept} , giving
     * the Interceptor a chance to initialize any needed resources.
     */
    void init();

    /**
     * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
     * request by the {@link ActionInvocation} or to short-circuit the processing and just return a String return code.
     *
     * @return the return code, either returned from {@link ActionInvocation#invoke()}, or from the interceptor itself.
     * @throws Exception any system-level error, as definied in {@link com.opensymphony.xwork.Action#execute()}.
     */
    String intercept(ActionInvocation invocation) throws Exception;
}
