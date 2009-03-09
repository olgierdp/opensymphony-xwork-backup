/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ognl.PropertyAccessor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ModelDrivenAction;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.CompoundRootAccessor;
import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.util.OgnlValueStackFactory;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.XWorkConverter;


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
        final ValueStack stack = new OgnlValueStack() {
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }
        };
        ValueStackFactory.setFactory(new ValueStackFactory() {

            @Override
            public ValueStack createValueStack() {
                return stack;
            }

            @Override
            public ValueStack createValueStack(ValueStack stack) {
                return stack;
            }

        });
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

        ValueStackFactory.setFactory(new OgnlValueStackFactory());
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
        params.put("\u0023session[\'user\']", "0wn3d");
        params.put("\\u0023session[\'user\']", "0wn3d");
        params.put("\u0023session.user2", "0wn3d");
        params.put("\\u0023session.user2", "0wn3d");
        params.put("('\u0023'%20%2b%20'session[\'user3\']')(unused)", "0wn3d");
        params.put("('\\u0023' + 'session[\\'user4\\']')(unused)", "0wn3d");
        params.put("\\u0023session[\'user5\']", "0wn3d");
        params.put("\\u0023session.user6", "0wn3d");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        ValueStack stack = proxy.getInvocation().getStack();
        HashMap session = new HashMap();
        stack.getContext().put("session", session);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        assertNull(session.get("foo"));
        assertNull(session.get("user"));
        assertNull(session.get("user2"));
        assertNull(session.get("user3"));
        assertNull(session.get("user4"));
        assertNull(session.get("user5"));
        assertNull(session.get("user6"));
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

    public void testExcludedTrickyParameters() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("blah", "This is blah");
                put("name", "try_1");
                put("(name)", "try_2");
                put("['name']", "try_3");
                put("['na' + 'me']", "try_4");
                put("{name}[0]", "try_5");
                put("(new string{'name'})[0]", "try_6");
                put("#{key: 'name'}.key", "try_7");

            }
        };

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        ParametersInterceptor pi =(ParametersInterceptor) config.getInterceptors().get(0).getInterceptor();
        pi.setExcludeParams("name");

        proxy.execute();

        SimpleAction action = (SimpleAction) proxy.getAction();
        assertNull(action.getName());
        assertEquals("This is blah", (action).getBlah());
    }

    public void testAcceptedTrickyParameters() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("blah", "This is blah");
                put("['baz']", "123");
                put("name", "try_1");
                put("(name)", "try_2");
                put("['name']", "try_3");
                put("['na' + 'me']", "try_4");
                put("{name}[0]", "try_5");
                put("(new string{'name'})[0]", "try_6");
                put("#{key: 'name'}.key", "try_7");
            }
        };

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        ParametersInterceptor pi =(ParametersInterceptor) config.getInterceptors().get(0).getInterceptor();
        pi.setAcceptParamNames("blah, baz");

        proxy.execute();

        SimpleAction action = (SimpleAction) proxy.getAction();
        assertNull(action.getName());
        assertEquals("This is blah", (action).getBlah());
         assertEquals(123, action.getBaz());
    }

    public void testNonexistentParametersGetLoggedInDevMode() throws Exception {
        Map params = new HashMap();
        params.put("not_a_property", "There is no action property named like this");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);
        OgnlValueStack.setDevMode("true");
        ParametersInterceptor.setDevMode("true");

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
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

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
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
        pi.setExcludeParams("dojo\\..*,.*\\\\.*");
        final Map actual = new HashMap();
        final ValueStack stack = new OgnlValueStack() {
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }
        };
        ValueStackFactory.setFactory(new ValueStackFactory() {

            @Override
            public ValueStack createValueStack() {
                return stack;
            }

            @Override
            public ValueStack createValueStack(ValueStack stack) {
                return stack;
            }

        });

        final Map expected = new HashMap() {
            {
                put("fooKey", "fooValue");
            }
        };

        Map parameters = new HashMap() {
            {
                put("dojo.test", "dojoValue");
                put("fooKey", "fooValue");
                put("bar\\Key", "barValue");
            }
        };
        pi.setParameters(new NoParametersAction(), stack, parameters);
        assertEquals(expected, actual);

        ValueStackFactory.setFactory(new OgnlValueStackFactory());
    }

    private ValueStack createStubValueStack(final Map<String, Object> actual) {
        ValueStack stack = new OgnlValueStack() {
            @Override
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
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());
    }
}
