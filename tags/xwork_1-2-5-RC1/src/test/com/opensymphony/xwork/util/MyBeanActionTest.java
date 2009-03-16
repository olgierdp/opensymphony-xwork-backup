/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.config.ConfigurationManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>MyBeanActionTest</code>
 *
 * @author Rainer Hermanns
 */
public class MyBeanActionTest extends XWorkTestCase {

    public void testIndexedList() {
        HashMap params = new HashMap();
        params.put("beanList(1234567890).name", "This is the bla bean");
        params.put("beanList(1234567891).name", "This is the 2nd bla bean");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", "MyBean", extraContext);
            proxy.execute();
            assertEquals(2, Integer.parseInt(proxy.getInvocation().getStack().findValue("beanList.size").toString()));
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanList.get(0)").getClass().getName());
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanList.get(1)").getClass().getName());

            assertEquals("This is the bla bean", proxy.getInvocation().getStack().findValue("beanList.get(0).name"));
            assertEquals(new Long(1234567890), Long.valueOf(proxy.getInvocation().getStack().findValue("beanList.get(0).id").toString()));
            assertEquals("This is the 2nd bla bean", proxy.getInvocation().getStack().findValue("beanList.get(1).name"));
            assertEquals(new Long(1234567891), Long.valueOf(proxy.getInvocation().getStack().findValue("beanList.get(1).id").toString()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testIndexedMap() {
        HashMap params = new HashMap();
        params.put("beanMap[1234567890].id", "1234567890");
        params.put("beanMap[1234567891].id", "1234567891");

        params.put("beanMap[1234567890].name", "This is the bla bean");
        params.put("beanMap[1234567891].name", "This is the 2nd bla bean");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", "MyBean", extraContext);
            proxy.execute();
            MyBeanAction action = (MyBeanAction) proxy.getInvocation().getAction();

            assertEquals(2, Integer.parseInt(proxy.getInvocation().getStack().findValue("beanMap.size").toString()));

            Map map = (Map) proxy.getInvocation().getStack().findValue("beanMap");
            assertEquals(true, action.getBeanMap().containsKey(new Long(1234567890)));
            assertEquals(true, action.getBeanMap().containsKey(new Long(1234567891)));


            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanMap.get(1234567890L)").getClass().getName());
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanMap.get(1234567891L)").getClass().getName());

            assertEquals("This is the bla bean", proxy.getInvocation().getStack().findValue("beanMap.get(1234567890L).name"));
            assertEquals("This is the 2nd bla bean", proxy.getInvocation().getStack().findValue("beanMap.get(1234567891L).name"));

            assertEquals("1234567890", proxy.getInvocation().getStack().findValue("beanMap.get(1234567890L).id").toString());
            assertEquals("1234567891", proxy.getInvocation().getStack().findValue("beanMap.get(1234567891L).id").toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.getConfiguration().reload();
    }
}
