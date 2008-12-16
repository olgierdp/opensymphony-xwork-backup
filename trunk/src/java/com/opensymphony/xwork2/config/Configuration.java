/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * XWork configuration.
 *
 * @author Mike
 */
public interface Configuration extends Serializable {

    void rebuildRuntimeConfiguration();

    PackageConfig getPackageConfig(String name);

    Set<String> getPackageConfigNames();

    Map<String, PackageConfig> getPackageConfigs();

    /**
     * The current runtime configuration. Currently, if changes have been made to the Configuration since the last
     * time buildRuntimeConfiguration() was called, you'll need to make sure to.
     *
     * @return the current runtime configuration
     */
    RuntimeConfiguration getRuntimeConfiguration();

    void addPackageConfig(String name, PackageConfig packageConfig);

    /**
     * Allow the Configuration to clean up any resources that have been used.
     */
    void destroy();

    /**
     * @deprecated Since 2.1
     * @param providers
     * @throws ConfigurationException
     */
    @Deprecated void reload(List<ConfigurationProvider> providers) throws ConfigurationException;
    
    /**
     * @since 2.1
     * @param containerProviders
     * @throws ConfigurationException
     */
    List<PackageProvider> reloadContainer(List<ContainerProvider> containerProviders) throws ConfigurationException;

    /**
     * @return the container
     */
    Container getContainer();

    Set<String> getLoadedFileNames();

    /**
     * @since 2.1
     * @return list of unknown handlers
     */
    List<UnknownHandlerConfig> getUnknownHandlerStack();

    /**
     * @since 2.1
     * @param unknownHandlerStack
     */
    void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack);
}
