/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import junit.framework.TestCase;


/**
 * DefaultActionProxyTest
 * @author Jason Carreira
 * Created Aug 8, 2003 1:41:45 AM
 */
public class DefaultActionProxyTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        ConfigurationManager.destroyConfiguration();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
    }

    public void testActionNameIsSet() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.FOO_ACTION_NAME, null);
        assertEquals(MockConfigurationProvider.FOO_ACTION_NAME, ActionContext.getContext().getName());
    }
}
