/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
*/
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.*;

import java.util.HashMap;
import java.util.Map;


/**
 * AliasInterceptorTest
 *
 * Test of aliasInterceptor specifically depends on actionTest test defined in /test/xwork.xml
 * stack.getContext().putAll(params);
 * <p/>
 * e.g.
 * <action name="aliasTest" class="com.opensymphony.xwork2.SimpleAction">
 *    <param name="aliases">#{ "aliasSource" : "aliasDest", "bar":"baz" }</param>
 *    <interceptor-ref name="defaultStack"/>
 *    <interceptor-ref name="alias"/>
 * </action>
 *
 * @author Matthew Payne
 */
public class AliasInterceptorTest extends XWorkTestCase {

    public void testUsingDefaultInterceptorThatAliasPropertiesAreCopied() throws Exception {
        Map params = new HashMap();
        params.put("aliasSource", "source here");

        loadConfigurationProviders(new XmlConfigurationProvider("xwork-sample.xml"));
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");
        actionOne.setFoo(17);
        actionOne.setBar(23);
        proxy.execute();
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());
    }

    public void testInvalidAliasExpression() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig();
        Map params = new HashMap();
        params.put("aliases", "invalid alias expression");
        cfg.setParams(params);
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();

        ai.intercept(mai);

        ai.destroy();
    }

    public void testSetAliasKeys() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig();
        Map params = new HashMap();
        params.put("hello", "invalid alias expression");
        cfg.setParams(params);
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();
        ai.setAliasesKey("hello");

        ai.intercept(mai);

        ai.destroy();
    }

    public void testSetInvalidAliasKeys() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig();
        Map params = new HashMap();
        params.put("hello", "invalid alias expression");
        cfg.setParams(params);
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();
        ai.setAliasesKey("iamnotinconfig");

        ai.intercept(mai);

        ai.destroy();
    }

}

