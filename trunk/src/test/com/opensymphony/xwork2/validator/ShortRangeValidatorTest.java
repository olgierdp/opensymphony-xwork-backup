/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ShortRangeValidatorTest
 * <p/>
 *
 */
public class ShortRangeValidatorTest extends XWorkTestCase {

    public void testRangeValidation() {
        HashMap params = new HashMap();
        params.put("shortFoo", "200");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List errorMessages = (List) errors.get("shortFoo");
            assertEquals(1, errorMessages.size());

            String errorMessage = (String) errorMessages.get(0);
            assertNotNull(errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());
    }
}
