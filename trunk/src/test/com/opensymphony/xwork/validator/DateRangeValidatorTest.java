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

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * DateRangeValidatorTest
 *
 * @author Jason Carreira
 *         Created Feb 9, 2003 1:25:42 AM
 */
public class DateRangeValidatorTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Locale origLocale;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Tests whether the date range validation is working. Should produce an validation error,
     * because the action config sets date to 12/20/2002 while expected range is Dec 22-25.
     */
    public void testRangeValidation() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, null);
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

    protected void setUp() throws Exception {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(origLocale);
    }
}
