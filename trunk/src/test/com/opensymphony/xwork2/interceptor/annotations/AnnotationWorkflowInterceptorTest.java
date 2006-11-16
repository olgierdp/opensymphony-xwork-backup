/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor.annotations;

import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class AnnotationWorkflowInterceptorTest extends XWorkTestCase {
    private static final String ANNOTATED_ACTION = "annotatedAction";
    private static final String SHORTCIRCUITED_ACTION = "shortCircuitedAction";
    private final AnnotationWorkflowInterceptor annotationInterceptor = new AnnotationWorkflowInterceptor();

    public void setUp() {
        configurationManager = new ConfigurationManager();
        configurationManager.addConfigurationProvider(new MockConfigurationProvider());
        configurationManager.getConfiguration().reload(configurationManager.getConfigurationProviders());
        container = configurationManager.getConfiguration().getContainer();
        ObjectFactory.setObjectFactory(container.getInstance(ObjectFactory.class));

        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
        
    }

    public void testInterceptsBeforeAndAfter() throws Exception {
        ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(configurationManager.getConfiguration(), "", ANNOTATED_ACTION, null);
        assertEquals(Action.SUCCESS, proxy.execute());
        AnnotatedAction action = (AnnotatedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before-execute-beforeResult-after", action.log);
    }

    public void testInterceptsShortcircuitedAction() throws Exception {
        ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(configurationManager.getConfiguration(), "", SHORTCIRCUITED_ACTION, null);
        assertEquals("shortcircuit", proxy.execute());
        ShortcircuitedAction action = (ShortcircuitedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before", action.log);
    }

    private class MockConfigurationProvider implements ConfigurationProvider {
        private Configuration config;

        public void init(Configuration configuration) throws ConfigurationException {
            this.config = configuration;
        }

        public boolean needsReload() {
            return false;
        }

        public void destroy() { }


        public void register(ContainerBuilder builder, Properties props) throws ConfigurationException {
            if (!builder.contains(ObjectFactory.class)) {
                builder.factory(ObjectFactory.class);
            }
            if (!builder.contains(ActionProxyFactory.class)) {
                builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
            }
        }

        public void loadPackages() throws ConfigurationException {
            PackageConfig packageConfig = new PackageConfig();
            config.addPackageConfig("default", packageConfig);

            ActionConfig actionConfig = new ActionConfig(null, AnnotatedAction.class, null, null,
                    Arrays.asList(new InterceptorMapping[]{ new InterceptorMapping("annotationInterceptor", annotationInterceptor) }));
            packageConfig.addActionConfig(ANNOTATED_ACTION, actionConfig);
            actionConfig.addResultConfig(new ResultConfig("success", MockResult.class.getName()));
            actionConfig = new ActionConfig(null, ShortcircuitedAction.class, null, null,
                    Arrays.asList(new InterceptorMapping[]{ new InterceptorMapping("annotationInterceptor", annotationInterceptor) }));
            packageConfig.addActionConfig(SHORTCIRCUITED_ACTION, actionConfig);
            actionConfig.addResultConfig(new ResultConfig("shortcircuit", MockResult.class.getName()));
            config.addPackageConfig("defaultPackage", packageConfig);
        }
    }
}
