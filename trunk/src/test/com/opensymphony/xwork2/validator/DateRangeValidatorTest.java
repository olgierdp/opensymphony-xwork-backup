/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.validator.validators.DateRangeFieldValidator;
import junit.framework.TestCase;

import java.util.*;


/**
 * DateRangeValidatorTest
 *
 * @author Jason Carreira
 *         Created Feb 9, 2003 1:25:42 AM
 */
public class DateRangeValidatorTest extends XWorkTestCase {

    private Locale origLocale;


    /**
     * Tests whether the date range validation is working. Should produce an validation error,
     * because the action config sets date to 12/20/2002 while expected range is Dec 22-25.
     */
    public void testRangeValidation() throws Exception {
        ActionProxy proxy = container.getInstance(ActionProxyFactory.class).createActionProxy(
                configurationManager.getConfiguration(), "", MockConfigurationProvider.VALIDATION_ACTION_NAME, null);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        Iterator it = errors.entrySet().iterator();

        List errorMessages = (List) errors.get("date");
        assertNotNull("Expected date range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = (String) errorMessages.get(0);
        assertNotNull(errorMessage);
    }

    public void testGetSetMinMax() throws Exception {
        DateRangeFieldValidator val = new DateRangeFieldValidator();
        Date max = new Date();
        val.setMax(max);
        assertEquals(max, val.getMax());

        Date min = new Date();
        val.setMin(min);
        assertEquals(min, val.getMin());
    }

    protected void setUp() throws Exception {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        configurationManager = new ConfigurationManager();
        configurationManager.addConfigurationProvider(new XmlConfigurationProvider("xwork-test-beans.xml"));
        configurationManager.addConfigurationProvider(new MockConfigurationProvider());
        configurationManager.reload();
        container = configurationManager.getConfiguration().getContainer();
        ObjectFactory.setObjectFactory(container.getInstance(ObjectFactory.class));
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(origLocale);
    }
}
