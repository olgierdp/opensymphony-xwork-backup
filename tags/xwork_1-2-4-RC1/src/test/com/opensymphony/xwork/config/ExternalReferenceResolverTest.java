/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ExternalReferenceAction;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;
import junit.framework.TestCase;

import java.util.HashMap;


/**
 * Test support for external-ref tag in xwork.xml. This tag allows objects from 'external' sources
 * to be used by an action.
 * 
 * @author Ross
 */
public class ExternalReferenceResolverTest extends TestCase {

    /**
     * test that resolver has been loaded and given to the package config
     */
    public void testResolverIsInstanciated() throws Exception {
        RuntimeConfiguration config = ConfigurationManager.getConfiguration().getRuntimeConfiguration();
        PackageConfig packageConfig = ConfigurationManager.getConfiguration().getPackageConfig("default");

        assertNotNull("There should be a package called 'default'", packageConfig);

        ExternalReferenceResolver err = packageConfig.getExternalRefResolver();
        assertNotNull(err);
        assertTrue(err instanceof TestExternalReferenceResolver);
    }

    /**
     * The TestExternalRefResolver5 is defined in a child package which doesn't have an external
     * reference resolver defined on it, so the resolver should be used from its parent
     *
     * @throws Exception
     */
    public void testResolverOnParentPackage() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("test/externalRef/", "TestExternalRefResolver4", null);

        ExternalReferenceAction erAction = (ExternalReferenceAction) proxy.getAction();

        proxy.getInvocation().invoke();

        assertNotNull("The Foo object should have been resolved", erAction.getFoo());
        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction.getFoo().getName());
    }

    /**
     * Test that the ActionInvocation implementation uses the resolver to resolve
     * external references
     *
     * @throws Exception because it wants to!
     */
    public void testResolverResolvesDependancies() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "TestExternalRefResolver", null);
        Object action = proxy.getAction();
        assertNotNull("Action should be null", action);
        assertTrue("Action should be an ExternalReferenceAction", action instanceof ExternalReferenceAction);

        ExternalReferenceAction erAction = (ExternalReferenceAction) action;
        assertNull("The Foo object should not have been resolved yet", erAction.getFoo());

        proxy.getInvocation().invoke();

        assertNotNull("The Foo object should have been resolved", erAction.getFoo());
        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction.getFoo().getName());
    }

    /**
     * Test that required dependacies cause exception when not found and non-dependant do not
     * TestExternalRefResolver2 has two external-refs, one of which doesn't exist but is also not required
     *
     * @throws Exception
     */
    public void testResolverRespectsRequiredDependancies() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "TestExternalRefResolver2", null);
        Object action = proxy.getAction();
        assertNotNull("Action should be null", action);
        assertTrue("Action should be an ExternalReferenceAction", action instanceof ExternalReferenceAction);

        ExternalReferenceAction erAction = (ExternalReferenceAction) action;
        assertNull("The Foo object should not have been resolved yet", erAction.getFoo());

        proxy.getInvocation().invoke();

        assertNotNull("The Foo object should have been resolved", erAction.getFoo());
        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction.getFoo().getName());

        //now test that a required dependacy that is missing will throw an exception
        proxy = ActionProxyFactory.getFactory().createActionProxy(null, "TestExternalRefResolver3", null);
        action = proxy.getAction();
        assertNotNull("Action should be null", action);
        erAction = (ExternalReferenceAction) action;

        try {
            proxy.getInvocation().invoke();
            fail("Invoking the action should have thrown ReferenceResolverException");
        } catch (ReferenceResolverException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        super.setUp();

        ActionContext.setContext(new ActionContext(new HashMap()));

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider c = new XmlConfigurationProvider();
        ConfigurationManager.addConfigurationProvider(c);
        ConfigurationManager.getConfiguration().reload();
    }

    protected void tearDown() throws Exception {
        ActionContext.setContext(null);
        super.tearDown();
    }
}
