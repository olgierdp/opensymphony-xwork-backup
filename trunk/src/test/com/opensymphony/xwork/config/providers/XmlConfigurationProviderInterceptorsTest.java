/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.MockInterceptor;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.RuntimeConfiguration;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
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
    //~ Methods ////////////////////////////////////////////////////////////////

    protected void setUp() throws Exception {
        super.setUp();

        ConfigurationManager.destroyConfiguration();
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        ConfigurationManager.destroyConfiguration();
    }

    public void testBasicInterceptors() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-interceptors-basic.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        // the timer & logging interceptors
        InterceptorConfig timerInterceptor = new InterceptorConfig("timer", TimerInterceptor.class, new HashMap());
        InterceptorConfig loggingInterceptor = new InterceptorConfig("logging", LoggingInterceptor.class, new HashMap());

        // the test interceptor with a parameter
        Map params = new HashMap();
        params.put("foo", "expectedFoo");

        InterceptorConfig paramsInterceptor = new InterceptorConfig("test", MockInterceptor.class, params);

        // the default interceptor stack
        InterceptorStackConfig defaultStack = new InterceptorStackConfig("defaultStack");
        defaultStack.addInterceptor(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));
        defaultStack.addInterceptor(InterceptorBuilder.buildInterceptor(MockInterceptor.class, params));

        // the derivative interceptor stack
        InterceptorStackConfig derivativeStack = new InterceptorStackConfig("derivativeStack");
        derivativeStack.addInterceptor(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));
        derivativeStack.addInterceptor(InterceptorBuilder.buildInterceptor(MockInterceptor.class, params));
        derivativeStack.addInterceptor(InterceptorBuilder.buildInterceptor(LoggingInterceptor.class, new HashMap()));

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
        interceptors.add(InterceptorBuilder.buildInterceptor(LoggingInterceptor.class, new HashMap()));

        ActionConfig actionWithOwnRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        interceptors = new ArrayList();
        interceptors.add(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));

        ActionConfig actionWithDefaultRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        // sub package
        // this should inherit
        ActionConfig actionWithNoRef = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        interceptors = new ArrayList();
        interceptors.add(InterceptorBuilder.buildInterceptor(LoggingInterceptor.class, new HashMap()));

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
        inheritedStack.addInterceptor(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));

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
        inheritedStack.addInterceptor(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));
        inheritedStack.addInterceptor(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));

        PackageConfig subSubPkg = configuration.getPackageConfig("subSubPackage");
        assertEquals(1, subSubPkg.getInterceptorConfigs().size());
        assertEquals(4, subSubPkg.getAllInterceptorConfigs().size());
        assertEquals(inheritedStack, subSubPkg.getInterceptorConfigs().get("subSubDefaultStack"));
    }
}
