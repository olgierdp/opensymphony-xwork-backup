/*
 * Created on Aug 12, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.collectors;

import java.io.IOException;
import java.util.Map;

/**
 * Collects Flows from an MVC configuration
 */
public interface Collector {
    public final static int TYPE_WEBWORK2 = 1;

    public Map getPackages();

    public void setBasePath(String basePath) throws IOException;
}