/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor.annotations;

import java.util.Arrays;

import junit.framework.TestCase;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class AnnotationWorkflowInterceptorTest extends TestCase {
	private static final String ANNOTATED_ACTION = "annotatedAction";
	private static final String SHORTCIRCUITED_ACTION = "shortCircuitedAction";
	private final AnnotationWorkflowInterceptor annotationInterceptor = new AnnotationWorkflowInterceptor();
	
	public void setUp() {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
	}
	
	public void testInterceptsBeforeAndAfter() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", ANNOTATED_ACTION, null, false);
        assertEquals(Action.SUCCESS, proxy.execute());
        AnnotatedAction action = (AnnotatedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before-execute-beforeResult-after", action.log);		
	}
	
	public void testInterceptsShortcircuitedAction() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", SHORTCIRCUITED_ACTION, null, false);
        assertEquals("shortcircuit", proxy.execute());
        ShortcircuitedAction action = (ShortcircuitedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before", action.log);		
	}
	
	private class MockConfigurationProvider implements ConfigurationProvider {

		public void init(Configuration configuration) throws ConfigurationException {
			PackageConfig packageConfig = new PackageConfig();
			configuration.addPackageConfig("default", packageConfig);
			
			ActionConfig actionConfig = new ActionConfig(null, AnnotatedAction.class, null, null, 
					Arrays.asList(new Object[]{ annotationInterceptor }));
			packageConfig.addActionConfig(ANNOTATED_ACTION, actionConfig);
			actionConfig = new ActionConfig(null, ShortcircuitedAction.class, null, null, 
					Arrays.asList(new Object[]{ annotationInterceptor }));
			packageConfig.addActionConfig(SHORTCIRCUITED_ACTION, actionConfig);
	        configuration.addPackageConfig("defaultPackage", packageConfig);			
		}

		public boolean needsReload() {
			return false;
		}
		
		public void destroy() { }
	}
}
