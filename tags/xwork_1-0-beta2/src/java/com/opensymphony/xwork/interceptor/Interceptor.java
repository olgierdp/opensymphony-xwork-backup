/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;


/**
 * Interceptors follow the paradigm of {@link javax.servlet.Filter}. Interceptors should be stateless and not assume that
 * a new instance will be created for each request or Action. Interceptors may choose to either short-circuit the
 * DefaultActionInvocation execution and return a return code (such as Action.SUCCESS), or it may choose to do some processing
 * before and/or after delegating the rest of the procesing using DefaultActionInvocation.doInterceptor().
 *
 * @author $author$
 * @version $Revision$
 */
public interface Interceptor {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Called to let an interceptor clean up any resources it has allocated.
    */
    void destroy();

    /**
    * Called after an Interceptor is created, but before any requests are processed using the intercept() methodName. This
    * gives the Interceptor a chance to initialize any needed resources.
    */
    void init();

    /**
    * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
    * request by the DefaultActionInvocation or to short-circuit the processing and just return a String return code.
    *
    * @param invocation
    * @return
    * @throws Exception
    */
    String intercept(ActionInvocation invocation) throws Exception;
}
