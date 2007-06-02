/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.easymock.MockControl;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.util.FileManager;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * ConfigurationManagerTest
 *
 * @author Jason Carreira
 * @author tmjee
 * 
 * @version $Date$ $Id$
 */
public class ConfigurationManagerTest extends XWorkTestCase {

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
    	
    	control.expectAndDefaultReturn(
    			configuration.getRuntimeConfiguration(), 
    			new RuntimeConfiguration() {
					public ActionConfig getActionConfig(String namespace, String name) {
						return null;
					}
					public Map getActionConfigs() {
						return Collections.EMPTY_MAP;
					}
    				
    			});
    	configuration.destroy();		// EasyMock
    	configProviderMock.expect("destroy");  // MockObject
    	control.replay();
    	ConfigurationManager.destroyConfiguration();
    	configProviderMock.verify();
    	control.verify();
    }
    
    
    
    public void testDestroyInterceptors() throws Exception {
    	
    	final MockInterceptor interceptor1 = new MockInterceptor();
    	final MockInterceptor interceptor2 = new MockInterceptor();
    	final MockInterceptor interceptor3 = new MockInterceptor();
    	final MockInterceptor interceptor4 = new MockInterceptor();
    	final MockInterceptor interceptor5 = new MockInterceptor();
    	final MockInterceptor interceptor6 = new MockInterceptor();
    	final MockInterceptor interceptor7 = new MockInterceptor();
    	final MockInterceptor interceptor8 = new MockInterceptor();
    	final InterceptorMapping interceptorMapping1 = new InterceptorMapping("interceptor1", interceptor1);
    	final InterceptorMapping interceptorMapping2 = new InterceptorMapping("interceptor2", interceptor2);
    	final InterceptorMapping interceptorMapping3 = new InterceptorMapping("interceptor3", interceptor3);
    	final InterceptorMapping interceptorMapping4 = new InterceptorMapping("interceptor4", interceptor4);
    	final InterceptorMapping interceptorMapping5 = new InterceptorMapping("interceptor5", interceptor5);
    	final InterceptorMapping interceptorMapping6 = new InterceptorMapping("interceptor6", interceptor6);
    	final InterceptorMapping interceptorMapping7 = new InterceptorMapping("interceptor7", interceptor7);
    	final InterceptorMapping interceptorMapping8 = new InterceptorMapping("interceptor8", interceptor8);
    	
    	
    	MockControl control = MockControl.createControl(Configuration.class);
    	Configuration configuration = (Configuration) control.getMock();
    	ConfigurationManager.setConfiguration(configuration);
    	
    	control.expectAndDefaultReturn(
    			configuration.getRuntimeConfiguration(), 
    			new RuntimeConfiguration() {
					public ActionConfig getActionConfig(String namespace, String name) {
						return null;
					}
					public Map getActionConfigs() {
						return new LinkedHashMap() {
							private static final long serialVersionUID = 3898273938943025926L;
							{
								put("/namespace1",  
										new LinkedHashMap() {
											private static final long serialVersionUID = -501228436728130730L;
											{
												put("action1", new ActionConfig("execute", ActionSupport.class, Collections.EMPTY_MAP, Collections.EMPTY_MAP, 
														new LinkedList() {
															private static final long serialVersionUID = -469598749286764352L;
															{
																	add(interceptorMapping1);
																	add(interceptorMapping2);
																	add(interceptorMapping3);
															}
													}));
											}
										});
								put("/namespace2",  
										new LinkedHashMap() {
											private static final long serialVersionUID = -3776017770134006105L;
											{
												put("action2", new ActionConfig("execute", ActionSupport.class, Collections.EMPTY_MAP, Collections.EMPTY_MAP, 
														new LinkedList() {
															private static final long serialVersionUID = 3329054396195275042L;
															{
																	add(interceptorMapping2);
																	add(interceptorMapping3);
																	add(interceptorMapping4);
															}
													}));
											}
										});
								put("/namespace3",  
										new LinkedHashMap() {
											private static final long serialVersionUID = 8617571684293955862L;
											{
												put("action3", new ActionConfig("execute", ActionSupport.class, Collections.EMPTY_MAP, Collections.EMPTY_MAP, 
														new LinkedList() {
															private static final long serialVersionUID = -1373751922832875792L;
															{
																	add(interceptorMapping4);
																	add(interceptorMapping5);
																	add(interceptorMapping6);
															}
													}));
											}
										});
								put("/namespace4",  
										new LinkedHashMap() {
											private static final long serialVersionUID = -2184515238440865053L;
											{
												put("action4", new ActionConfig("execute", ActionSupport.class, Collections.EMPTY_MAP, Collections.EMPTY_MAP, 
														new LinkedList() {
															private static final long serialVersionUID = 5583322314404579945L;
															{
																	add(interceptorMapping6);
																	add(interceptorMapping7);
																	add(interceptorMapping8);
															}
													}));
											}
										});
							}
						};
					}
    			});
    	configuration.destroy();		// EasyMock
    	configProviderMock.expect("destroy");  // MockObject
    	control.replay();
    	ConfigurationManager.destroyConfiguration();
    	configProviderMock.verify();
    	control.verify();
    	
    	assertTrue(interceptor1.destroyed);
    	assertTrue(interceptor2.destroyed);
    	assertTrue(interceptor3.destroyed);
    	assertTrue(interceptor4.destroyed);
    	assertTrue(interceptor5.destroyed);
    	assertTrue(interceptor6.destroyed);
    	assertTrue(interceptor7.destroyed);
    	assertTrue(interceptor8.destroyed);
    	assertEquals(interceptor1.destroyCallCount, 1);
    	assertEquals(interceptor2.destroyCallCount, 1);
    	assertEquals(interceptor3.destroyCallCount, 1);
    	assertEquals(interceptor4.destroyCallCount, 1);
    	assertEquals(interceptor5.destroyCallCount, 1);
    	assertEquals(interceptor6.destroyCallCount, 1);
    	assertEquals(interceptor7.destroyCallCount, 1);
    	assertEquals(interceptor8.destroyCallCount, 1);
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
    
    
    protected static class MockInterceptor implements Interceptor {
		private static final long serialVersionUID = 7221646431244123611L;
		public boolean destroyed = false;
    	public int destroyCallCount = 0;
		public void destroy() {
			destroyed = true;
			destroyCallCount++;
		}
		public void init() {
		}
		public String intercept(ActionInvocation invocation) throws Exception {
			return invocation.invoke();
		}
    }
}
