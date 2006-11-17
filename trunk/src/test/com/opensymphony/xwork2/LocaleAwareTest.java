/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import java.util.Locale;


/**
 * LocaleAwareTest
 *
 * @author Jason Carreira
 *         Created Feb 10, 2003 6:13:13 PM
 */
public class LocaleAwareTest extends XWorkTestCase {

    public void testGetText() {
        try {
            ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                    configurationManager.getConfiguration(), "", MockConfigurationProvider.FOO_ACTION_NAME, null);
            ActionContext.getContext().setLocale(Locale.US);

            TextProvider localeAware = (TextProvider) proxy.getAction();
            assertEquals("Foo Range Message", localeAware.getText("foo.range"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testLocaleGetText() {
        try {
            ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                    configurationManager.getConfiguration(), "", MockConfigurationProvider.FOO_ACTION_NAME, null);
            ActionContext.getContext().setLocale(Locale.GERMANY);

            TextProvider localeAware = (TextProvider) proxy.getAction();
            assertEquals("I don't know German", localeAware.getText("foo.range"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        configurationManager = new ConfigurationManager();
        configurationManager.addConfigurationProvider(new XmlConfigurationProvider("xwork-test-beans.xml"));
        configurationManager.addConfigurationProvider(new MockConfigurationProvider());
        configurationManager.reload();
        container = configurationManager.getConfiguration().getContainer();
        ObjectFactory.setObjectFactory(container.getInstance(ObjectFactory.class));

        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
    }
}
