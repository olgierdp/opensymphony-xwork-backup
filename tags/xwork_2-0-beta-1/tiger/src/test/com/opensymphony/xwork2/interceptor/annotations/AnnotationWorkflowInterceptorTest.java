/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor.annotations;

import java.util.Arrays;

import junit.framework.TestCase;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;

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
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", ANNOTATED_ACTION, null, false, false);
        assertEquals(Action.SUCCESS, proxy.execute());
        AnnotatedAction action = (AnnotatedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before-execute-beforeResult-after", action.log);
    }

    public void testInterceptsShortcircuitedAction() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", SHORTCIRCUITED_ACTION, null, false, false);
        assertEquals("shortcircuit", proxy.execute());
        ShortcircuitedAction action = (ShortcircuitedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before", action.log);
    }

    private class MockConfigurationProvider implements ConfigurationProvider {

        public void init(Configuration configuration) throws ConfigurationException {
            PackageConfig packageConfig = new PackageConfig();
            configuration.addPackageConfig("default", packageConfig);

            ActionConfig actionConfig = new ActionConfig(null, AnnotatedAction.class, null, null,
                    Arrays.asList(new Object[]{ new InterceptorMapping("annotationInterceptor", annotationInterceptor) }));
            packageConfig.addActionConfig(ANNOTATED_ACTION, actionConfig);
            actionConfig = new ActionConfig(null, ShortcircuitedAction.class, null, null,
                    Arrays.asList(new Object[]{ new InterceptorMapping("annotationInterceptor", annotationInterceptor) }));
            packageConfig.addActionConfig(SHORTCIRCUITED_ACTION, actionConfig);
            configuration.addPackageConfig("defaultPackage", packageConfig);
        }

        public boolean needsReload() {
            return false;
        }

        public void destroy() { }
    }
}
