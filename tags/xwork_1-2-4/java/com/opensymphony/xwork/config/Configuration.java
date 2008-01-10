/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.config.entities.PackageConfig;

import java.util.Map;
import java.util.Set;


/**
 * XWork configuration.
 *
 * @author Mike
 * @author tmjee
 * @version $Date$ $Id$
 */
public interface Configuration {

    /**
     * Rebuild the {@link com.opensymphony.xwork.config.RuntimeConfiguration} of XWork's configuration.
     */
    public void rebuildRuntimeConfiguration();

    /**
     * Return the configuration parameters in xwork.xml
     * <pre>
     *    <xwork>
     *      <parameters>
     *         <parameter name="..." value="..." />
     *         ....
     *      </parameters>
     *      ....
     *    </xwork>
     * </pre>
     * @return Map
     */
    Map getParameters();

    /**
     * Return the configuration parameter value for with parameter name as <code>name</code>.
     * @param name parameter name
     * @return String
     */
    String getParameter(String name);

    /**
     * Set the configuration parameter.
     * @param name parameter name
     * @param value parameter value
     */
    void setParameter(String name, String value);

    /**
     * Return the {@link com.opensymphony.xwork.config.entities.PackageConfig} (package configuration) for
     * package with named as <code>name</code>
     * @param name package name
     * @return {@link com.opensymphony.xwork.config.entities.PackageConfig}
     */
    PackageConfig getPackageConfig(String name);

    /**
     * Return the {@link com.opensymphony.xwork.config.entities.PackageConfig}s name as a {@link java.util.Set}.
     * @return Set
     */
    Set getPackageConfigNames();

    /**
     * Return the {@link com.opensymphony.xwork.config.entities.PackageConfig} as a {@link java.util.Map} with
     * the key as the package name and its value as the corresponding
     * {@link com.opensymphony.xwork.config.entities.PackageConfig}
     * @return Map
     */
    Map getPackageConfigs();

    /**
     * The current runtime configuration. Currently, if changes have been made to the Configuration since the last
     * time buildRuntimeConfiguration() was called, you'll need to make sure to get it using this method.
     *
     * @return the current runtime configuration
     */
    RuntimeConfiguration getRuntimeConfiguration();

    /**
     * Add a {@link com.opensymphony.xwork.config.entities.PackageConfig} with package name specified as
     * <code>name</code>
     * 
     * @param name
     * @param packageConfig
     */
    void addPackageConfig(String name, PackageConfig packageConfig);

    /**
     * Allow the Configuration to clean up any resources that have been used.
     */
    void destroy();

    /**
     * Reload xwork configuration, once this is done, we need to grab the
     * {@link com.opensymphony.xwork.config.RuntimeConfiguration} using {@link #getRuntimeConfiguration()} 
     * @throws ConfigurationException
     */
    void reload() throws ConfigurationException;

    /**
     * Remove the {@link com.opensymphony.xwork.config.entities.PackageConfig} for package with name as
     * <code>name</code>.
     * @param name package name to be removed
     */
    void removePackageConfig(String name);
}
