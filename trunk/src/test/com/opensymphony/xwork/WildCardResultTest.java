/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork;

import com.opensymphony.xwork.mock.MockResult;
import junit.framework.TestCase;

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
        configurationManager.getConfiguration().reload(configurationManager.getConfigurationProviders());
    }

    public void testWildCardEvaluation() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy(
                configurationManager.getConfiguration(), null, "WildCard", null);
        assertEquals("success", proxy.execute());
        assertEquals(VoidResult.class, proxy.getInvocation().getResult().getClass());

        proxy = ActionProxyFactory.getFactory().createActionProxy(
                configurationManager.getConfiguration(), null, "WildCardInput", null);
        assertEquals("input", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());

        proxy = ActionProxyFactory.getFactory().createActionProxy(
                configurationManager.getConfiguration(), null, "WildCardError", null);
        assertEquals("error", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());
    }

}
