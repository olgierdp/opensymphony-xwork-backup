/*
 * Created on 28/02/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork;

import junit.framework.TestCase;

import com.opensymphony.xwork.config.ConfigurationManager;


/**
 * @author CameronBraid
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ChainResultTest extends TestCase {

    public void testRecursiveChain() throws Exception {

        // allowed 1 chain
        ActionChainResult.maxChainDepth = 1;
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", "RecursionChain", null);
        try {
            proxy.execute();
            fail("infinite recursion not detected");
        } catch (XworkException e) {
            assertTrue(e.getMessage().indexOf("infinite") != -1);
        }
        
    }

    public void testValidChain() throws Exception {

        ActionChainResult.maxChainDepth = 100;
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", "RecursionChain", null);
        proxy.execute();
        
    }

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.getConfiguration().reload();
    }
}
