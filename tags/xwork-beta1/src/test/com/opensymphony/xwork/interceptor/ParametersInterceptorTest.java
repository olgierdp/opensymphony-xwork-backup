/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;

import junit.framework.TestCase;

import java.util.HashMap;


/**
 * ParametersInterceptorTest
 *
 * Created : Jan 15, 2003 8:49:15 PM
 *
 * @author Jason Carreira
 */
public class ParametersInterceptorTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testParameters() {
        HashMap params = new HashMap();
        params.put("blah", "This is blah");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
            proxy.execute();
            assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
