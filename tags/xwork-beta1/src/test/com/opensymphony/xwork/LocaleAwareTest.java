/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;

import junit.framework.TestCase;

import java.util.Locale;


/**
 * LocaleAwareTest
 * @author Jason Carreira
 * Created Feb 10, 2003 6:13:13 PM
 */
public class LocaleAwareTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testGetText() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.FOO_ACTION_NAME, null);
            ActionContext.getContext().setLocale(Locale.US);

            LocaleAware localeAware = (LocaleAware) proxy.getAction();
            assertEquals("Foo Range Message", localeAware.getText("foo.range"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testLocaleGetText() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.FOO_ACTION_NAME, null);
            ActionContext.getContext().setLocale(Locale.GERMANY);

            LocaleAware localeAware = (LocaleAware) proxy.getAction();
            assertEquals("I don't know German", localeAware.getText("foo.range"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
