/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers.dsx;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationUtil;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultTypeConfig;
import com.opensymphony.xwork.xml.Path;
import com.opensymphony.xwork.xml.handlers.AddPartHandler;
import com.opensymphony.xwork.xml.handlers.ExpressionSetPropertyHandler;
import com.opensymphony.xwork.xml.handlers.ObjectCreateHandler;
import com.opensymphony.xwork.xml.handlers.PathLocationEnum;

import java.util.List;
import java.util.Map;


/**
 * PackageHandler
 * @author Jason Carreira
 * Created May 19, 2003 8:00:30 AM
 */
public class PackageHandler extends ObjectCreateHandler {
    //~ Instance fields ////////////////////////////////////////////////////////

    Configuration configuration;

    //~ Constructors ///////////////////////////////////////////////////////////

    public PackageHandler(Path path, Configuration configuration) {
        super(path, PackageConfig.class);
        this.configuration = configuration;
        registerHandles();
    }

    public PackageHandler(String s, Configuration configuration) {
        super(s, PackageConfig.class);
        this.configuration = configuration;
        registerHandles();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void endingPath(Path path) {
        PackageConfig config = (PackageConfig) getValueStack().pop();
        configuration.addPackageConfig(config.getName(), config);
    }

    public void startingPath(Path path, Map map) {
        super.startingPath(path, map);

        String extendString = (String) map.get("extends");
        List parents = ConfigurationUtil.buildParentsFromString(configuration, extendString);
        getPackageConfig().addAllParents(parents);
    }

    PackageConfig getPackageConfig() {
        return ((PackageConfig) getValueStack().peek());
    }

    private void addInterceptorHandlers(String interceptorPathString) {
        final String interceptorConfigPathString = interceptorPathString + "/interceptor";
        ObjectCreateHandler interceptorConfigHandler = new ObjectCreateHandler(interceptorConfigPathString, InterceptorConfig.class);
        interceptorConfigHandler.addAttributeMapping("class", "clazz");
        interceptorConfigHandler.registerWith(this);
        new ParamHandler(interceptorConfigPathString).registerWith(this);
        new AddPartHandler(interceptorConfigPathString, "addInterceptorConfig").registerWith(this);

        final String interceptorStackConfigPathString = interceptorPathString + "/interceptor-stack";
        new ObjectCreateHandler(interceptorStackConfigPathString, InterceptorStackConfig.class).registerWith(this);

        final String interceptorRefPathString = interceptorStackConfigPathString + "/interceptor-ref";
        new InterceptorRefHandler(interceptorRefPathString).registerWith(this);
        new AddPartHandler(interceptorStackConfigPathString, "addInterceptorStackConfig").registerWith(this);
    }

    private void addResultTypeHandlers(final String resultTypePathString) {
        ObjectCreateHandler resultTypeCreateHandler = new ObjectCreateHandler(resultTypePathString, ResultTypeConfig.class);
        resultTypeCreateHandler.addAttributeMapping("class", "clazz");
        resultTypeCreateHandler.registerWith(this);

        ExpressionSetPropertyHandler exSetPropertyHandler = new ExpressionSetPropertyHandler(resultTypePathString, "[1].defaultResultType", "name", PathLocationEnum.END);
        exSetPropertyHandler.setAttributeFlag("default");
        exSetPropertyHandler.registerWith(this);
        new AddPartHandler(resultTypePathString, "addResultTypeConfig").registerWith(this);
    }

    private void registerHandles() {
        final String resultTypePathString = "result-types/result-type";
        addResultTypeHandlers(resultTypePathString);

        final String interceptorPathString = "interceptors";
        addInterceptorHandlers(interceptorPathString);

        new DefaultInterceptorRefHandler("default-interceptor-ref").registerWith(this);

        final String globalResultPathString = "global-results/result";
        new ResultHandler(globalResultPathString, "addGlobalResultConfig").registerWith(this);

        final String actionConfigPathString = "action";
        new ActionConfigHandler(actionConfigPathString).registerWith(this);
    }
}
