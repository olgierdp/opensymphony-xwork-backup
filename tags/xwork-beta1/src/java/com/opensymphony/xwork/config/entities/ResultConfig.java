/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 1:21:06 PM
 * To change this template use Options | File Templates.
 */
public class ResultConfig implements Parameterizable {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Class clazz;
    private Map params;
    private String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ResultConfig() {
        params = new HashMap();
    }

    public ResultConfig(String name, Class clazz) {
        this(name, clazz, new HashMap());
    }

    public ResultConfig(String name, Class clazz, Map params) {
        this.name = name;
        this.clazz = clazz;
        this.params = params;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void addParam(String name, Object value) {
        getParams().put(name, value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ResultConfig)) {
            return false;
        }

        final ResultConfig resultConfig = (ResultConfig) o;

        if ((clazz != null) ? (!clazz.equals(resultConfig.clazz)) : (resultConfig.clazz != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(resultConfig.name)) : (resultConfig.name != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(resultConfig.params)) : (resultConfig.params != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((clazz != null) ? clazz.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);

        return result;
    }
}
