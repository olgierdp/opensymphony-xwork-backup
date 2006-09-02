/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import org.easymock.MockControl;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.util.FileManager;
import junit.framework.TestCase;


/**
 * ConfigurationManagerTest
 *
 * @author Jason Carreira
 *         Created May 6, 2003 10:59:59 PM
 */
public class ConfigurationManagerTest extends TestCase {

    Mock configProviderMock;


    public void testConfigurationReload() {
        FileManager.setReloadingConfigs(true);

        // now check that it reloads
        configProviderMock.expectAndReturn("needsReload", Boolean.TRUE);
        configProviderMock.expect("init", C.isA(Configuration.class));
        ConfigurationManager.getConfiguration();
        configProviderMock.verify();

        // this will be called in teardown
        configProviderMock.expect("destroy");
    }

    public void testNoConfigurationReload() {
        FileManager.setReloadingConfigs(false);

        // now check that it doesn't try to reload
        ConfigurationManager.getConfiguration();
        configProviderMock.verify();

        // this will be called in teardown
        configProviderMock.expect("destroy");
    }

    public void testDestroyConfiguration() throws Exception {
    	MockControl control = MockControl.createControl(Configuration.class);
    	Configuration configuration = (Configuration) control.getMock();
    	ConfigurationManager.setConfiguration(configuration);
    	
    	configuration.destroy();		// EasyMock
    	configProviderMock.expect("destroy");  // MockObject
    	control.replay();
    	ConfigurationManager.destroyConfiguration();
    	configProviderMock.verify();
    	control.verify();
    }
    
    public void testClearConfigurationProviders() throws Exception {
    	configProviderMock.expect("destroy");  
    	ConfigurationManager.clearConfigurationProviders();
    	configProviderMock.verify();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        ConfigurationManager.destroyConfiguration();

        configProviderMock = new Mock(ConfigurationProvider.class);

        ConfigurationProvider mockProvider = (ConfigurationProvider) configProviderMock.proxy();
        ConfigurationManager.addConfigurationProvider(mockProvider);

        //the first time it always inits
        configProviderMock.expect("init", C.isA(Configuration.class));
        ConfigurationManager.getConfiguration();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ConfigurationManager.destroyConfiguration();
    }
}
