/*
 * Created on Jun 12, 2004
 */
package com.opensymphony.xwork.spring;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.spring.SpringResult;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test loading actions from the Spring Application Context.
 *
 * @author Simon Stewart
 */
public class ActionsFromSpringTest extends XWorkTestCase {
    private ApplicationContext appContext;

    protected void setUp() throws Exception {
        super.setUp();
        // Set up Spring and the ObjectFactory
        appContext = new ClassPathXmlApplicationContext("com/opensymphony/xwork/spring/actionContext-spring.xml");
        SpringObjectFactory springObjectFactory = new SpringObjectFactory();
        springObjectFactory.setApplicationContext(appContext);
        ObjectFactory.setObjectFactory(springObjectFactory);

        // Set up XWork
        XmlConfigurationProvider c = new XmlConfigurationProvider("com/opensymphony/xwork/spring/actionContext-xwork.xml");
        ConfigurationManager.addConfigurationProvider(c);
        ConfigurationManager.getConfiguration().reload();
    }

    public void testLoadSimpleAction() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "simpleAction", null);
        Object action = proxy.getAction();

        Action expected = (Action) appContext.getBean("simple-action");

        assertEquals(expected.getClass(), action.getClass());
    }

    public void testLoadActionWithDependencies() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "dependencyAction", null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        assertEquals("injected", action.getBlah());
    }

    public void testProxiedActionIsNotStateful() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "proxiedAction", null);
        SimpleAction action = (SimpleAction) proxy.getAction();

        action.setBlah("Hello World");

        proxy = ActionProxyFactory.getFactory().createActionProxy(null, "proxiedAction", null);
        action = (SimpleAction) proxy.getAction();

        // If the action is a singleton, this test will fail
        SimpleAction sa = new SimpleAction();
        assertEquals(sa.getBlah(), action.getBlah());

        // And if the advice is not being applied, this will be SUCCESS.
        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }

    public void testAutoProxiedAction() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "autoProxiedAction", null);

        SimpleAction action = (SimpleAction) proxy.getAction();

        String result = action.execute();
        assertEquals(Action.INPUT, result);
    }
    
    public void testActionWithSpringResult() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
        		null, "simpleActionSpringResult", null);
                
        proxy.execute();
        
        SpringResult springResult = (SpringResult) proxy.getInvocation().getResult();
        assertTrue(springResult.isInitialize());
        assertNotNull(springResult.getStringParameter());
    }
}
