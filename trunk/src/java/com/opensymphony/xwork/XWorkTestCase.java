/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork;

import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.util.LocalizedTextUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.XWorkConverter;
import junit.framework.TestCase;

/**
 * Base JUnit TestCase to extend for XWork specific unit tests.
 *
 * @author plightbo
 */
public abstract class XWorkTestCase extends TestCase {

    protected ConfigurationManager configurationManager = new ConfigurationManager();
    
    protected void setUp() throws Exception {
        // Reset the value stack
        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));

        //  clear out configuration
        configurationManager.destroyConfiguration();

        // clear out localization
        LocalizedTextUtil.reset();

        // type conversion
        XWorkConverter.resetInstance();

        // reset ognl
        OgnlValueStack.reset();
    }

    protected void tearDown() throws Exception {
        // reset the old object factory
        ObjectFactory.setObjectFactory(new ObjectFactory());

        //  clear out configuration
        configurationManager.destroyConfiguration();
    }
}
