/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import com.opensymphony.xwork.interceptor.Interceptor;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Contains everything needed to configure and execute an action:
 * <ul>
 *  <li>methodName - the method name to execute on the action. If this is null, the Action will be cast to the Action
 * Interface and the execute() method called</li>
 *  <li>clazz - the class name for the action</li>
 *  <li>params - the params to be set for this action just before execution</li>
 *  <li>results - the result map {String -> View class}</li>
 *  <li>resultParameters - params for results {String -> Map}</li>
 *  <li>typeConverter - the Ognl TypeConverter to use when getting/setting properties</li>
 * </ul>
 *
 * @author $Author$
 * @version $Revision$
 */
public class ActionConfig implements InterceptorListHolder, Parameterizable, Serializable {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected List interceptors;
    protected Map params;
    protected Map results;
    protected Method method;
    protected String methodName;
    private Class clazz;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ActionConfig() {
        params = new HashMap();
        results = new HashMap();
        interceptors = new ArrayList();
    }

    public ActionConfig(String methodName, Class clazz, Map parameters, Map results, List interceptors) {
        this.methodName = methodName;
        this.interceptors = interceptors;
        this.params = parameters;
        this.results = results;
        this.clazz = clazz;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public List getInterceptors() {
        if (interceptors == null) {
            interceptors = new ArrayList();
        }

        return interceptors;
    }

    /**
     * Returns cached instance of the action method or null if method name was not specified
     * @return cached instance of the action method or null if method name was not specified
     */
    public Method getMethod() throws NoSuchMethodException {
        if (method != null) {
            return method;
        }

        if (methodName != null) {
            method = clazz.getMethod(methodName, new Class[0]);
        } else // return default execute() method if method name is not specified
         {
            method = clazz.getMethod("execute", new Class[0]);
        }

        return method;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns name of the action method
     * @return name of the method to execute
     */
    public String getMethodName() {
        return methodName;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getParams() {
        if (params == null) {
            params = new HashMap();
        }

        return params;
    }

    public void setResults(Map results) {
        this.results = results;
    }

    public Map getResults() {
        if (results == null) {
            results = new HashMap();
        }

        return results;
    }

    public void addInterceptor(Interceptor interceptor) {
        getInterceptors().add(interceptor);
    }

    public void addInterceptors(List interceptors) {
        getInterceptors().addAll(interceptors);
    }

    public void addParam(String name, Object value) {
        getParams().put(name, value);
    }

    public void addResultConfig(ResultConfig resultConfig) {
        getResults().put(resultConfig.getName(), resultConfig);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ActionConfig)) {
            return false;
        }

        final ActionConfig actionConfig = (ActionConfig) o;

        if ((clazz != null) ? (!clazz.equals(actionConfig.clazz)) : (actionConfig.clazz != null)) {
            return false;
        }

        if ((interceptors != null) ? (!interceptors.equals(actionConfig.interceptors)) : (actionConfig.interceptors != null)) {
            return false;
        }

        if ((method != null) ? (!method.equals(actionConfig.method)) : (actionConfig.method != null)) {
            return false;
        }

        if ((methodName != null) ? (!methodName.equals(actionConfig.methodName)) : (actionConfig.methodName != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(actionConfig.params)) : (actionConfig.params != null)) {
            return false;
        }

        if ((results != null) ? (!results.equals(actionConfig.results)) : (actionConfig.results != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((interceptors != null) ? interceptors.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);
        result = (29 * result) + ((results != null) ? results.hashCode() : 0);
        result = (29 * result) + ((method != null) ? method.hashCode() : 0);
        result = (29 * result) + ((methodName != null) ? methodName.hashCode() : 0);
        result = (29 * result) + ((clazz != null) ? clazz.hashCode() : 0);

        return result;
    }
}
