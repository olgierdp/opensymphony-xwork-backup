/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.*;

import ognl.PropertyAccessor;


/**
 * Unit test for {@link ParametersInterceptor}.
 *
 * @author Jason Carreira
 */
public class ParametersInterceptorTest extends XWorkTestCase {

    public void testParameterNameAware() {
        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        final Map actual = new HashMap();
        ValueStack stack = createStubValueStack(actual);
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
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), 
                new MockConfigurationProvider(Collections.singletonMap("devMode", "true")));
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        ParametersInterceptor.setDevMode("true");

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        final String actionMessage = "" + ((SimpleAction) proxy.getAction()).getActionMessages().toArray()[0];
        assertTrue(actionMessage.indexOf("Error setting expression 'not_a_property' with value 'There is no action property named like this'") > -1);
    }

    public void testNonexistentParametersAreIgnoredInProductionMode() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), 
                new MockConfigurationProvider(Collections.singletonMap("devMode", "false")));
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

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

    public void testNoOrdered() throws Exception {
        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        final Map actual = new LinkedHashMap();
        ValueStack stack = createStubValueStack(actual);

        Map parameters = new HashMap();
        parameters.put("user.address.city", "London");
        parameters.put("user.name", "Superman");

        Action action = new SimpleAction();
        pi.setParameters(action, stack, parameters);

        assertEquals("ordered should be false by default", false, pi.isOrdered());
        assertEquals(2, actual.size());
        assertEquals("London", actual.get("user.address.city"));
        assertEquals("Superman", actual.get("user.name"));

        // is not ordered
        List values = new ArrayList(actual.values());
        assertEquals("London", values.get(0));
        assertEquals("Superman", values.get(1));
    }

    public void testOrdered() throws Exception {
        ParametersInterceptor pi = new ParametersInterceptor();
        pi.setOrdered(true);
        container.inject(pi);
        final Map actual = new LinkedHashMap();
        ValueStack stack = createStubValueStack(actual);

        Map parameters = new HashMap();
        parameters.put("user.address.city", "London");
        parameters.put("user.name", "Superman");

        Action action = new SimpleAction();
        pi.setParameters(action, stack, parameters);

        assertEquals(true, pi.isOrdered());
        assertEquals(2, actual.size());
        assertEquals("London", actual.get("user.address.city"));
        assertEquals("Superman", actual.get("user.name"));

        // should be ordered so user.name should be first
        List values = new ArrayList(actual.values());
        assertEquals("Superman", values.get(0));
        assertEquals("London", values.get(1));
    }

    public void testSetOrdered() throws Exception {
        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        assertEquals("ordered should be false by default", false, pi.isOrdered());
        pi.setOrdered(true);
        assertEquals(true, pi.isOrdered());
    }
    
    public void testExcludedParametersAreIgnored() throws Exception {
        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        pi.setExcludeParams("dojo\\..*");
        final Map actual = new HashMap();
        ValueStack stack = createStubValueStack(actual);
        container.inject(stack);
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

    private ValueStack createStubValueStack(final Map actual) {
        ValueStack stack = new OgnlValueStack(
                container.getInstance(XWorkConverter.class),
                (CompoundRootAccessor)container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName()),
                container.getInstance(TextProvider.class, "system"), true) {
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }
        };
        container.inject(stack);
        return stack;
    }
    
    /*
    public void testIndexedParameters() throws Exception {
        Map params = new HashMap();
        params.put("indexedProp[33]", "This is blah");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getIndexedProp(33));
    }
    */


    private class NoParametersAction implements Action, NoParameters {

        public String execute() throws Exception {
            return SUCCESS;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());
    }
}
