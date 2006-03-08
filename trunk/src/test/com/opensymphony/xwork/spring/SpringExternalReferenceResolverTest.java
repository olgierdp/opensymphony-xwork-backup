/*
 * Created on Jun 9, 2004
 */
package com.opensymphony.xwork.spring;

import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ExternalReferenceResolver;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Mike
 */
public class SpringExternalReferenceResolverTest extends XWorkTestCase {
    ApplicationContext applicationContext;

    public void setUp() throws Exception {
        super.setUp();
        //Create the spring Application context
        applicationContext = new ClassPathXmlApplicationContext(
                "com/opensymphony/xwork/spring/resolverApplicationContext.xml");
        //ensure we're using the default xwork configuration
        XmlConfigurationProvider c = new XmlConfigurationProvider("com/opensymphony/xwork/spring/xwork-ext.xml");
        ConfigurationManager.addConfigurationProvider(c);
        ConfigurationManager.getConfiguration().reload();
    }

    /**
     * test that resolver has been loaded and given to the package config
     */
    public void testResolverIsInstanciated() throws Exception {
        PackageConfig packageConfig = ConfigurationManager.getConfiguration()
                .getPackageConfig("default");

        assertNotNull("There should be a package called 'default'",
                packageConfig);

        ExternalReferenceResolver err = packageConfig.getExternalRefResolver();
        assertNotNull(err);
        assertTrue(err instanceof SpringExternalReferenceResolver);
    }

    /**
     * Test that the ActionInvocation implementation uses the resolver to
     * resolve external references
     */
//    public void testSpringApplicationContextReferenceResolver()
//            throws Exception {
//        initialiseReferenceResolver();
//
//        ActionProxy proxy = AbstractActionProxyFactory.getFactory().createActionProxy(
//                null, "TestExternalRefResolver", null);
//
//        Object action = proxy.getAction();
//        assertNotNull("Action should be null", action);
//        assertTrue("Action should be an ExternalReferenceAction",
//                action instanceof ExternalReferenceAction);
//
//        ExternalReferenceAction erAction = (ExternalReferenceAction) action;
//        assertNull("The Foo object should not have been resolved yet", erAction
//                .getFoo());
//
//        proxy.getInvocation().invoke();
//
//        assertNotNull("The Foo object should have been resolved", erAction
//                .getFoo());
//        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction
//                .getFoo().getName());
//
//        assertNotNull("The Bar object should have been resolved", erAction
//                .getBar());
//        assertEquals("Bar value should be 16", 16, erAction.getBar().getValue());
//
//        assertNotNull(
//                "The Bar object should have a received Foo Object by autowire",
//                erAction.getBar().getFoo());
//
//    }

    /**
     * Test that required dependacies cause exception when not found and
     * non-dependant do not TestExternalRefResolver2 has two external-refs, one
     * of which doesn't exist but is also not required
     *
     * @throws Exception
     */
//    public void testResolverRespectsRequiredDependancies() throws Exception {
//        initialiseReferenceResolver();
//
//        ActionProxy proxy = AbstractActionProxyFactory.getFactory().createActionProxy(
//                null, "TestExternalRefResolver2", null);
//        Object action = proxy.getAction();
//        assertNotNull("Action should be null", action);
//        assertTrue("Action should be an ExternalReferenceAction",
//                action instanceof ExternalReferenceAction);
//
//        ExternalReferenceAction erAction = (ExternalReferenceAction) action;
//        assertNull("The Foo object should not have been resolved yet", erAction
//                .getFoo());
//
//        proxy.getInvocation().invoke();
//
//        assertNotNull("The Foo object should have been resolved", erAction
//                .getFoo());
//        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction
//                .getFoo().getName());
//
//        //now test that a required dependacy that is missing will throw an
//        // exception
//        proxy = AbstractActionProxyFactory.getFactory().createActionProxy(null,
//                "TestExternalRefResolver3", null);
//        action = proxy.getAction();
//        assertNotNull("Action should be null", action);
//        erAction = (ExternalReferenceAction) action;
//
//        try {
//            proxy.getInvocation().invoke();
//            fail("Invoking the action should have thrown ReferenceResolverException");
//        } catch (ReferenceResolverException e) {
//            // expected
//        }
//    }

    /**
     * The TestExternalRefResolver4 has a external-ref declared without the
     * actual external ref defined
     *
     * @throws Exception
     */
//    public void testResolutionBasedOnTypeOnly() throws Exception {
//        initialiseReferenceResolver();
//
//        ActionProxy proxy = AbstractActionProxyFactory.getFactory().createActionProxy(
//                null, "TestExternalRefResolver4", null);
//
//        ExternalReferenceAction erAction = (ExternalReferenceAction) proxy
//                .getAction();
//
//        proxy.getInvocation().invoke();
//
//        assertNotNull("The Foo object should have been resolved", erAction
//                .getFoo());
//        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction
//                .getFoo().getName());
//
//    }

    /**
     * The TestExternalRefResolver5 is defined in a child package which doesn't
     * have an external reference resolver defined on it, so the resolver should
     * be used from its parent
     *
     * @throws Exception
     */
//    public void testResolverOnParentPackage() throws Exception {
//        initialiseReferenceResolver();
//
//        ActionProxy proxy = AbstractActionProxyFactory.getFactory().createActionProxy(
//                "test/", "TestExternalRefResolver5", null);
//
//        ExternalReferenceAction erAction = (ExternalReferenceAction) proxy
//                .getAction();
//
//        proxy.getInvocation().invoke();
//
//        assertNotNull("The Foo object should have been resolved", erAction
//                .getFoo());
//        assertEquals("Foos name should be 'Little Foo'", "Little Foo", erAction
//                .getFoo().getName());
//
//    }

    /**
     * sets the applicationContext on the resolver
     */
    private void initialiseReferenceResolver() {
        PackageConfig packageConfig = ConfigurationManager.getConfiguration()
                .getPackageConfig("default");
        assertNotNull("There should be a package called 'default'",
                packageConfig);
        //A small hack to set the applicationContext on the resolver
        ((SpringExternalReferenceResolver) packageConfig
                .getExternalRefResolver())
                .setApplicationContext(applicationContext);
    }
}
