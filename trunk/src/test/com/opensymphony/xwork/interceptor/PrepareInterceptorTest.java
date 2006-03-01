/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
*/
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork.*;
import junit.framework.TestCase;

/**
 * Unit test for PrepareInterceptor.
 *
 * @author Claus Ibsen
 */
public class PrepareInterceptorTest extends TestCase {

    private Mock mock;
    private PrepareInterceptor interceptor;

    public void testPrepareCalled() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(mock.proxy());
        mock.expect("prepare");

        interceptor.before(mai);
        interceptor.after(mai, Action.SUCCESS); // to have higher code coverage
    }

    protected void setUp() throws Exception {
        mock = new Mock(Preparable.class);
        interceptor = new PrepareInterceptor();
    }

    protected void tearDown() throws Exception {
        mock.verify();
    }

}
