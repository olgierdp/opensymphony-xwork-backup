/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionChainResult;
import com.opensymphony.xwork.ModelDrivenAction;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.ParametersInterceptor;
import com.opensymphony.xwork.interceptor.StaticParametersInterceptor;
import com.opensymphony.xwork.validator.ValidationInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * TestConfigurationProvider provides a simple configuration class without the need for xml files, etc. for simple testing.
 *
 * @author $author$
 * @version $Revision$
 */
public class TestConfigurationProvider implements ConfigurationProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String FOO_ACTION_NAME = "foo";
    public static final String MODEL_DRIVEN_ACTION_NAME = "model";
    public static final String PARAM_INTERCEPTOR_ACTION_NAME = "parametersInterceptorTest";
    public static final String VALIDATION_ACTION_NAME = "validationInterceptorTest";
    public static final String VALIDATION_ALIAS_NAME = "validationAlias";

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Allows the configuration to clean up any resources used
    */
    public void destroy() {
    }

    /**
    * Initializes the configuration object.
    */
    public void init(Configuration configurationManager) {
        PackageConfig defaultPackageConfig = new PackageConfig();

        HashMap results = new HashMap();
        HashMap successParams = new HashMap();
        successParams.put("actionName", "bar");

        ResultConfig resultConfig = new ResultConfig("chain", ActionChainResult.class, successParams);
        results.put(Action.SUCCESS, resultConfig);

        HashMap params = new HashMap();
        params.put("bar", "5");

        ActionConfig fooActionConfig = new ActionConfig(null, SimpleAction.class, params, results, null);
        defaultPackageConfig.addActionConfig(FOO_ACTION_NAME, fooActionConfig);

        results = new HashMap();
        successParams = new HashMap();
        successParams.put("actionName", "bar");
        resultConfig = new ResultConfig("chain", ActionChainResult.class, successParams);
        results.put(Action.SUCCESS, resultConfig);

        List interceptors = new ArrayList();
        interceptors.add(new ParametersInterceptor());

        ActionConfig paramInterceptorActionConfig = new ActionConfig(null, SimpleAction.class, null, results, interceptors);
        defaultPackageConfig.addActionConfig(PARAM_INTERCEPTOR_ACTION_NAME, paramInterceptorActionConfig);

        results = new HashMap();
        results.put(Action.SUCCESS, ActionChainResult.class);

        results = new HashMap();
        successParams = new HashMap();
        successParams.put("actionName", "bar");
        resultConfig = new ResultConfig("chain", ActionChainResult.class, successParams);
        results.put(Action.SUCCESS, resultConfig);

        interceptors = new ArrayList();
        interceptors.add(new StaticParametersInterceptor());
        interceptors.add(new ParametersInterceptor());
        interceptors.add(new ValidationInterceptor());

        ActionConfig validationActionConfig = new ActionConfig(null, SimpleAction.class, null, results, interceptors);
        defaultPackageConfig.addActionConfig(VALIDATION_ACTION_NAME, validationActionConfig);
        defaultPackageConfig.addActionConfig(VALIDATION_ALIAS_NAME, validationActionConfig);

        ActionConfig modelActionConfig = new ActionConfig(null, ModelDrivenAction.class, null, null, null);
        defaultPackageConfig.addActionConfig(MODEL_DRIVEN_ACTION_NAME, modelActionConfig);

        // We need this actionconfig to be the final destination for action chaining
        ActionConfig barActionConfig = new ActionConfig(null, SimpleAction.class, null, null, null);
        defaultPackageConfig.addActionConfig("bar", barActionConfig);

        configurationManager.addPackageConfig("defaultPackage", defaultPackageConfig);
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration
     * @return
     */
    public boolean needsReload() {
        return false;
    }
}
