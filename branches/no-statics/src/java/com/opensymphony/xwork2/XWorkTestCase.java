/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2;

import java.util.Arrays;

import junit.framework.TestCase;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.impl.MockConfiguration;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;


/**
 * Base JUnit TestCase to extend for XWork specific JUnit tests. Uses 
 * the generic test setup for logic.
 *
 * @author plightbo
 */
public abstract class XWorkTestCase extends TestCase {
    
    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;
    
    public XWorkTestCase() {
        super();
    }
    
    protected void setUp() throws Exception {
        configurationManager = XWorkTestCaseHelper.setUp();
        configuration = new MockConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }
    
    protected void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(configurationManager);
        configurationManager = null;
        configuration = null;
        container = null;
        actionProxyFactory = null;
    }
    
    protected void loadConfigurationProviders(ConfigurationProvider... providers) {
        configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(configurationManager, providers);
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        actionProxyFactory = container.getInstance(ActionProxyFactory.class);
    }
    
    protected void loadWithOverriding(ConfigurationProvider... providers) {
        XmlConfigurationProvider def = new XmlConfigurationProvider("xwork-default.xml");
        def.setThrowExceptionOnDuplicateBeans(false);
        ConfigurationProvider[] sum = new ConfigurationProvider[providers.length + 1];
        System.arraycopy(providers, 0, sum, 0, providers.length);
        sum[providers.length] = def;
        
        loadConfigurationProviders(sum);
    }
    
    
    
    
}
