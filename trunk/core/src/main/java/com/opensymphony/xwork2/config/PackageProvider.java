/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

/**
 * Provides configuration packages.  The separate init and loadPackages calls are due to the need to 
 * preserve backwards compatibility with the 2.0 {@link ConfigurationProvider} interface
 * 
 * @since 2.1
 */
public interface PackageProvider {
    
    /**
     * Initializes with the configuration
     * @param configuration The configuration
     * @throws ConfigurationException If anything goes wrong
     */
    public void init(Configuration configuration) throws ConfigurationException;
    
    /**
     * Tells whether the PackageProvider should reload its configuration
     *
     * @return <tt>true</tt>, whether the PackageProvider should reload its configuration, <tt>false</tt>otherwise.
     */
    public boolean needsReload();

    /**
     * Loads the packages for the configuration.
     * @throws ConfigurationException
     */
    public void loadPackages() throws ConfigurationException;
    
}
