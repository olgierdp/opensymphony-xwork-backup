/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.util.OgnlUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 9:45:43 AM
 * To change this template use Options | File Templates.
 */
public class InterceptorBuilder {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Log LOG = LogFactory.getLog(InterceptorBuilder.class);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Builds an Interceptor instance for the given class name and parameterizes it. The interceptor will have its init()
    * method called before returning.
    * @param interceptorClass the Interceptor class to create an instance of
    * @param params the params to set on the interceptor instance
    * @return an instance of the Interceptor class or null if there is a problem loading the Interceptor class
    * @throws com.opensymphony.xwork.config.ConfigurationException
    */
    public static Interceptor buildInterceptor(Class interceptorClass, Map params) throws ConfigurationException {
        try {
            Interceptor interceptor = (Interceptor) interceptorClass.newInstance();
            OgnlUtil.setProperties(params, interceptor);
            interceptor.init();

            return interceptor;
        } catch (InstantiationException e) {
            LOG.error("Unable to instantiate an instance of Interceptor class [" + interceptorClass.getName() + "].");
        } catch (IllegalAccessException e) {
            LOG.error("IllegalAccessException while attempting to instantiate an instance of Interceptor class [" + interceptorClass.getName() + "].");
        } catch (ClassCastException e) {
            LOG.error("Class [" + interceptorClass.getName() + "] does not implement com.opensymphony.xwork.interceptor.Interceptor");
        } catch (Exception e) {
            throw new ConfigurationException("Caught Exception while registering Interceptor class " + interceptorClass.getName(), e);
        }

        return null;
    }

    /**
    * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
    * @param packageConfig
    * @param refName
    * @param refParams
    * @return
    * @throws ConfigurationException
    */
    public static List constructInterceptorReference(PackageConfig packageConfig, String refName, Map refParams) throws ConfigurationException {
        Object referencedConfig = packageConfig.getAllInterceptorConfigs().get(refName);
        List result = new ArrayList();

        if (referencedConfig == null) {
            LOG.error("Unable to find interceptor class referenced by ref-name " + refName);
        } else {
            if (referencedConfig instanceof InterceptorConfig) {
                InterceptorConfig interceptorConfig = (InterceptorConfig) referencedConfig;
                Map thisInterceptorClassParams = interceptorConfig.getParams();
                Map params = (thisInterceptorClassParams == null) ? new HashMap() : new HashMap(thisInterceptorClassParams);
                params.putAll(refParams);
                result.add(buildInterceptor(interceptorConfig.getClazz(), params));
            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    LOG.warn("Interceptor-ref params are being ignored because they are applied to an Interceptor-Stack reference. Ref name = " + refName + ", params = " + refParams);
                }

                result.addAll(stackConfig.getInterceptors());
            } else {
                LOG.error("Got unexpected type for interceptor " + refName);
            }
        }

        return result;
    }
}
