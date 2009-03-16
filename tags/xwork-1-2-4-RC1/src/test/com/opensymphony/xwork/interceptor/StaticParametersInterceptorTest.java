/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.SimpleFooAction;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.config.entities.Parameterizable;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.mock.MockActionProxy;
import com.mockobjects.dynamic.Mock;

import java.util.Map;
import java.util.HashMap;

/**
 * Unit test of {@link StaticParametersInterceptor}.
 *
 * @author Claus Ibsen
 */
public class StaticParametersInterceptorTest extends XWorkTestCase {

    private StaticParametersInterceptor interceptor;

    public void testParameterizable() throws Exception {
        Mock mock = new Mock(Parameterizable.class);

        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig();

        Map params = new HashMap();
        ac.setParams(params);

        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(mock.proxy());
        mock.expect("setParams", params);

        interceptor.intercept(mai);
        mock.verify();
    }

    public void testNoParameters() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig();

        ac.setParams(null);
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        int before = ActionContext.getContext().getValueStack().size();
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
    }

    public void testWithOneParameters() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig();

        Map params = new HashMap();
        params.put("top.name", "Santa");
        ac.setParams(params);
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("Santa", user.getName());
    }

    public void testWithOneParametersParse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig();

        Map params = new HashMap();
        params.put("top.name", "${top.hero}");
        ac.setParams(params);
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.setParse("true");
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("Superman", user.getName());
    }

    public void testWithOneParametersNoParse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig();

        Map params = new HashMap();
        params.put("top.name", "${top.hero}");
        ac.setParams(params);
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.setParse("false");
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("${top.hero}", user.getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new StaticParametersInterceptor();
        interceptor.init();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
    }

    public static class User {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHero() {
            return "Superman";
        }
    }

}
