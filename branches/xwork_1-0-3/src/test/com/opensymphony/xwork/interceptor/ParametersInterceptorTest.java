/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ModelDrivenAction;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * ParametersInterceptorTest
 *
 * Created : Jan 15, 2003 8:49:15 PM
 *
 * @author Jason Carreira
 */
public class ParametersInterceptorTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testDoesNotAllowMethodInvocations() {
        Map params = new HashMap();
        params.put("@java.lang.System@exit(1).dummy", "dumb value");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, extraContext);
            assertEquals(Action.SUCCESS, proxy.execute());

            ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
            TestBean model = (TestBean) action.getModel();

            String property = System.getProperty("webwork.security.test");
            assertNull(property);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testModelDrivenParameters() {
        Map params = new HashMap();
        final String fooVal = "com.opensymphony.xwork.interceptor.ParametersInterceptorTest.foo";
        params.put("foo", fooVal);

        final String nameVal = "com.opensymphony.xwork.interceptor.ParametersInterceptorTest.name";
        params.put("name", nameVal);
        params.put("count", "15");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, extraContext);
            assertEquals(Action.SUCCESS, proxy.execute());

            ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
            TestBean model = (TestBean) action.getModel();
            assertEquals(nameVal, model.getName());
            assertEquals(15, model.getCount());
            assertEquals(fooVal, action.getFoo());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParameters() {
        Map params = new HashMap();
        params.put("blah", "This is blah");
        params.put("#session.foo", "Foo");
        params.put("\u0023session[\'user\']", "0wn3d");
        params.put("\u0023session.user2", "0wn3d");
        params.put("('\u0023'%20%2b%20'session[\'user3\']')(unused)", "0wn3d");
        params.put("('\\u0023' + 'session[\\'user4\\']')(unused)", "0wn3d");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
            OgnlValueStack stack = proxy.getInvocation().getStack();
            HashMap session = new HashMap();
            stack.getContext().put("session", session);
            proxy.execute();
            assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
            assertNull(session.get("foo"));
            assertNull(session.get("user"));
            assertNull(session.get("user2"));
            assertNull(session.get("user3"));
            assertNull(session.get("user4"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}