/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.mock.MockInterceptor;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.*;
import com.opensymphony.xwork2.interceptor.TimerInterceptor;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Mike
 * @author Rainer Hermanns
 */
public class XmlConfigurationProviderActionsTest extends ConfigurationTestBase {

    private List interceptors;
    private List exceptionMappings;
    private Map params;
    private Map results;
    private ObjectFactory objectFactory;


    public void testActions() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-actions.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        // bar action is very simple, just two params
        params.put("foo", "17");
        params.put("bar", "23");
        params.put("testXW412", "foo.jspa?fooID=${fooID}&something=bar");
        params.put("testXW412Again", "something");


        ActionConfig barAction = new ActionConfig.Builder("", "Bar", SimpleAction.class.getName())
            .addParams(params).build();

        // foo action is a little more complex, two params, a result and an interceptor stack
        results = new HashMap();
        params = new HashMap();
        params.put("foo", "18");
        params.put("bar", "24");
        results.put("success", new ResultConfig.Builder("success", MockResult.class.getName()).build());

        InterceptorConfig timerInterceptorConfig = new InterceptorConfig.Builder("timer", TimerInterceptor.class.getName()).build();
        interceptors.add(new InterceptorMapping("timer", objectFactory.buildInterceptor(timerInterceptorConfig, new HashMap())));

        ActionConfig fooAction = new ActionConfig.Builder("", "Foo", SimpleAction.class.getName())
            .addParams(params)
            .addResultConfigs(results)
            .addInterceptors(interceptors)
            .build();

        // wildcard action is simple wildcard example
        results = new HashMap();
        results.put("*", new ResultConfig.Builder("*", MockResult.class.getName()).build());

        ActionConfig wildcardAction = new ActionConfig.Builder("", "WildCard", SimpleAction.class.getName())
            .addResultConfigs(results)
            .addInterceptors(interceptors)
            .build();

        // fooBar action is a little more complex, two params, a result and an interceptor stack
        params = new HashMap();
        params.put("foo", "18");
        params.put("bar", "24");
        results = new HashMap();
        results.put("success", new ResultConfig.Builder("success", MockResult.class.getName()).build());

        ExceptionMappingConfig exceptionConfig = new ExceptionMappingConfig.Builder("runtime", "java.lang.RuntimeException", "exception")
                .build();
        exceptionMappings.add(exceptionConfig);

        ActionConfig fooBarAction = new ActionConfig.Builder("", "FooBar", SimpleAction.class.getName())
            .addParams(params)
            .addResultConfigs(results)
            .addInterceptors(interceptors)
            .addExceptionMappings(exceptionMappings)
            .build();

        // TestInterceptorParam action tests that an interceptor worked
        HashMap interceptorParams = new HashMap();
        interceptorParams.put("expectedFoo", "expectedFooValue");
        interceptorParams.put("foo", MockInterceptor.DEFAULT_FOO_VALUE);

        InterceptorConfig mockInterceptorConfig = new InterceptorConfig.Builder("test", MockInterceptor.class.getName()).build();
        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptorConfig, interceptorParams)));

        ActionConfig intAction = new ActionConfig.Builder("", "TestInterceptorParam", SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .build();

        // TestInterceptorParamOverride action tests that an interceptor with a param override worked
        interceptorParams = new HashMap();
        interceptorParams.put("expectedFoo", "expectedFooValue");
        interceptorParams.put("foo", "foo123");
        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("test", objectFactory.buildInterceptor(mockInterceptorConfig, interceptorParams)));

        ActionConfig intOverAction = new ActionConfig.Builder("", "TestInterceptorParamOverride", SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .build();

        // execute the configuration
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(6, actionConfigs.size());
        assertEquals(barAction, actionConfigs.get("Bar"));
        assertEquals(fooAction, actionConfigs.get("Foo"));
        assertEquals(wildcardAction, actionConfigs.get("WildCard"));
        assertEquals(fooBarAction, actionConfigs.get("FooBar"));
        assertEquals(intAction, actionConfigs.get("TestInterceptorParam"));
        assertEquals(intOverAction, actionConfigs.get("TestInterceptorParamOverride"));
    }

    public void testInvalidActions() throws Exception {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-action-invalid.xml";

        try {
            ConfigurationProvider provider = buildConfigurationProvider(filename);
            fail("Should have thrown an exception");
        } catch (ConfigurationException ex) {
            // it worked correctly
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        params = new HashMap();
        results = new HashMap();
        interceptors = new ArrayList();
        exceptionMappings = new ArrayList();
        this.objectFactory = container.getInstance(ObjectFactory.class);
    }
}
