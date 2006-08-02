/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockInterceptor;

import java.util.HashMap;
import java.util.Map;


/**
 * ConfigurationTest
 * <p/>
 * Created : Jan 27, 2003 1:30:08 AM
 *
 * @author Jason Carreira
 */
public class ConfigurationTest extends XWorkTestCase {

    public void testAbstract() {
        try {
            ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "/abstract", "test", null);
            fail();
        } catch (Exception e) {
            // this is what we expected
        }

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "/nonAbstract", "test", null);
            assertTrue(proxy.getActionName().equals("test"));
            assertTrue(proxy.getConfig().getClassName().equals(SimpleAction.class.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDefaultNamespace() {
        HashMap params = new HashMap();
        params.put("blah", "this is blah");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "/does/not/exist", "Foo", extraContext);
            proxy.execute();
            assertEquals("this is blah", proxy.getInvocation().getStack().findValue("[1].blah"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFileIncludeLoader() {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        // check entityTest package
        assertNotNull(configuration.getActionConfig("includeTest", "includeTest"));

        // check inheritance from Default
        assertNotNull(configuration.getActionConfig("includeTest", "Foo"));
    }
    
    public void testWildcardName() {
        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        ActionConfig config = configuration.getActionConfig("", "WildCard/Simple/input");
        
        assertNotNull(config);
        assertTrue("Wrong class name, "+config.getClassName(), 
                "com.opensymphony.xwork2.SimpleAction".equals(config.getClassName()));
        assertTrue("Wrong method name", "input".equals(config.getMethodName()));
        
        Map<String, Object> p = config.getParams();
        assertTrue("Wrong parameter, "+p.get("foo"), "Simple".equals(p.get("foo")));
        assertTrue("Wrong parameter, "+p.get("bar"), "input".equals(p.get("bar")));
    }

    public void testGlobalResults() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "", "Foo", null);
            assertNotNull(proxy.getConfig().getResults().get("login"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamInehritanceOverride() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "/foo/bar", "TestInterceptorParamInehritanceOverride", null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) ((InterceptorMapping) proxy.getConfig().getInterceptors().get(0)).getInterceptor();
            assertEquals("foo123", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamInheritance() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "/foo/bar", "TestInterceptorParamInheritance", null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) ((InterceptorMapping) proxy.getConfig().getInterceptors().get(0)).getInterceptor();
            assertEquals("expectedFoo", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParamOverride() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "", "TestInterceptorParamOverride", null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) ((InterceptorMapping) proxy.getConfig().getInterceptors().get(0)).getInterceptor();
            assertEquals("foo123", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testInterceptorParams() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "", "TestInterceptorParam", null);
            assertEquals(1, proxy.getConfig().getInterceptors().size());

            MockInterceptor testInterceptor = (MockInterceptor) ((InterceptorMapping) proxy.getConfig().getInterceptors().get(0)).getInterceptor();
            assertEquals("expectedFoo", testInterceptor.getExpectedFoo());
            proxy.execute();
            assertTrue(testInterceptor.isExecuted());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMultipleConfigProviders() {
        configurationManager.addConfigurationProvider(new MockConfigurationProvider());

        try {
            configurationManager.reload();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            fail();
        }

        RuntimeConfiguration configuration = configurationManager.getConfiguration().getRuntimeConfiguration();

        // check that it has configuration from xml
        assertNotNull(configuration.getActionConfig("/foo/bar", "Bar"));

        // check that it has configuration from MockConfigurationProvider
        assertNotNull(configuration.getActionConfig("", MockConfigurationProvider.FOO_ACTION_NAME));
    }

    public void testMultipleInheritance() {
        try {
            ActionProxy proxy;
            proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "multipleInheritance", "test", null);
            assertNotNull(proxy);
            proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "multipleInheritance", "Foo", null);
            assertNotNull(proxy);
            proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "multipleInheritance", "testMultipleInheritance", null);
            assertNotNull(proxy);
            assertEquals(6, proxy.getConfig().getInterceptors().size());
            assertEquals(2, proxy.getConfig().getResults().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testPackageExtension() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                    configurationManager.getConfiguration(), "/foo/bar", "Bar", null);
            assertEquals(6, proxy.getConfig().getInterceptors().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider c = new XmlConfigurationProvider("xwork-sample.xml");
        configurationManager.addConfigurationProvider(c);
        configurationManager.reload();
    }
}
