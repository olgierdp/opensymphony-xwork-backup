/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import com.opensymphony.xwork.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 2:41:35 PM
 * To change this template use Options | File Templates.
 */
public class InterceptorStackConfig implements InterceptorListHolder {
    //~ Instance fields ////////////////////////////////////////////////////////

    private List interceptors;
    private String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public InterceptorStackConfig() {
        this.interceptors = new ArrayList();
    }

    public InterceptorStackConfig(String name) {
        this();
        this.name = name;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Collection getInterceptors() {
        return interceptors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public void addInterceptors(List interceptors) {
        this.interceptors.addAll(interceptors);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }

        final InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig) o;

        if ((interceptors != null) ? (!interceptors.equals(interceptorStackConfig.interceptors)) : (interceptorStackConfig.interceptors != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorStackConfig.name)) : (interceptorStackConfig.name != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((interceptors != null) ? interceptors.hashCode() : 0);

        return result;
    }
}
