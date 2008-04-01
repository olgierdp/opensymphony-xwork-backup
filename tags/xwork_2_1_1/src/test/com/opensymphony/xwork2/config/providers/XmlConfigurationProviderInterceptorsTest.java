/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.interceptor.LoggingInterceptor;
import com.opensymphony.xwork2.interceptor.TimerInterceptor;
import com.opensymphony.xwork2.mock.MockInterceptor;

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

    InterceptorConfig loggingInterceptor = new InterceptorConfig.Builder("logging", LoggingInterceptor.class.getName()).build();
    InterceptorConfig mockInterceptor = new InterceptorConfig.Builder("mock", MockInterceptor.class.getName()).build();
    InterceptorConfig timerInterceptor = new InterceptorConfig.Builder("timer", TimerInterceptor.class.getName()).build();
    ObjectFactory objectFactory;
    
    public void setUp() throws Exception {
        super.setUp();
        objectFactory = container.getInstance(ObjectFactory.class);
    }


    public void testBasicInterceptors() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-interceptors-basic.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        // the test interceptor with a parameter
        Map params = new HashMap();
        params.put("foo", "expectedFoo");

        InterceptorConfig paramsInterceptor = new InterceptorConfig.Builder("test", MockInterceptor.class.getName())
            .addParams(params).build();

        // the default interceptor stack
        InterceptorStackConfig defaultStack = new InterceptorStackConfig.Builder("defaultStack")
                .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
                .addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, params)))
                .build();

        // the derivative interceptor stack
        InterceptorStackConfig derivativeStack = new InterceptorStackConfig.Builder("derivativeStack")
            .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
            .addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, params)))
            .addInterceptor(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())))
            .build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

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
        loadConfigurationProviders(new XmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-defaultref.xml"));

        // expectations - the inherited interceptor stack
        // default package
        ArrayList interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())));

        ActionConfig actionWithOwnRef = new ActionConfig.Builder("", "ActionWithOwnRef", SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .build();

        ActionConfig actionWithDefaultRef = new ActionConfig.Builder("", "ActionWithDefaultRef", SimpleAction.class.getName())
            .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
            .build();

        // sub package
        // this should inherit
        ActionConfig actionWithNoRef = new ActionConfig.Builder("", "ActionWithNoRef", SimpleAction.class.getName())
            .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
            .build();

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())));

        ActionConfig anotherActionWithOwnRef = new ActionConfig.Builder("", "AnotherActionWithOwnRef", SimpleAction.class.getName())
            .addInterceptor(new InterceptorMapping("logging", objectFactory.buildInterceptor(loggingInterceptor, new HashMap())))
            .build();

        RuntimeConfiguration runtimeConfig = configurationManager.getConfiguration().getRuntimeConfiguration();

        // assertions
        assertEquals(actionWithOwnRef, runtimeConfig.getActionConfig("", "ActionWithOwnRef"));
        assertEquals(actionWithDefaultRef, runtimeConfig.getActionConfig("", "ActionWithDefaultRef"));

        assertEquals(actionWithNoRef, runtimeConfig.getActionConfig("", "ActionWithNoRef"));
        assertEquals(anotherActionWithOwnRef, runtimeConfig.getActionConfig("", "AnotherActionWithOwnRef"));
    }

    public void testInterceptorInheritance() throws ConfigurationException {
        
        // expectations - the inherited interceptor stack
        InterceptorStackConfig inheritedStack = new InterceptorStackConfig.Builder("subDefaultStack")
            .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
            .build();

        ConfigurationProvider provider = buildConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-inheritance.xml");

        // assertions
        PackageConfig defaultPkg = configuration.getPackageConfig("default");
        assertEquals(2, defaultPkg.getInterceptorConfigs().size());

        PackageConfig subPkg = configuration.getPackageConfig("subPackage");
        assertEquals(1, subPkg.getInterceptorConfigs().size());
        assertEquals(3, subPkg.getAllInterceptorConfigs().size());
        assertEquals(inheritedStack, subPkg.getInterceptorConfigs().get("subDefaultStack"));

        // expectations - the inherited interceptor stack
        inheritedStack = new InterceptorStackConfig.Builder("subSubDefaultStack")
                .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
                .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
                .build();

        PackageConfig subSubPkg = configuration.getPackageConfig("subSubPackage");
        assertEquals(1, subSubPkg.getInterceptorConfigs().size());
        assertEquals(4, subSubPkg.getAllInterceptorConfigs().size());
        assertEquals(inheritedStack, subSubPkg.getInterceptorConfigs().get("subSubDefaultStack"));
    }


    public void testInterceptorParamOverriding() throws Exception {

        Map params = new HashMap();
        params.put("foo", "expectedFoo");
        params.put("expectedFoo", "expectedFooValue");

        InterceptorStackConfig defaultStack = new InterceptorStackConfig.Builder("defaultStack")
            .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
            .addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, params)))
            .build();

        ArrayList interceptors = new ArrayList();
        interceptors.addAll(defaultStack.getInterceptors());

        ActionConfig intAction = new ActionConfig.Builder("", "TestInterceptorParam", SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .build();

        // TestInterceptorParamOverride action tests that an interceptor with a param override worked
        HashMap interceptorParams = new HashMap();
        interceptorParams.put("expectedFoo", "expectedFooValue2");
        interceptorParams.put("foo", "foo123");
        interceptors = new ArrayList();

        InterceptorStackConfig defaultStack2 = new InterceptorStackConfig.Builder("defaultStack")
            .addInterceptor(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptor, new HashMap())))
            .addInterceptor(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptor, interceptorParams)))
            .build();

        interceptors = new ArrayList();

        interceptors.addAll(defaultStack2.getInterceptors());

        ActionConfig intOverAction = new ActionConfig.Builder("", "TestInterceptorParamOverride", SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .build();

        ConfigurationProvider provider = buildConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-params.xml");


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

}
