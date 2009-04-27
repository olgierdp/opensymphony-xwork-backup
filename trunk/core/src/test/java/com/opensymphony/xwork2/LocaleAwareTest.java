/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

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
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.FOO_ACTION_NAME, null);
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
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.FOO_ACTION_NAME, null);
            ActionContext.getContext().setLocale(Locale.GERMANY);

            TextProvider localeAware = (TextProvider) proxy.getAction();
            assertEquals("I don't know German", localeAware.getText("foo.range"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    protected void setUp() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());

        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));
    }
}
