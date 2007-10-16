/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Simple configuration used for unit testing
 */
public class MockConfiguration implements Configuration {

    private Map packages = new HashMap();
    private Set loadedFiles = new HashSet();
    private Container container;
    
    public MockConfiguration() {
        ContainerBuilder builder = new ContainerBuilder();
        LocatableProperties props = new LocatableProperties();
        new XWorkConfigurationProvider().register(builder, props);
        builder.constant("devMode", "false");
        container = builder.create(true);
    }

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
