/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ActionChainResult;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.mock.MockResult;

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
public class XmlConfigurationProviderResultsTest extends ConfigurationTestBase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-results.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        HashMap parameters = new HashMap();
        HashMap results = new HashMap();

        results.put("chainDefaultTypedResult", new ResultConfig("chainDefaultTypedResult", ActionChainResult.class, new HashMap()));

        results.put("mockTypedResult", new ResultConfig("mockTypedResult", MockResult.class, new HashMap()));

        Map resultParams = new HashMap();
        resultParams.put("location", "bar.vm");
        results.put("specificLocationResult", new ResultConfig("specificLocationResult", ActionChainResult.class, resultParams));

        resultParams = new HashMap();
        resultParams.put("location", "foo.vm");
        results.put("defaultLocationResult", new ResultConfig("defaultLocationResult", ActionChainResult.class, resultParams));

        resultParams = new HashMap();
        resultParams.put("foo", "bar");
        results.put("noDefaultLocationResult", new ResultConfig("noDefaultLocationResult", ActionChainResult.class, resultParams));

        ActionConfig expectedAction = new ActionConfig(null, SimpleAction.class, parameters, results, new ArrayList());

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(1, actionConfigs.size());

        ActionConfig action = (ActionConfig) actionConfigs.get("Bar");
        assertEquals(expectedAction, action);
    }

    public void testResultInheritance() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-result-inheritance.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // expectations
        provider.init(configuration);

        // assertions
        PackageConfig subPkg = configuration.getPackageConfig("subPackage");
        assertEquals(1, subPkg.getResultTypeConfigs().size());
        assertEquals(3, subPkg.getAllResultTypeConfigs().size());
    }

    public void testResultTypes() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork/config/providers/xwork-test-results.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        // setup expectations
        ResultTypeConfig chainResult = new ResultTypeConfig("chain", ActionChainResult.class);
        ResultTypeConfig mockResult = new ResultTypeConfig("mock", MockResult.class);

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map resultTypes = pkg.getResultTypeConfigs();

        // assertions
        assertEquals(2, resultTypes.size());
        assertEquals("chain", pkg.getDefaultResultType());
        assertEquals(chainResult, resultTypes.get("chain"));
        assertEquals(mockResult, resultTypes.get("mock"));
    }
}
