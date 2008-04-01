/*
 * Copyright (c) 2002-2008 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

/**
 * <code>WildCardResultTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: WildCardResultTest.java 1219 2006-11-18 22:38:58Z mrdon $
 */
public class DefaultClasstTest extends XWorkTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-sample.xml"));
    }

    public void testWildCardEvaluation() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("Abstract-crud", "edit", null);
        assertEquals("com.opensymphony.xwork2.SimpleAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example", "edit", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
         

        proxy = actionProxyFactory.createActionProxy("/example2", "override", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example2/subItem", "save", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example2", "list", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example3", "list", null);
        assertEquals("com.opensymphony.xwork2.SimpleAction", proxy.getConfig().getClassName());
    }

}
