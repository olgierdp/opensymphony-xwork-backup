/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * ActionNestingTest
 * @author Jason Carreira
 * Created Mar 5, 2003 2:02:01 PM
 */
public class ActionNestingTest extends TestCase {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String VALUE = "myValue";
    public static final String NESTED_VALUE = "myNestedValue";
    public static final String KEY = "myProperty";
    public static final String NESTED_KEY = "nestedProperty";
    public static final String NAMESPACE = "NestedActionTest";
    public static final String SIMPLE_ACTION_NAME = "SimpleAction";
    public static final String NO_STACK_ACTION_NAME = "NoStackNestedAction";
    public static final String STACK_ACTION_NAME = "StackNestedAction";

    //~ Instance fields ////////////////////////////////////////////////////////

    private ActionContext context;

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getMyProperty() {
        return VALUE;
    }

    public void setUp() throws ConfigurationException {
        ConfigurationManager.addConfigurationProvider(new NestedTestConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();

        OgnlValueStack stack = new OgnlValueStack();

        // create the action context
        Map contextMap = stack.getContext();

        // give the value stack a context
        stack.push(this);
        context = new ActionContext(contextMap);
        ActionContext.setContext(context);
    }

    public void testNestedContext() {
        assertEquals(context, ActionContext.getContext());

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(NAMESPACE, SIMPLE_ACTION_NAME, null);
            proxy.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(context, ActionContext.getContext());
    }

    public void testNestedNoValueStack() {
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        assertEquals(VALUE, stack.findValue(KEY));

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(NAMESPACE, NO_STACK_ACTION_NAME, null);
            proxy.execute();
            stack = ActionContext.getContext().getValueStack();
            assertEquals(stack.findValue(KEY), VALUE);
            assertNull(stack.findValue(NESTED_KEY));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testNestedValueStack() {
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        assertEquals(VALUE, stack.findValue(KEY));

        try {
            HashMap extraContext = new HashMap();
            extraContext.put(ActionContext.VALUE_STACK, stack);

            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(NAMESPACE, STACK_ACTION_NAME, extraContext);
            proxy.execute();
            assertEquals(context, ActionContext.getContext());
            assertEquals(stack, ActionContext.getContext().getValueStack());
            assertEquals(VALUE, stack.findValue(KEY));
            assertEquals(NESTED_VALUE, stack.findValue(NESTED_KEY));
            assertEquals(2, stack.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    class NestedTestConfigurationProvider implements ConfigurationProvider {
        public void destroy() {
        }

        /**
        * Initializes the configuration object.
        */
        public void init(Configuration configurationManager) {
            PackageConfig packageContext = new PackageConfig();
            ActionConfig config = new ActionConfig(null, SimpleAction.class, null, null, null);
            packageContext.addActionConfig(SIMPLE_ACTION_NAME, config);
            config = new ActionConfig("noStack", com.opensymphony.xwork.NestedAction.class, null, null, null);
            packageContext.addActionConfig(NO_STACK_ACTION_NAME, config);
            config = new ActionConfig("stack", com.opensymphony.xwork.NestedAction.class, null, null, null);
            packageContext.addActionConfig(STACK_ACTION_NAME, config);
            packageContext.setNamespace(NAMESPACE);
            configurationManager.addPackageConfig("nestedActionTest", packageContext);
        }

        /**
        * Tells whether the ConfigurationProvider should reload its configuration
        * @return
        */
        public boolean needsReload() {
            return false;
        }
    }
}
