/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import java.util.List;
import java.util.Properties;

import com.opensymphony.xwork2.inject.ContainerBuilder;


/**
 * Interface to be implemented by all forms of XWork configuration classes.
 *
 * @author $Author$
 * @version $Revision$
 */
public interface ConfigurationProvider {

    public void destroy();
    
    public void init(Configuration configuration) throws ConfigurationException;
    
    public void register(ContainerBuilder builder, Properties props, List<Class<?>> ignoreFailureStaticInjection) throws ConfigurationException;
    
    public void loadPackages() throws ConfigurationException;
    
    /**
     * Tells whether the ConfigurationProvider should reload its configuration
     *
     * @return <tt>true</tt>, whether the ConfigurationProvider should reload its configuration, <tt>false</tt>otherwise.
     */
    public boolean needsReload();
    
}
