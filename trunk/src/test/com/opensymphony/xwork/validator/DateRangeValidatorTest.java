/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.XWorkStatic;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.validator.validators.DateRangeFieldValidator;
import junit.framework.TestCase;

import java.util.*;


/**
 * DateRangeValidatorTest
 *
 * @author Jason Carreira
 *         Created Feb 9, 2003 1:25:42 AM
 */
public class DateRangeValidatorTest extends TestCase {

    private Locale origLocale;


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
        XWorkStatic.getConfigurationManager().clearConfigurationProviders();
        XWorkStatic.getConfigurationManager().addConfigurationProvider(new MockConfigurationProvider());
        XWorkStatic.getConfigurationManager().getConfiguration().reload();
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(origLocale);
    }
}
