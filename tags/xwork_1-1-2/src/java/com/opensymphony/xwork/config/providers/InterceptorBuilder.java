/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
 * 
 * @author Mike
 * @author Rainer Hermanns
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
                result.add(new InterceptorMapping(refName, ObjectFactory.getObjectFactory().buildInterceptor((InterceptorConfig) referencedConfig, refParams)));
            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    result = constructParameterizedInterceptorReferences(packageConfig, stackConfig, refParams);
                } else {
                    result.addAll(stackConfig.getInterceptors());
                }

            } else {
                LOG.error("Got unexpected type for interceptor " + refName + ". Got " + referencedConfig);
            }
        }

        return result;
    }

    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig overriding the properties
     * of the referenced interceptor with refParams.
     * 
     * @param packageConfig
     * @param stackConfig
     * @param refParams The overridden interceptor properies
     * @return list of interceptors referenced by the refName in the supplied PackageConfig overridden with refParams.
     */
    private static List constructParameterizedInterceptorReferences(PackageConfig packageConfig, InterceptorStackConfig stackConfig, Map refParams) {
        List result;
        Map params = new HashMap();

        for ( Iterator iter = refParams.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String value = (String) refParams.get(key);

            try {
                String name = key.substring(0, key.indexOf('.'));
                key = key.substring(key.indexOf('.')  + 1);

                Map map;
                if ( params.containsKey(name)) {
                    map = (Map) params.get(name);
                } else {
                    map = new HashMap();
                }

                map.put(key, value);
                params.put(name, map);

            } catch (Exception e) {
                LOG.warn("No interceptor found for name = " + key);
            }
        }

        result = (ArrayList) stackConfig.getInterceptors();

        for ( Iterator iter = params.keySet().iterator(); iter.hasNext();) {

            String key = (String) iter.next();
            Map map = (Map) params.get(key);

            InterceptorConfig cfg = (InterceptorConfig) packageConfig.getAllInterceptorConfigs().get(key);
            Interceptor interceptor = ObjectFactory.getObjectFactory().buildInterceptor(cfg, map);

            InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
            if ( result != null && result.contains(mapping)) {
                int index = result.indexOf(mapping);
                result.set(index, mapping);
            } else {
                result.add(mapping);
            }
        }

        return result;
    }
}
