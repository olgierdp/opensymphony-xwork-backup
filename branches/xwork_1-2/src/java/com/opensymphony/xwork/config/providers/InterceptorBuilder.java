/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
 * 
 * @author Mike
 * @author Rainer Hermanns
 * @author tmjee
 * 
 * @version $Date$ $Id$
 */
public class InterceptorBuilder {

    private static final Log LOG = LogFactory.getLog(InterceptorBuilder.class);


    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig (InterceptorMapping object).
     *
     * @param packageConfig
     * @param refName
     * @param refParams
     * @return list of interceptors referenced by the refName in the supplied PackageConfig (InterceptorMapping object).
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
    private static List constructParameterizedInterceptorReferences(PackageConfig packageConfig, 
    		InterceptorStackConfig stackConfig, Map refParams) {
        Set result;
        Map params = new HashMap();
        
        /*
         * We strip
         * 
         * <interceptor-ref name="someStack">
         *    <param name="interceptor1.param1">someValue</param>
         *    <param name="interceptor1.param2">anotherValue</param>
         * </interceptor-ref>
         * 
         * down to map 
         *  interceptor1 -> [param1 -> someValue, param2 -> anotherValue]
         * 
         * or
         * <interceptor-ref name="someStack">
         *    <param name="interceptorStack1.interceptor1.param1">someValue</param>
         *    <param name="interceptorStack1.interceptor1.param2">anotherValue</param>
         * </interceptor-ref>
         * 
         * down to map
         *  interceptorStack1 -> [interceptor1.param1 -> someValue, interceptor1.param2 -> anotherValue]
         * 
         */
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

        result = new LinkedHashSet(stackConfig.getInterceptors());

        for ( Iterator iter = params.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            Map map = (Map) params.get(key);
            
            Object interceptorCfgObj = packageConfig.getAllInterceptorConfigs().get(key);
            
            /*
             * Now we attempt to separate out param that refers to Interceptor 
             * and Interceptor stack, eg.
             * 
             * <interceptor-ref name="someStack">
             *    <param name="interceptor1.param1">someValue</param>
             *    ...
             * </interceptor-ref>
             * 
             *  vs
             *  
             *  <interceptor-ref name="someStack">
             *    <param name="interceptorStack1.interceptor1.param1">someValue</param>
             *    ...
             *  </interceptor-ref>
             */
            if(interceptorCfgObj instanceof InterceptorConfig) {  //  interceptor-ref param refer to an interceptor
            	InterceptorConfig cfg = (InterceptorConfig) interceptorCfgObj;
            	Interceptor interceptor = ObjectFactory.getObjectFactory().buildInterceptor(cfg, map);

            	InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
            	if ( result != null && result.contains(mapping)) {
            		// if an existing interceptor mapping exists, 
            		// we remove from the result Set, just to make sure 
            		// there's always one unique mapping.
            		result.remove(mapping);
            	}
                result.add(mapping);
            }
            else if (interceptorCfgObj instanceof InterceptorStackConfig){  // interceptor-ref param refer to an interceptor stack
            	
            	// If its an interceptor-stack, we call this method recursively untill, 
            	// all the params (eg. interceptorStack1.interceptor1.param etc.) 
            	// are resolved down to a specific interceptor.
            	
            	InterceptorStackConfig stackCfg = (InterceptorStackConfig) interceptorCfgObj;
            	List tmpResult = constructParameterizedInterceptorReferences(packageConfig, stackCfg, map);
            	for (Iterator i = tmpResult.iterator(); i.hasNext(); ) {
            		InterceptorMapping tmpInterceptorMapping = (InterceptorMapping) i.next();
            		if (result.contains(tmpInterceptorMapping)) {
            			result.remove(tmpInterceptorMapping);
            		}
            		result.add(tmpInterceptorMapping);
            	}
            }
        }
        return new ArrayList(result);
    }
}
