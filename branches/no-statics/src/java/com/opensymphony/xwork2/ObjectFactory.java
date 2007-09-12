/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.validator.Validator;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ObjectFactory is responsible for building the core framework objects. Users may register their 
 * own implementation of the ObjectFactory to control instantiation of these Objects.
 * <p/>
 * This default implementation uses the {@link #buildBean(Class,java.util.Map) buildBean} 
 * method to create all classes (interceptors, actions, results, etc).
 * <p/>
 * To add lifecycle hook into an <code>ObjectFactory</code>, either or both of the following interfaces
 * could be implemented by the <code>ObjectFactory</code> itself.
 * <ul>
 * 		<li>{@link org.apache.struts2.util.ObjectFactoryInitializable}</li>
 *      <li>{@link org.apache.struts2.util.ObjectFactoryDestroyable}</li>
 * </ul>
 * Both will be invoked during the startup and showdown of {@link org.apache.struts2.dispatcher.FilterDispatcher} through {@link org.apache.struts2.dispatcher.Dispatcher}.
 *
 * @author Jason Carreira
 */
public class ObjectFactory implements Serializable {
    private static final Log LOG = LogFactory.getLog(ObjectFactory.class);

    private transient ClassLoader ccl;
    private Container container;
    protected ReflectionProvider reflectionProvider;

    @Inject(value="objectFactory.classloader", required=false)
    public void setClassLoader(ClassLoader cl) {
        this.ccl = cl;
    }
    
    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    public ObjectFactory() {
    }
    
    public ObjectFactory(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public static ObjectFactory getObjectFactory() {
        return ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
    }

    /**
     * Allows for ObjectFactory implementations that support
     * Actions without no-arg constructors.
     *
     * @return true if no-arg constructor is required, false otherwise
     */
    public boolean isNoArgConstructorRequired() {
        return true;
    }

    /**
     * Utility method to obtain the class matched to className. Caches look ups so that subsequent
     * lookups will be faster.
     *
     * @param className The fully qualified name of the class to return
     * @return The class itself
     * @throws ClassNotFoundException
     */
    public Class getClassInstance(String className) throws ClassNotFoundException {
        if (ccl != null) {
            return ccl.loadClass(className);
        }

        return ClassLoaderUtil.loadClass(className, this.getClass());
    }

    /**
     * Build an instance of the action class to handle a particular request (eg. web request)
     * @param actionName the name the action configuration is set up with in the configuration
     * @param namespace the namespace the action is configured in
     * @param config the action configuration found in the config for the actionName / namespace
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     * @return instance of the action class to handle a web request
     * @throws Exception
     */
    public Object buildAction(String actionName, String namespace, ActionConfig config, Map extraContext) throws Exception {
        return buildBean(config.getClassName(), extraContext);
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param clazz the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        return clazz.newInstance();
    }

    /**
     * @param obj
     */
    protected Object injectInternalBeans(Object obj) {
        if (obj != null && container != null) {
            container.inject(obj);
        }
        return obj;
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Object buildBean(String className, Map extraContext) throws Exception {
        return buildBean(className, extraContext, true);
    }
    
    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Object buildBean(String className, Map extraContext, boolean injectInternal) throws Exception {
        Class clazz = getClassInstance(className);
        Object obj = buildBean(clazz, extraContext);
        if (injectInternal) {
            injectInternalBeans(obj);
        }
        return obj;
    }

    /**
     * Builds an Interceptor from the InterceptorConfig and the Map of
     * parameters from the interceptor reference. Implementations of this method
     * should ensure that the Interceptor is parameterized with both the
     * parameters from the Interceptor config and the interceptor ref Map (the
     * interceptor ref params take precedence), and that the Interceptor.init()
     * method is called on the Interceptor instance before it is returned.
     *
     * @param interceptorConfig    the InterceptorConfig from the configuration
     * @param interceptorRefParams a Map of params provided in the Interceptor reference in the
     *                             Action mapping or InterceptorStack definition
     */
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams) throws ConfigurationException {
        String interceptorClassName = interceptorConfig.getClassName();
        Map thisInterceptorClassParams = interceptorConfig.getParams();
        Map params = (thisInterceptorClassParams == null) ? new HashMap() : new HashMap(thisInterceptorClassParams);
        params.putAll(interceptorRefParams);

        String message;
        Throwable cause;

        try {
            // interceptor instances are long-lived and used across user sessions, so don't try to pass in any extra context
            Interceptor interceptor = (Interceptor) buildBean(interceptorClassName, null);
            reflectionProvider.setProperties(params, interceptor);
            interceptor.init();

            return interceptor;
        } catch (InstantiationException e) {
            cause = e;
            message = "Unable to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        } catch (IllegalAccessException e) {
            cause = e;
            message = "IllegalAccessException while attempting to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        } catch (ClassCastException e) {
            cause = e;
            message = "Class [" + interceptorClassName + "] does not implement com.opensymphony.xwork2.interceptor.Interceptor";
        } catch (Exception e) {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        } catch (NoClassDefFoundError e) {
            cause = e;
            message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
        }

        throw new ConfigurationException(message, cause, interceptorConfig);
    }

    /**
     * Build a Result using the type in the ResultConfig and set the parameters in the ResultConfig.
     *
     * @param resultConfig the ResultConfig found for the action with the result code returned
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Result buildResult(ResultConfig resultConfig, Map extraContext) throws Exception {
        String resultClassName = resultConfig.getClassName();
        Result result = null;

        if (resultClassName != null) {
            result = (Result) buildBean(resultClassName, extraContext);
        	reflectionProvider.setProperties(resultConfig.getParams(), result, extraContext, true);
        }

        return result;
    }

    /**
     * Build a Validator of the given type and set the parameters on it
     *
     * @param className the type of Validator to build
     * @param params    property name -> value Map to set onto the Validator instance
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Validator buildValidator(String className, Map params, Map extraContext) throws Exception {
        Validator validator = (Validator) buildBean(className, null);
        reflectionProvider.setProperties(params, validator);

        return validator;
    }

    static class ContinuationsClassLoader extends ClassLoader {
        
    }
}
