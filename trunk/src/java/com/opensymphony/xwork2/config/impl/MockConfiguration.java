/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.*;


/**
 * Simple configuration used for unit testing
 */
public class MockConfiguration implements Configuration {

    private Map<String, PackageConfig> packages = new HashMap<String, PackageConfig>();
    private Set<String> loadedFiles = new HashSet<String>();
    private Container container;
    
    public MockConfiguration() {
        ContainerBuilder builder = new ContainerBuilder();
        LocatableProperties props = new LocatableProperties();
        new XWorkConfigurationProvider().register(builder, props);
        builder.constant("devMode", "false");
        container = builder.create(true);
    }

    public PackageConfig getPackageConfig(String name) {
        return packages.get(name);
    }

    public Set<String> getPackageConfigNames() {
        return packages.keySet();
    }

    public Map<String, PackageConfig> getPackageConfigs() {
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

    public Container getContainer() {
        return container;
    }

    public Set<String> getLoadedFileNames() {
        return loadedFiles;
    }

    public List<PackageProvider> reloadContainer(
            List<ContainerProvider> containerProviders)
            throws ConfigurationException {
        throw new UnsupportedOperationException();
    }
}
