/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.MockInterceptor;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.TimerInterceptor;
import com.opensymphony.xwork.mock.MockResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:10:16 PM
 * To change this template use Options | File Templates.
 */
public class XmlConfigurationProviderActionsTest extends ConfigurationTestBase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private List interceptors;
    private Map params;
    private Map results;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-actions.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        // bar action is very simple, just two params
        params.put("foo", "17");
        params.put("bar", "23");

        ActionConfig barAction = new ActionConfig(null, SimpleAction.class, params, new HashMap(), new ArrayList());

        // foo action is a little more complex, two params, a result and an interceptor stack
        params = new HashMap();
        params.put("foo", "18");
        params.put("bar", "24");
        results.put("success", new ResultConfig("success", MockResult.class, new HashMap()));
        interceptors.add(InterceptorBuilder.buildInterceptor(TimerInterceptor.class, new HashMap()));

        ActionConfig fooAction = new ActionConfig(null, SimpleAction.class, params, results, interceptors);

        // TestInterceptorParam action tests that an interceptor worked
        HashMap interceptorParams = new HashMap();
        interceptorParams.put("expectedFoo", "expectedFooValue");
        interceptorParams.put("foo", MockInterceptor.DEFAULT_FOO_VALUE);
        interceptors = new ArrayList();
        interceptors.add(InterceptorBuilder.buildInterceptor(MockInterceptor.class, interceptorParams));

        ActionConfig intAction = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        // TestInterceptorParamOverride action tests that an interceptor with a param override worked
        interceptorParams = new HashMap();
        interceptorParams.put("expectedFoo", "expectedFooValue");
        interceptorParams.put("foo", "foo123");
        interceptors = new ArrayList();
        interceptors.add(InterceptorBuilder.buildInterceptor(MockInterceptor.class, interceptorParams));

        ActionConfig intOverAction = new ActionConfig(null, SimpleAction.class, new HashMap(), new HashMap(), interceptors);

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(4, actionConfigs.size());
        assertEquals(barAction, actionConfigs.get("Bar"));
        assertEquals(fooAction, actionConfigs.get("Foo"));
        assertEquals(intAction, actionConfigs.get("TestInterceptorParam"));
        assertEquals(intOverAction, actionConfigs.get("TestInterceptorParamOverride"));
    }

    protected void setUp() throws Exception {
        super.setUp();
        params = new HashMap();
        results = new HashMap();
        interceptors = new ArrayList();
    }
}
