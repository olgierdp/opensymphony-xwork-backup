/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.mock.MockInterceptor;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.RuntimeConfiguration;
import com.opensymphony.xwork.config.entities.*;
import com.opensymphony.xwork.interceptor.LoggingInterceptor;
import com.opensymphony.xwork.interceptor.TimerInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderInterceptorsTest extends ConfigurationTestBase {

    InterceptorConfig loggingInterceptor = new InterceptorConfig("logging", LoggingInterceptor.class, new HashMap());
    InterceptorConfig mockInterceptor = new InterceptorConfig("mock", MockInterceptor.class, new HashMap());
    InterceptorConfig timerInterceptor = new InterceptorConfig("timer", TimerInterceptor.class, new HashMap());
    ObjectFactory objectFactory = ObjectFactory.getObjectFactory();


    public void testBasicInterceptors() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-interceptors-basic.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        // the test interceptor with a parameter
        Map params = new HashMap();
        params.put("foo", "expectedFoo");

        InterceptorConfig paramsInterceptor = new InterceptorConfig("test", MockInterceptor.class, params);

        // the default interceptor stack
        InterceptorStackConfig defaultStack = new InterceptorStackConfig("defaultStack");
        defaultStack.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));
        defaultStack.addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, params)));

        // the derivative interceptor stack
        InterceptorStackConfig derivativeStack = new InterceptorStackConfig("derivativeStack");
        derivativeStack.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));
        derivativeStack.addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, params)));
        derivativeStack.addInterceptor(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())));

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map interceptorConfigs = pkg.getInterceptorConfigs();

        // assertions for size
        assertEquals(5, interceptorConfigs.size());

        // assertions for interceptors
        assertEquals(timerInterceptor, interceptorConfigs.get("timer"));
        assertEquals(loggingInterceptor, interceptorConfigs.get("logging"));
        assertEquals(paramsInterceptor, interceptorConfigs.get("test"));

        // assertions for interceptor stacks
        assertEquals(defaultStack, interceptorConfigs.get("defaultStack"));
        assertEquals(derivativeStack, interceptorConfigs.get("derivativeStack"));
    }

    public void testInterceptorDefaultRefs() throws ConfigurationException {
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork/config/providers/xwork-test-interceptor-defaultref.xml");
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(provider);

        // expectations - the inherited interceptor stack
        // default package
        ArrayList interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())));

        ActionConfig actionWithOwnRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));

        ActionConfig actionWithDefaultRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        // sub package
        // this should inherit
        ActionConfig actionWithNoRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())));

        ActionConfig anotherActionWithOwnRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        RuntimeConfiguration runtimeConfig = ConfigurationManager.getConfiguration().getRuntimeConfiguration();

        // assertions
        assertEquals(actionWithOwnRef, runtimeConfig.getActionConfig("", "ActionWithOwnRef"));
        assertEquals(actionWithDefaultRef, runtimeConfig.getActionConfig("", "ActionWithDefaultRef"));

        assertEquals(actionWithNoRef, runtimeConfig.getActionConfig("", "ActionWithNoRef"));
        assertEquals(anotherActionWithOwnRef, runtimeConfig.getActionConfig("", "AnotherActionWithOwnRef"));
    }

    public void testInterceptorInheritance() throws ConfigurationException {
        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork/config/providers/xwork-test-interceptor-inheritance.xml");

        // expectations - the inherited interceptor stack
        InterceptorStackConfig inheritedStack = new InterceptorStackConfig("subDefaultStack");
        inheritedStack.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));

        provider.init(configuration);

        // assertions
        PackageConfig defaultPkg = configuration.getPackageConfig("default");
        assertEquals(2, defaultPkg.getInterceptorConfigs().size());

        PackageConfig subPkg = configuration.getPackageConfig("subPackage");
        assertEquals(1, subPkg.getInterceptorConfigs().size());
        assertEquals(3, subPkg.getAllInterceptorConfigs().size());
        assertEquals(inheritedStack, subPkg.getInterceptorConfigs().get("subDefaultStack"));

        // expectations - the inherited interceptor stack
        inheritedStack = new InterceptorStackConfig("subSubDefaultStack");
        inheritedStack.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));
        inheritedStack.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));

        PackageConfig subSubPkg = configuration.getPackageConfig("subSubPackage");
        assertEquals(1, subSubPkg.getInterceptorConfigs().size());
        assertEquals(4, subSubPkg.getAllInterceptorConfigs().size());
        assertEquals(inheritedStack, subSubPkg.getInterceptorConfigs().get("subSubDefaultStack"));
    }


    public void testInterceptorParamOverriding() throws Exception {

        XmlConfigurationProvider provider = new XmlConfigurationProvider("com/opensymphony/xwork/config/providers/xwork-test-interceptor-params.xml");

        Map params = new HashMap();
        params.put("foo", "expectedFoo");
        params.put("expectedFoo", "expectedFooValue");

        InterceptorStackConfig defaultStack = new InterceptorStackConfig("defaultStack");
        defaultStack.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));
        defaultStack.addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, params)));

        ArrayList interceptors = new ArrayList();
        interceptors.addAll(defaultStack.getInterceptors());

        ActionConfig intAction = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        // TestInterceptorParamOverride action tests that an interceptor with a param override worked
        HashMap interceptorParams = new HashMap();
        interceptorParams.put("expectedFoo", "expectedFooValue2");
        interceptorParams.put("foo", "foo123");
        interceptors = new ArrayList();

        InterceptorStackConfig defaultStack2 = new InterceptorStackConfig("defaultStack");
        defaultStack2.addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())));
        defaultStack2.addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, interceptorParams)));

        interceptors = new ArrayList();

        interceptors.addAll(defaultStack2.getInterceptors());

        ActionConfig intOverAction = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(2, actionConfigs.size());
        assertEquals(intAction, actionConfigs.get("TestInterceptorParam"));
        assertEquals(intOverAction, actionConfigs.get("TestInterceptorParamOverride"));

        ActionConfig ac = (ActionConfig) actionConfigs.get("TestInterceptorParamOverride");
        assertEquals(defaultStack.getInterceptors(), ac.getInterceptors());

        ActionConfig ac2 = (ActionConfig) actionConfigs.get("TestInterceptorParam");
        assertEquals(defaultStack2.getInterceptors(), ac2.getInterceptors());

    }

    protected void setUp() throws Exception {
        super.setUp();

        ConfigurationManager.destroyConfiguration();
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        ConfigurationManager.destroyConfiguration();
    }
}
