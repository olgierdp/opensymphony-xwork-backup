/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;

import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 1:21:06 PM
 * To change this template use Options | File Templates.
 */
public class ResultConfig implements Parameterizable, Serializable {

    private Map params;
    private String className;
    private String name;


    public ResultConfig() {
        params = new TreeMap();
    }

    public ResultConfig(String name, Class clazz) {
        this(name, clazz, new TreeMap());
    }

    public ResultConfig(String name, Class clazz, Map params) {
        this.name = name;
        this.className = clazz.getName();
        this.params = params;
    }

    public ResultConfig(String name, String className, Map params) {
        this.name = name;
        this.className = className;
        this.params = params;
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
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
            params = new TreeMap();
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

        if ((className != null) ? (!className.equals(resultConfig.className)) : (resultConfig.className != null)) {
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
        result = (29 * result) + ((className != null) ? className.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);

        return result;
    }
}
