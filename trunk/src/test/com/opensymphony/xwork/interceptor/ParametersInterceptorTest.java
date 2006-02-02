/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * ParametersInterceptorTest
 * <p/>
 * Created : Jan 15, 2003 8:49:15 PM
 *
 * @author Jason Carreira
 */
public class ParametersInterceptorTest extends TestCase {

    public void testParameterNameAware() {
        ParametersInterceptor pi = new ParametersInterceptor();
        final Map actual = new HashMap();
        OgnlValueStack stack = new OgnlValueStack() {
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }
        };
        final Map expected = new HashMap() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
            }
        };
        Object a = new ParameterNameAware() {
            public boolean acceptableParameterName(String parameterName) {
                return expected.containsKey(parameterName);
            }
        };
        Map parameters = new HashMap() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
                put("error", "error");
            }
        };
        pi.setParameters(a, stack, parameters);
        assertEquals(expected, actual);
    }

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

    public void testParametersDoesNotAffectSession() {
        Map params = new HashMap();
        params.put("blah", "This is blah");
        params.put("#session.foo", "Foo");

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
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testParameters() {
        Map params = new HashMap();
        params.put("blah", "This is blah");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
            proxy.execute();
            assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testNonexistentParametersGetLoggedInDevMode() {
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        extraContext.put(ActionContext.DEV_MODE, Boolean.TRUE);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
            proxy.execute();
            final String actionMessage = ""+((SimpleAction) proxy.getAction()).getActionMessages().toArray()[0];
            assertTrue(actionMessage.indexOf("No object in the CompoundRoot has a property named 'not_a_property'")>-1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testNonexistentParametersAreIgnoredInProductionMode() {
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        extraContext.put(ActionContext.DEV_MODE, Boolean.FALSE);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((SimpleAction) proxy.getAction()).getActionMessages().isEmpty());
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
