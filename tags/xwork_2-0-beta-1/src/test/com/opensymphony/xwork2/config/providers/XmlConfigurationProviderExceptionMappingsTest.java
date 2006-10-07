package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.mock.MockResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Aug 15, 2005
 * Time: 2:05:36 PM
 */
public class XmlConfigurationProviderExceptionMappingsTest extends ConfigurationTestBase {

    public void testActions() throws ConfigurationException {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-exception-mappings.xml";
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        List exceptionMappings = new ArrayList();
        HashMap parameters = new HashMap();
        HashMap results = new HashMap();

        exceptionMappings.add(new ExceptionMappingConfig("spooky-result", "com.opensymphony.xwork2.SpookyException", "spooky-result"));

        results.put("spooky-result", new ResultConfig("spooky-result", MockResult.class.getName(), new HashMap()));

        Map resultParams = new HashMap();
        resultParams.put("actionName", "bar.vm");
        results.put("specificLocationResult", new ResultConfig("specificLocationResult", ActionChainResult.class.getName(), resultParams));

        ActionConfig expectedAction = new ActionConfig(null, SimpleAction.class, parameters, results, new ArrayList(), exceptionMappings);

        // execute the configuration
        provider.init(configuration);

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map actionConfigs = pkg.getActionConfigs();

        // assertions
        assertEquals(1, actionConfigs.size());

        ActionConfig action = (ActionConfig) actionConfigs.get("Bar");
        assertEquals(expectedAction, action);
    }

}
