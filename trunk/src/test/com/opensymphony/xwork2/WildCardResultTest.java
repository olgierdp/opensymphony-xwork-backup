/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2;

import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

/**
 * <code>WildCardResultTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class WildCardResultTest extends XWorkTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        configurationManager.clearConfigurationProviders();
        configurationManager.addConfigurationProvider(new XmlConfigurationProvider("xwork-sample.xml"));
        configurationManager.getConfiguration().reload(configurationManager.getConfigurationProviders());
    }

    public void testWildCardEvaluation() throws Exception {
        ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                configurationManager.getConfiguration(), null, "WildCard", null);
        assertEquals("success", proxy.execute());
        assertEquals(VoidResult.class, proxy.getInvocation().getResult().getClass());

        proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                configurationManager.getConfiguration(), null, "WildCardInput", null);
        assertEquals("input", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());

        proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                configurationManager.getConfiguration(), null, "WildCardError", null);
        assertEquals("error", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());
    }

}
