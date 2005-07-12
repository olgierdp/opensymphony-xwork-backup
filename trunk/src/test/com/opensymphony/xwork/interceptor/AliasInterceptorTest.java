/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.mockobjects.dynamic.Mock;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.util.OgnlValueStack;

import junit.framework.TestCase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * AliasInterceptorTest
 *
 * @author Matthew Payne
 *         Date: June 28, 2005 
 *         test of aliasInterceptor
 *         specifically depends on 
 *         actionTest test defined in /test/xwork.xml
 *          stack.getContext().putAll(params);
       
       e.g.
        <action name="aliasTest" class="com.opensymphony.xwork.SimpleAction">
            <param name="foo">17</param>
            <param name="bar">23</param>
            <param name="aliases">#{ "aliasSource" : "aliasDest", "bar":"baz" }</param>
            <interceptor-ref name="defaultStack"/>
            <interceptor-ref name="alias"/>
        </action>
       
 */
public class AliasInterceptorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

          
        public void testAliasPropertiesCopied() throws Exception {
            
            Map params = new HashMap();
            params.put("aliasSource", "source here");
            
            ActionProxyFactory factory = ActionProxyFactory.getFactory();
            ActionProxy proxy = factory.createActionProxy("", "aliasTest", params);
            SimpleAction actionOne = (SimpleAction)proxy.getAction();
            actionOne.setAliasSource("name to be copied");
            System.out.println(proxy.execute());
            assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());       
            
        }

        
        
    protected void setUp() throws Exception {
        super.setUp();
        
    }
}

