/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ActionChainResult;
import com.opensymphony.xwork.ModelDrivenAction;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import com.opensymphony.xwork.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork.interceptor.ParametersInterceptor;
import com.opensymphony.xwork.interceptor.StaticParametersInterceptor;
import com.opensymphony.xwork.validator.ValidationInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * MockConfigurationProvider provides a simple configuration class without the need for xml files, etc. for simple testing.
 *
 * @author $author$
 * @version $Revision$
 */
public class MockConfigurationProvider implements ConfigurationProvider {

    public static final String FOO_ACTION_NAME = "foo";
    public static final String MODEL_DRIVEN_PARAM_TEST = "modelParamTest";
    public static final String MODEL_DRIVEN_PARAM_FILTER_TEST  = "modelParamFilterTest";
    public static final String PARAM_INTERCEPTOR_ACTION_NAME = "parametersInterceptorTest";
    public static final String VALIDATION_ACTION_NAME = "validationInterceptorTest";
    public static final String VALIDATION_ALIAS_NAME = "validationAlias";
    public static final String VALIDATION_SUBPROPERTY_NAME = "subproperty";


    /**
     * Allows the configuration to clean up any resources used
     */
    public void destroy() {
    }

    /**
     * Initializes the configuration object.
     */
    public void init(Configuration configurationManager) {
        PackageConfig defaultPackageContext = new PackageConfig("defaultPackage");
        HashMap params = new HashMap();
        params.put("bar", "5");

        HashMap results = new HashMap();
        HashMap successParams = new HashMap();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig("success", ActionChainResult.class, successParams));

        ActionConfig fooActionConfig = new ActionConfig(null, SimpleAction.class, params, results, null);
        fooActionConfig.setPackageName("defaultPackage");
        defaultPackageContext.addActionConfig(FOO_ACTION_NAME, fooActionConfig);

        results = new HashMap();
        successParams = new HashMap();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig("success", ActionChainResult.class, successParams));

        List interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));

        ActionConfig paramInterceptorActionConfig = new ActionConfig(null, SimpleAction.class, null, results, interceptors);
        paramInterceptorActionConfig.setPackageName("defaultPackage");
        defaultPackageContext.addActionConfig(PARAM_INTERCEPTOR_ACTION_NAME, paramInterceptorActionConfig);

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("model", new ModelDrivenInterceptor()));
        interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));

        ActionConfig modelParamActionConfig = new ActionConfig(null, ModelDrivenAction.class, null, null, interceptors);
        modelParamActionConfig.setPackageName("defaultPackage");
        defaultPackageContext.addActionConfig(MODEL_DRIVEN_PARAM_TEST, modelParamActionConfig);
        
        //List paramFilterInterceptor=new ArrayList();
        //paramFilterInterceptor.add(new ParameterFilterInterC)
        //ActionConfig modelParamFilterActionConfig = new ActionConfig(null, ModelDrivenAction.class, null, null, interceptors);
        

        results = new HashMap();
        successParams = new HashMap();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig("success", ActionChainResult.class, successParams));

        interceptors = new ArrayList();
        interceptors.add(new InterceptorMapping("static-params", new StaticParametersInterceptor()));
        interceptors.add(new InterceptorMapping("model", new ModelDrivenInterceptor()));
        interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));
        interceptors.add(new InterceptorMapping("validation", new ValidationInterceptor()));

        //Explicitly set an out-of-range date for DateRangeValidatorTest
        params = new HashMap();
        params.put("date", new java.util.Date(2002 - 1900, 11, 20));

        //Explicitly set an out-of-range double for DoubleRangeValidatorTest
        params.put("percentage", new Double(100.0123));

        ActionConfig validationActionConfig = new ActionConfig(null, SimpleAction.class, params, results, interceptors);
        validationActionConfig.setPackageName("defaultPackage");
        defaultPackageContext.addActionConfig(VALIDATION_ACTION_NAME, validationActionConfig);
        defaultPackageContext.addActionConfig(VALIDATION_ALIAS_NAME, validationActionConfig);
        defaultPackageContext.addActionConfig(VALIDATION_SUBPROPERTY_NAME, validationActionConfig);


        params = new HashMap();
        params.put("percentage", new Double(1.234567));
        ActionConfig percentageActionConfig = new ActionConfig(null, SimpleAction.class, params, results, interceptors);
        percentageActionConfig.setPackageName("defaultPackage");
        defaultPackageContext.addActionConfig("percentage", percentageActionConfig);

        // We need this actionconfig to be the final destination for action chaining
        ActionConfig barActionConfig = new ActionConfig(null, SimpleAction.class, null, null, null);
        barActionConfig.setPackageName("defaultPackage");
        defaultPackageContext.addActionConfig("bar", barActionConfig);

        configurationManager.addPackageConfig("defaultPackage", defaultPackageContext);
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration
     *
     * @return false
     */
    public boolean needsReload() {
        return false;
    }
}
