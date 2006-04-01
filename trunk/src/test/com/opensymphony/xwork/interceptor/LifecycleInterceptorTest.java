/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork.ActionContext;
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

			private InternalLifecycleInterceptor ili = new InternalLifecycleInterceptor();

			public void destroy() {
				ili.destroy();
			}

			public void init(Configuration configuration) throws ConfigurationException {
				ActionConfig actionConfig = new ActionConfig();
				actionConfig.setClassName("com.opensymphony.xwork.interceptor.LifecycleInterceptorTest$MyAction");
				actionConfig.setMethodName("execute");
				actionConfig.setPackageName("myPackageName");
				actionConfig.addInterceptor(new InterceptorMapping("myInterceptor", ili));
				
				PackageConfig packageConfig = new PackageConfig();
				packageConfig.setName("myPackageName");
				packageConfig.setNamespace("/myNamespace");
				packageConfig.addActionConfig("myActionName", actionConfig);
				
				configuration.addPackageConfig(packageConfig.getName(), packageConfig);

				ili.init();
			}

			public boolean needsReload() {
				return false;
			}
		};
		
		ConfigurationManager.addConfigurationProvider(cp);

		Map extraContext = new HashMap();
		extraContext.put("forceException", Boolean.FALSE);
		
		ActionProxy actionProxy = ActionProxyFactory.getFactory().createActionProxy("/myNamespace", "myActionName", extraContext);
		actionProxy.execute();
		
		assertEquals(eventSequence.size(), 3);
		assertEquals(((TestResultEncapsulation)eventSequence.get(0)).event, "before");
		assertEquals(((TestResultEncapsulation)eventSequence.get(0)).result, null);
		assertEquals(((TestResultEncapsulation)eventSequence.get(1)).event, "beforeResult");
		assertEquals(((TestResultEncapsulation)eventSequence.get(1)).result, MyAction.SUCCESS);
		assertEquals(((TestResultEncapsulation)eventSequence.get(2)).event, "after");
		assertEquals(((TestResultEncapsulation)eventSequence.get(2)).result, MyAction.SUCCESS);
		
		eventSequence.clear();
		extraContext.clear();
		actionProxy = ActionProxyFactory.getFactory().createActionProxy("/myNamespace", "myActionName", extraContext);
		extraContext.put("forceException", Boolean.TRUE);
		actionProxy.execute();
		assertEquals(((TestResultEncapsulation)eventSequence.get(0)).event, "before");
		assertEquals(((TestResultEncapsulation)eventSequence.get(0)).result, null);
		assertEquals(((TestResultEncapsulation)eventSequence.get(1)).event, "handleException");
		assertEquals(((TestResultEncapsulation)eventSequence.get(1)).result, null);
	}
	
	public static class MyAction extends ActionSupport {
		private static final long serialVersionUID = -5014600447925835654L;

		public String execute() throws Exception {
			Boolean bool = (Boolean) ActionContext.getContext().getContextMap().get("forceException");
			if (! bool.booleanValue()) {
				return super.execute();
			} else {
				throw new RuntimeException("Forced Exception");
			}
		}
		
	}
	
	
	public class InternalLifecycleInterceptor extends AbstractLifecycleInterceptor {
		
		private static final long serialVersionUID = -6987132432154659517L;

		public void beforeResult(ActionInvocation invocation, String resultCode) {
			super.beforeResult(invocation, resultCode);
			eventSequence.add(new TestResultEncapsulation("beforeResult", null, resultCode));
		}
		
		protected void before(ActionInvocation invocation) throws Exception {
			super.before(invocation);
			eventSequence.add(new TestResultEncapsulation("before", invocation, null));
		}
		
		protected void after(ActionInvocation invocation, String result) throws Exception {
			super.after(invocation, result);
			eventSequence.add(new TestResultEncapsulation("after", invocation, result));
		}

		protected void handleException(Exception e) throws Exception {
			try {
				super.handleException(e);
			} catch (Exception exp) {
				eventSequence.add(new TestResultEncapsulation("handleException", null, null));
			}
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
