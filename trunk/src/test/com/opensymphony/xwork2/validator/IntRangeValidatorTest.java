/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

import junit.framework.TestCase;

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
        HashMap params = new HashMap();
        params.put("bar", "5");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                    configurationManager.getConfiguration(), "", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List errorMessages = (List) errors.get("bar");
            assertEquals(1, errorMessages.size());

            String errorMessage = (String) errorMessages.get(0);
            assertNotNull(errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        configurationManager = new ConfigurationManager();
        configurationManager.addConfigurationProvider(new XmlConfigurationProvider("xwork-test-beans.xml"));
        configurationManager.addConfigurationProvider(new MockConfigurationProvider());
        configurationManager.reload();
        container = configurationManager.getConfiguration().getContainer();
        ObjectFactory.setObjectFactory(container.getInstance(ObjectFactory.class));
    }
}
