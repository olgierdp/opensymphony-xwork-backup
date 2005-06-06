/*
 * Created on Aug 13, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.entities;

import java.util.List;
import java.util.Map;

/**
 * A generic MVC command object
 */
public interface Command {

    public String getClassName();

    public List getInterceptors();

    //public List getBeans();

    /**
     * Returns a Map of resultName/View
     */
    public Map getViews();

    public String getName();

    public String getNamespace();

    public String toString();
}