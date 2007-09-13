/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

//import org.easymock.MockControl;

import java.util.Properties;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.XWorkTestCase;


/**
 * ConfigurationManagerTest
 *
 * @author Jason Carreira
 *         Created May 6, 2003 10:59:59 PM
 */
public class ConfigurationManagerTest extends XWorkTestCase {

    Mock configProviderMock;


    public void testConfigurationReload() {
        FileManager.setReloadingConfigs(true);

        // now check that it reloads
        configProviderMock.expectAndReturn("needsReload", Boolean.TRUE);
        configProviderMock.expect("init", C.isA(Configuration.class));
        configProviderMock.expect("register", C.ANY_ARGS);
        configProviderMock.expect("loadPackages", C.ANY_ARGS);
        configProviderMock.expect("destroy", C.ANY_ARGS);
        configProviderMock.matchAndReturn("toString", "mock");
        configurationManager.getConfiguration();
        configProviderMock.verify();

        // this will be called in teardown
        configProviderMock.expect("destroy");
    }

    public void testNoConfigurationReload() {
        FileManager.setReloadingConfigs(false);

        // now check that it doesn't try to reload
        configurationManager.getConfiguration();
        configProviderMock.verify();

        // this will be called in teardown
        configProviderMock.expect("destroy");
    }

    public void testDestroyConfiguration() throws Exception {
    	class State {
    		public boolean isDestroyed1 =false;
    		public boolean isDestroyed2 =false;
    	};
    	
    	final State state = new State();
    	ConfigurationManager configurationManager = new ConfigurationManager();
    	configurationManager.addConfigurationProvider(new ConfigurationProvider() {
			public void destroy() { 
				throw new RuntimeException("testing testing 123");
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	configurationManager.addConfigurationProvider(new ConfigurationProvider() {
			public void destroy() { 
				state.isDestroyed1 = true;
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	configurationManager.addConfigurationProvider(new ConfigurationProvider() {
			public void destroy() { 
				throw new RuntimeException("testing testing 123");
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	configurationManager.addConfigurationProvider(new ConfigurationProvider() {
			public void destroy() { 
				state.isDestroyed2 = true;
			}
			public void init(Configuration configuration) throws ConfigurationException {
			}
			public void loadPackages() throws ConfigurationException {
			}
			public boolean needsReload() { return false;
			}
			public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
			}
			public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
			}
    	});
    	
    	assertFalse(state.isDestroyed1);
    	assertFalse(state.isDestroyed2);
    	
    	configurationManager.clearConfigurationProviders();
    	
    	assertTrue(state.isDestroyed1);
    	assertTrue(state.isDestroyed2);
    }

    public void testClearConfigurationProviders() throws Exception {
        configProviderMock.expect("destroy");
        configurationManager.clearConfigurationProviders();
        configProviderMock.verify();
    }

    protected void setUp() throws Exception {
        super.setUp();
        configurationManager.destroyConfiguration();

        configProviderMock = new Mock(ConfigurationProvider.class);
        configProviderMock.matchAndReturn("equals", C.ANY_ARGS, false);

        ConfigurationProvider mockProvider = (ConfigurationProvider) configProviderMock.proxy();
        configurationManager.addConfigurationProvider(new XWorkConfigurationProvider());
        configurationManager.addConfigurationProvider(mockProvider);
        
        //the first time it always inits
        configProviderMock.expect("init", C.isA(Configuration.class));
        configProviderMock.expect("register", C.ANY_ARGS);
        configProviderMock.expect("loadPackages", C.ANY_ARGS);
        configProviderMock.matchAndReturn("toString", "mock");
        
        configurationManager.getConfiguration();
    }

    protected void tearDown() throws Exception {
        configProviderMock.expect("destroy");
        super.tearDown();
    }
}
