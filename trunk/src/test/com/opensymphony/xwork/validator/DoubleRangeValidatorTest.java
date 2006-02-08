package com.opensymphony.xwork.validator;

import junit.framework.TestCase;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.config.ConfigurationManager;

/**
 * <code>DoubleRangeValidatorTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class DoubleRangeValidatorTest extends TestCase {

    public void testRangeValidationWithError() throws Exception {
        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, null);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        Iterator it = errors.entrySet().iterator();

        List errorMessages = (List) errors.get("percentage");
        assertNotNull("Expected double range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = (String) errorMessages.get(0);
        assertNotNull("Expecting: percentage must be between 0.1 and 10.1, current value is 100.0123.", errorMessage);
        assertEquals("percentage must be between 0.1 and 10.1, current value is 100.0123.", errorMessage);
    }

    public void testRangeValidationNoError() throws Exception {

        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", "percentage", null);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        Iterator it = errors.entrySet().iterator();

        List errorMessages = (List) errors.get("percentage");
        assertNull("Expected no double range validation error message.", errorMessages);

    }

    protected void setUp() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
