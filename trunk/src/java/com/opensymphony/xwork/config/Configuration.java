/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.config.entities.PackageConfig;

import java.util.Map;
import java.util.Set;


/**
 * User: Mike
 * Date: May 6, 2003
 * Time: 3:13:55 PM
 */
public interface Configuration {

    public void rebuildRuntimeConfiguration();

    PackageConfig getPackageConfig(String name);

    Set getPackageConfigNames();

    Map getPackageConfigs();

    /**
     * the current runtime configuration.  currently, if changes have been made to the Configuration since the last
     * time buildRuntimeConfiguration() was called, you'll need to make sure to
     *
     * @return the current runtime configuration
     */
    RuntimeConfiguration getRuntimeConfiguration();

    void addPackageConfig(String name, PackageConfig packageConfig);

    /**
     * allow the Configuration to clean up any resources that have been used
     */
    void destroy();

    /**
     * @throws ConfigurationException
     */
    void reload() throws ConfigurationException;

    void removePackageConfig(String name);
}
