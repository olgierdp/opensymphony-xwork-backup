/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
 * 
 * @author Mike
 */
public class InterceptorBuilder {

    private static final Log LOG = LogFactory.getLog(InterceptorBuilder.class);


    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
     *
     * @param packageConfig
     * @param refName
     * @param refParams
     * @return list of interceptors referenced by the refName in the supplied PackageConfig.
     * @throws ConfigurationException
     */
    public static List constructInterceptorReference(PackageConfig packageConfig, String refName, Map refParams) throws ConfigurationException {
        Object referencedConfig = packageConfig.getAllInterceptorConfigs().get(refName);
        List result = new ArrayList();

        if (referencedConfig == null) {
            LOG.error("Unable to find interceptor class referenced by ref-name " + refName);
        } else {
            if (referencedConfig instanceof InterceptorConfig) {
                result.add(ObjectFactory.getObjectFactory().buildInterceptor((InterceptorConfig) referencedConfig, refParams));
            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    LOG.warn("Interceptor-ref params are being ignored because they are applied to an Interceptor-Stack reference. Ref name = " + refName + ", params = " + refParams);
                }

                result.addAll(stackConfig.getInterceptors());
            } else {
                LOG.error("Got unexpected type for interceptor " + refName + ". Got " + referencedConfig);
            }
        }

        return result;
    }
}
