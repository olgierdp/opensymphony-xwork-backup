/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork.config.entities.ActionConfig;

/**
 * Contribed by: Ruben Inoto
 * @version $Date$ $Id$
 */
public class ProxyInvocationTest extends XWorkTestCase {

    /**
     * Sets a ProxyObjectFactory as ObjectFactory (so the FooAction will always be retrieved
     * as a FooProxy), and it tries to call invokeAction on the TestActionInvocation.
     * 
     * It should fail, because the Method got from the action (actually a FooProxy) 
     * will be executed on the InvocationHandler of the action (so, in the action itself). 
     */
    public void testProxyInvocation() throws Exception {

        ObjectFactory.setObjectFactory(new ProxyObjectFactory());

        ActionProxy proxy = ActionProxyFactory.getFactory()
            .createActionProxy("", "ProxyInvocation", createDummyContext());
        TestActionInvocation invocation = new TestActionInvocation(proxy);

        String result = invocation.invokeAction(proxy.getAction(), proxy
            .getConfig());
        assertEquals("proxyResult", result);

    }

    /** 
     * Needed for the creation of the action proxy
     */
    private Map createDummyContext() {
        Map params = new HashMap();
        params.put("blah", "this is blah");
        Map extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        return extraContext;
    }

    /**
     * Simple proxy that just invokes the method on the target on the invoke method
     */
    public class ProxyInvocationProxy implements InvocationHandler {

        private Object target;

        public ProxyInvocationProxy(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable {
            return m.invoke(target, args);
        }
    }

    /**
     * Subclass of DefaultActionInvocation, to provide a visible constructor
     */
    private class TestActionInvocation extends DefaultActionInvocation {
    	
		private static final long serialVersionUID = -7015263281680465248L;

		protected TestActionInvocation(ActionProxy proxy) throws Exception {
            super(proxy);
        }

        public String invokeAction(Object action, ActionConfig actionConfig)
            throws Exception {
            return super.invokeAction(action, actionConfig);
        }
    }

    /**
     * ObjectFactory that returns a FooProxy in the buildBean if the clazz is FooAction 
     */
    private class ProxyObjectFactory extends ObjectFactory {

        /**
         * It returns an instance of the bean except if the class is FooAction. 
         * In this case, it returns a FooProxy of it.
         */
        public Object buildBean(Class clazz, Map extraContext)
            throws Exception {
            Object bean = super.buildBean(clazz, extraContext);
            if(clazz.equals(ProxyInvocationAction.class)) {
                return Proxy.newProxyInstance(bean.getClass()
                    .getClassLoader(), bean.getClass().getInterfaces(),
                    new ProxyInvocationProxy(bean));

            }
            return bean;
        }
    }
}
