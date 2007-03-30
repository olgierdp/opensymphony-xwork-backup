/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for {@link ParametersInterceptor}.
 *
 * @author Jason Carreira
 */
public class ParametersInterceptorTest extends XWorkTestCase {

    public void testParameterNameAware() {
        ParametersInterceptor pi = new ParametersInterceptor();
        final Map actual = new HashMap();
        ValueStack stack = new OgnlValueStack() {
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

    public void testDoesNotAllowMethodInvocations() throws Exception {
        Map params = new HashMap();
        params.put("@java.lang.System@exit(1).dummy", "dumb value");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, extraContext);
        assertEquals(Action.SUCCESS, proxy.execute());

        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        TestBean model = (TestBean) action.getModel();

        String property = System.getProperty("xwork.security.test");
        assertNull(property);
    }

    public void testModelDrivenParameters() throws Exception {
        Map params = new HashMap();
        final String fooVal = "com.opensymphony.xwork2.interceptor.ParametersInterceptorTest.foo";
        params.put("foo", fooVal);

        final String nameVal = "com.opensymphony.xwork2.interceptor.ParametersInterceptorTest.name";
        params.put("name", nameVal);
        params.put("count", "15");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, extraContext);
        assertEquals(Action.SUCCESS, proxy.execute());

        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        TestBean model = (TestBean) action.getModel();
        assertEquals(nameVal, model.getName());
        assertEquals(15, model.getCount());
        assertEquals(fooVal, action.getFoo());
    }

    public void testParametersDoesNotAffectSession() throws Exception {
        Map params = new HashMap();
        params.put("blah", "This is blah");
        params.put("#session.foo", "Foo");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        ValueStack stack = proxy.getInvocation().getStack();
        HashMap session = new HashMap();
        stack.getContext().put("session", session);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        assertNull(session.get("foo"));
    }

    public void testParameters() throws Exception {
        Map params = new HashMap();
        params.put("blah", "This is blah");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
    }

    public void testNonexistentParametersGetLoggedInDevMode() throws Exception {
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        OgnlValueStack.setDevMode("true");
        ParametersInterceptor.setDevMode("true");

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        final String actionMessage = "" + ((SimpleAction) proxy.getAction()).getActionMessages().toArray()[0];
        assertTrue(actionMessage.indexOf("Error setting expression 'not_a_property' with value 'There is no action property named like this'") > -1);
    }

    public void testNonexistentParametersAreIgnoredInProductionMode() throws Exception {
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        OgnlValueStack.setDevMode("false");

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        assertTrue(((SimpleAction) proxy.getAction()).getActionMessages().isEmpty());
    }

    public void testNoParametersAction() throws Exception {
        ParametersInterceptor interceptor = new ParametersInterceptor();
        interceptor.init();

        MockActionInvocation mai = new MockActionInvocation();
        Action action = new NoParametersAction();
        mai.setAction(action);

        interceptor.doIntercept(mai);
        interceptor.destroy();
    }
    
    public void testExcludedParametersAreIgnored() throws Exception {
        ParametersInterceptor pi = new ParametersInterceptor();
        pi.setExcludeParams("dojo\\..*");
        final Map actual = new HashMap();
        ValueStack stack = new OgnlValueStack() {
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }
        };
        final Map expected = new HashMap() {
            {
                put("fooKey", "fooValue");
            }
        };

        Map parameters = new HashMap() {
            {
                put("dojo.test", "dojoValue");
                put("fooKey", "fooValue");
            }
        };
        pi.setParameters(new NoParametersAction(), stack, parameters);
        assertEquals(expected, actual);
    }


    private class NoParametersAction implements Action, NoParameters {

        public String execute() throws Exception {
            return SUCCESS;
        }
    }

    protected void setUp() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());
    }
}
