/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.entities;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 1:24:41 PM
 * To change this template use Options | File Templates.
 */
public class ResultTypeConfig {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Class clazz;
    private String name;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ResultTypeConfig() {
    }

    public ResultTypeConfig(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ResultTypeConfig)) {
            return false;
        }

        final ResultTypeConfig resultTypeConfig = (ResultTypeConfig) o;

        if ((clazz != null) ? (!clazz.equals(resultTypeConfig.clazz)) : (resultTypeConfig.clazz != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(resultTypeConfig.name)) : (resultTypeConfig.name != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((clazz != null) ? clazz.hashCode() : 0);

        return result;
    }
}
