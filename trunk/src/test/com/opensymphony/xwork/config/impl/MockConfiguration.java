/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.impl;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.RuntimeConfiguration;
import com.opensymphony.xwork.config.entities.PackageConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:27:37 PM
 * To change this template use Options | File Templates.
 */
public class MockConfiguration implements Configuration {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Map packages = new HashMap();

    //~ Methods ////////////////////////////////////////////////////////////////

    public PackageConfig getPackageConfig(String name) {
        return (PackageConfig) packages.get(name);
    }

    public Set getPackageConfigNames() {
        return packages.keySet();
    }

    public Map getPackageConfigs() {
        return packages;
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void addPackageConfig(String name, PackageConfig packageContext) {
        packages.put(name, packageContext);
    }

    public void buildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public void rebuildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void reload() throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    public void removePackageConfig(String name) {
    }
}
