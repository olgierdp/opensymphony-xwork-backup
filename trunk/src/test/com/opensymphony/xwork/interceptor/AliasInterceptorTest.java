/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.XWorkTestCase;

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
 *         stack.getContext().putAll(params);
 *         <p/>
 *         e.g.
 *         <action name="aliasTest" class="com.opensymphony.xwork.SimpleAction">
 *         <param name="foo">17</param>
 *         <param name="bar">23</param>
 *         <param name="aliases">#{ "aliasSource" : "aliasDest", "bar":"baz" }</param>
 *         <interceptor-ref name="defaultStack"/>
 *         <interceptor-ref name="alias"/>
 *         </action>
 */
public class AliasInterceptorTest extends XWorkTestCase {
    //~ Instance fields ////////////////////////////////////////////////////////


    public void testAliasPropertiesCopied() throws Exception {

        Map params = new HashMap();
        params.put("aliasSource", "source here");

        ActionProxyFactory factory = ActionProxyFactory.getFactory();
        ActionProxy proxy = factory.createActionProxy("", "aliasTest", params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");
        System.out.println(proxy.execute());
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());

    }


    protected void setUp() throws Exception {
        super.setUp();

    }
}

