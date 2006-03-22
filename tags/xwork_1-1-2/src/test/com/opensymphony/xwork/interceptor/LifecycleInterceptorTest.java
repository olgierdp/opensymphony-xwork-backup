/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import com.opensymphony.xwork.config.entities.PackageConfig;

import junit.framework.TestCase;


/**
 * Test AbstractLifecycleLister.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class LifecycleInterceptorTest extends TestCase {

	List eventSequence = new ArrayList();
	
	public void testLifecycle() throws Exception {
		ConfigurationProvider cp = new ConfigurationProvider() {

			public void destroy() {
			}

			public void init(Configuration configuration) throws ConfigurationException {
				ActionConfig actionConfig = new ActionConfig();
				actionConfig.setClassName("com.opensymphony.xwork.interceptor.LifecycleInterceptorTest$MyAction");
				actionConfig.setMethodName("execute");
				actionConfig.setPackageName("myPackageName");
				actionConfig.addInterceptor(new InterceptorMapping("myInterceptor", new InternalLifecycleInterceptor()));
				
				PackageConfig packageConfig = new PackageConfig();
				packageConfig.setName("myPackageName");
				packageConfig.setNamespace("/myNamespace");
				packageConfig.addActionConfig("myActionName", actionConfig);
				
				configuration.addPackageConfig(packageConfig.getName(), packageConfig);
			}

			public boolean needsReload() {
				return false;
			}
		};
		
		ConfigurationManager.addConfigurationProvider(cp);
		
		ActionProxy actionProxy = ActionProxyFactory.getFactory().createActionProxy("/myNamespace", "myActionName", new HashMap());
		actionProxy.execute();
		
		assertEquals(eventSequence.size(), 3);
		assertEquals(((TestResultEncapsulation)eventSequence.get(0)).event, "before");
		assertEquals(((TestResultEncapsulation)eventSequence.get(0)).result, null);
		assertEquals(((TestResultEncapsulation)eventSequence.get(1)).event, "beforeResult");
		assertEquals(((TestResultEncapsulation)eventSequence.get(1)).result, MyAction.SUCCESS);
		assertEquals(((TestResultEncapsulation)eventSequence.get(2)).event, "after");
		assertEquals(((TestResultEncapsulation)eventSequence.get(2)).result, MyAction.SUCCESS);
		
	}
	
	public static class MyAction extends ActionSupport {

		private static final long serialVersionUID = -5014600447925835654L;
	}
	
	
	public class InternalLifecycleInterceptor extends AbstractLifecycleInterceptor {
		
		private static final long serialVersionUID = -6987132432154659517L;

		public void beforeResult(ActionInvocation invocation, String resultCode) {
			eventSequence.add(new TestResultEncapsulation("beforeResult", null, resultCode));
		}
		
		protected void before(ActionInvocation invocation) throws Exception {
			eventSequence.add(new TestResultEncapsulation("before", invocation, null));
		}
		
		protected void after(ActionInvocation invocation, String result) throws Exception {
			eventSequence.add(new TestResultEncapsulation("after", invocation, result));
		}
	}
	
	class TestResultEncapsulation {
		
		String event;
		ActionInvocation invocation;
		String result;
		
		public TestResultEncapsulation(String event, ActionInvocation invocation, String result) {
			this.event = event;
			this.invocation = invocation;
			this.result = result;
		}
	}
}
