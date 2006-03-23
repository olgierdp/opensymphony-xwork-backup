/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork;

import junit.framework.TestCase;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.mock.MockResult;

/**
 * <code>WildCardResultTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class WildCardResultTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.getConfiguration().reload();
    }

    public void testWildCardEvaluation() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(null, "WildCard", null);
        assertEquals("success", proxy.execute());
        assertEquals(VoidResult.class, proxy.getInvocation().getResult().getClass());

        proxy = ActionProxyFactory.getFactory().createActionProxy(null, "WildCardInput", null);
        assertEquals("input", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());

        proxy = ActionProxyFactory.getFactory().createActionProxy(null, "WildCardError", null);
        assertEquals("error", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());
    }

}
