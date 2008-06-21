/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;

import java.util.Map;

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
        ActionConfig ac = new ActionConfig.Builder("", "", "").build();

        Map params = ac.getParams();

        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(mock.proxy());
        mock.expect("setParams", params);

        interceptor.intercept(mai);
        mock.verify();
    }

    public void testWithOneParameters() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "Santa")
                .build();

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
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "${top.hero}")
                .build();
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
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "${top.hero}")
                .build();
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new StaticParametersInterceptor();
        interceptor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
    }

    private class User {
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
