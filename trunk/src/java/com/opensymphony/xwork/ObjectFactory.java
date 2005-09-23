/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.XWorkContinuationConfig;
import com.opensymphony.xwork.validator.Validator;
import com.uwyn.rife.continuations.ContinuationConfig;
import com.uwyn.rife.continuations.ContinuationInstrumentor;
import com.uwyn.rife.continuations.util.ClassByteUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * ObjectFactory is responsible for building the core framework objects. Users may register their own implementation of
 * the ObjectFactory to control instantiation of these Objects.
 * <p/>
 * This default implementation uses the {@link #buildBean(Class) buildBean} method to create all classes
 * (interceptors, actions, results, etc).
 *
 * @author Jason Carreira
 */
public class ObjectFactory {
    private static ContinuationsClassLoader ccl;
    private static ObjectFactory FACTORY = new ObjectFactory();
    private static String continuationPackage;

    public static void setContinuationPackage(String continuationPackage) {
        ContinuationConfig.setInstance(new XWorkContinuationConfig());
        ObjectFactory.continuationPackage = continuationPackage;
        ObjectFactory.ccl = new ContinuationsClassLoader(continuationPackage, Thread.currentThread().getContextClassLoader());
    }

    public static String getContinuationPackage() {
        return continuationPackage;
    }

    protected ObjectFactory() {
    }

    public static void setObjectFactory(ObjectFactory factory) {
        FACTORY = factory;
    }

    public static ObjectFactory getObjectFactory() {
        return FACTORY;
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

    public Object buildAction(String actionName, String namespace, ActionConfig config) throws Exception {
        return buildBean(config.getClassName());
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param clazz the type of Object to build
     */
    public Object buildBean(Class clazz) throws Exception {
        return clazz.newInstance();
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     */
    public Object buildBean(String className) throws Exception {
        Class clazz = getClassInstance(className);

        return clazz.newInstance();
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
            Interceptor interceptor = (Interceptor) buildBean(interceptorClassName);
            OgnlUtil.setProperties(params, interceptor);
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
            message = "Class [" + interceptorClassName + "] does not implement com.opensymphony.xwork.interceptor.Interceptor";
        } catch (Exception e) {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        } catch (NoClassDefFoundError e) {
            cause = e;
            message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
        }

        throw new ConfigurationException(message, cause);
    }

    /**
     * Build a Result using the type in the ResultConfig and set the parameters in the ResultConfig.
     */
    public Result buildResult(ResultConfig resultConfig) throws Exception {
        String resultClassName = resultConfig.getClassName();
        Result result = null;

        if (resultClassName != null) {
            result = (Result) buildBean(resultClassName);
            OgnlUtil.setProperties(resultConfig.getParams(), result, ActionContext.getContext().getContextMap());
        }

        return result;
    }

    /**
     * Build a Validator of the given type and set the parameters on it
     *
     * @param className the type of Validator to build
     * @param params    property name -> value Map to set onto the Validator instance
     */
    public Validator buildValidator(String className, Map params) throws Exception {
        Validator validator = (Validator) buildBean(className);
        OgnlUtil.setProperties(params, validator);

        return validator;
    }

    static class ContinuationsClassLoader extends ClassLoader {
        private String base;
        private ClassLoader parent;

        public ContinuationsClassLoader(String base, ClassLoader parent) {
            super(parent);
            this.base = base;
            this.parent = parent;
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            if (validName(name)) {
                if (findLoadedClass(name) == null) {
                    try {
                        byte[] bytes = ClassByteUtil.getBytes(name, parent);
                        if (bytes == null) {
                            return super.loadClass(name);
                        }

                        byte[] resume_bytes = ContinuationInstrumentor.instrument(bytes, name, false);

                        if (resume_bytes != null) {
                            bytes = resume_bytes;
                        }

                        return defineClass(name, bytes, 0, bytes.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return super.loadClass(name);
        }

        private boolean validName(String name) {
            if (name.startsWith(base + ".")) {
                return true;
            } else {
                return false;
            }
        }
    }
}
