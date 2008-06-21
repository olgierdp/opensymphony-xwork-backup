/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * IntRangeValidatorTest
 * <p/>
 * Created : Jan 21, 2003 12:16:01 AM
 *
 * @author Jason Carreira
 */
public class IntRangeValidatorTest extends XWorkTestCase {

    public void testRangeValidation() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("bar", "5");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List<String> errorMessages = errors.get("bar");
            assertEquals(1, errorMessages.size());

            String errorMessage = errorMessages.get(0);
            assertNotNull(errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    protected void setUp() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());
    }
}
