/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;

/**
 * Test for XW-465.
 * <p/>
 * Must extend XWorkTestCase to make sure the static ConfigurationManager and ActionProxyFactory doesn't cause sideeffects
 * in other tests.
 * <br/>
 * See XW-506 where we had such a corner case where a test in ParametersInterceptorTest was failing because this unit test
 * was extended TestCase directly.
 * 
 * @author tmjee
 */
public class ActionProxyTest extends XWorkTestCase {
	
	public static interface ProxyAction {
		String show() throws Exception;
	}

	public void testExecuteProxyAction() throws Exception {
		final ProxyAction action = new ProxyAction() {
			public String show() throws Exception {
				return "success";
			}
		};
		
		ObjectFactory.setObjectFactory(new ObjectFactory() {
			public Object buildAction(String actionName, String namespace, ActionConfig config, Map extraContext) throws Exception {
				return Proxy.newProxyInstance(
						Thread.currentThread().getContextClassLoader(), 
						new Class[] { ProxyAction.class }, 
						new InvocationHandler() {
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								return method.invoke(action, args);
							}
						});
			}
		});
		
		Map results = new LinkedHashMap();
		results.put("success", new ResultConfig("success", "com.opensymphony.xwork.mock.MockResult"));
		
		ActionConfig actionConfig = new ActionConfig("show", 
				"ProxyAction", 
				Collections.EMPTY_MAP, 
				results, 
				Collections.EMPTY_LIST);
		
		final PackageConfig packageConfig = new PackageConfig("myPackage", "/namespace", false, null);
		packageConfig.addActionConfig("show", actionConfig);
		
		
		ConfigurationManager.addConfigurationProvider(new ConfigurationProvider() {
			public void destroy() {}
			public void init(Configuration configuration) throws ConfigurationException {
				configuration.addPackageConfig("myPackage", packageConfig);
			};
			public boolean needsReload() { return false; }
		});
	
		ActionProxy actionProxy = ActionProxyFactory.getFactory().createActionProxy("/namespace", "show", new LinkedHashMap());
		String result = actionProxy.execute();
		
		assertEquals(result, "success");
	}
}
