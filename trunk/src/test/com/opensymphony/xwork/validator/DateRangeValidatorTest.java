/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;


/**
 * DateRangeValidatorTest
 * @author Jason Carreira
 * Created Feb 9, 2003 1:25:42 AM
 */
public class DateRangeValidatorTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testRangeValidation() {
        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, null);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List errorMessages = (List) errors.get("date");
            assertEquals(1, errorMessages.size());

            String errorMessage = (String) errorMessages.get(0);
            assertNotNull(errorMessage);
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
