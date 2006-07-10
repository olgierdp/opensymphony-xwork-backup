/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;

import java.util.List;
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

    private Map packages = new HashMap();


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

    public void reload(List<ConfigurationProvider> providers) throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    public void removePackageConfig(String name) {
    }
}
