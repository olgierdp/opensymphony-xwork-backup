/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.util.HashMap;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkTestCase;

public class ScopedModelDrivenInterceptorTest extends XWorkTestCase {

    protected ScopedModelDrivenInterceptor inter = null;
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(ScopedModelDrivenInterceptorTest.class);
    }

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
    
        inter = new ScopedModelDrivenInterceptor();
        ObjectFactory.setObjectFactory(new ObjectFactory());
    }

    public void testResolveModel() throws Exception {
        ActionContext ctx = ActionContext.getContext();
        ctx.setSession(new HashMap());
        
        ObjectFactory factory = ObjectFactory.getObjectFactory();
        Object obj = inter.resolveModel(factory, ctx, "java.lang.String", "request", "foo");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertTrue(obj == ctx.get("foo"));

        
        obj = inter.resolveModel(factory, ctx, "java.lang.String", "session", "foo");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertTrue(obj == ctx.getSession().get("foo"));

        obj = inter.resolveModel(factory, ctx, "java.lang.String", "session", "foo");
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertTrue(obj == ctx.getSession().get("foo"));
        
    }
}

