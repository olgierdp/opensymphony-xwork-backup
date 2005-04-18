/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ValidationAwareSupport;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.test.Equidae;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

/**
 * @author Mark Woon
 */
public class StringValidatorTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testRequiredString() throws ValidationException {
        Equidae equidae = new Equidae();

        // everything should fail
        equidae.setHorse("");
        ActionContext.getContext().getValueStack().push(equidae);

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManager.validate(equidae, null, context);

        assertTrue(context.hasFieldErrors());

        Map fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("horse"));
        assertEquals(2, ((List) fieldErrors.get("horse")).size());

        // trim = false should fail
        equidae.setHorse("  ");
        ActionContext.getContext().getValueStack().push(equidae);
        context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManager.validate(equidae, null, context);

        assertTrue(context.hasFieldErrors());
        fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("horse"));

        List errors = (List) fieldErrors.get("horse");
        assertEquals(1, errors.size());
        assertEquals("trim", (String) errors.get(0));
    }

    public void testStringLength() {
        try {
            Equidae equidae = new Equidae();

            equidae.setCow("asdf");
            equidae.setDonkey("asdf");
            ActionContext.getContext().getValueStack().push(equidae);

            DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
            ActionValidatorManager.validate(equidae, null, context);
            assertTrue(context.hasFieldErrors());

            Map fieldErrors = context.getFieldErrors();

            // cow
            assertTrue(fieldErrors.containsKey("cow"));

            List errors = (List) fieldErrors.get("cow");
            assertEquals(2, errors.size());
            assertEquals("noTrim-min5", errors.get(0));
            assertEquals("noTrim-min5-max10", errors.get(1));

            // donkey
            assertTrue(fieldErrors.containsKey("donkey"));
            errors = (List) fieldErrors.get("donkey");
            assertEquals(2, errors.size());
            assertEquals("trim-min5", errors.get(0));
            assertEquals("trim-min5-max10", errors.get(1));

            equidae.setCow("asdf  ");
            equidae.setDonkey("asdf  ");
            ActionContext.getContext().getValueStack().push(equidae);
            context = new DelegatingValidatorContext(new ValidationAwareSupport());
            ActionValidatorManager.validate(equidae, null, context);
            assertTrue(context.hasFieldErrors());

            fieldErrors = context.getFieldErrors();

            // cow
            assertFalse(fieldErrors.containsKey("cow"));

            // donkey
            assertTrue(fieldErrors.containsKey("donkey"));
            errors = (List) fieldErrors.get("donkey");
            assertEquals(2, errors.size());
            assertEquals("trim-min5", errors.get(0));
            assertEquals("trim-min5-max10", errors.get(1));

            equidae.setCow("asdfasdf");
            equidae.setDonkey("asdfasdf");
            ActionContext.getContext().getValueStack().push(equidae);
            context = new DelegatingValidatorContext(new ValidationAwareSupport());
            ActionValidatorManager.validate(equidae, null, context);
            assertTrue(context.hasFieldErrors());

            fieldErrors = context.getFieldErrors();

            // cow
            assertFalse(fieldErrors.containsKey("cow"));

            // donkey
            assertFalse(fieldErrors.containsKey("donkey"));

            equidae.setCow("asdfasdf   ");
            equidae.setDonkey("asdfasdf   ");
            ActionContext.getContext().getValueStack().push(equidae);
            context = new DelegatingValidatorContext(new ValidationAwareSupport());
            ActionValidatorManager.validate(equidae, null, context);
            assertTrue(context.hasFieldErrors());

            fieldErrors = context.getFieldErrors();

            // cow
            assertTrue(fieldErrors.containsKey("cow"));
            errors = (List) fieldErrors.get("cow");
            assertEquals(2, errors.size());
            assertEquals("noTrim-min5-max10", errors.get(0));
            assertEquals("noTrim-max10", errors.get(1));

            // donkey
            assertFalse(fieldErrors.containsKey("donkey"));

            equidae.setCow("asdfasdfasdf");
            equidae.setDonkey("asdfasdfasdf");
            ActionContext.getContext().getValueStack().push(equidae);
            context = new DelegatingValidatorContext(new ValidationAwareSupport());
            ActionValidatorManager.validate(equidae, null, context);
            assertTrue(context.hasFieldErrors());

            fieldErrors = context.getFieldErrors();

            // cow
            assertTrue(fieldErrors.containsKey("cow"));
            errors = (List) fieldErrors.get("cow");
            assertEquals(2, errors.size());
            assertEquals("noTrim-min5-max10", errors.get(0));
            assertEquals("noTrim-max10", errors.get(1));

            // donkey
            assertTrue(fieldErrors.containsKey("donkey"));
            errors = (List) fieldErrors.get("donkey");
            assertEquals(2, errors.size());
            assertEquals("trim-min5-max10", errors.get(0));
            assertEquals("trim-max10", errors.get(1));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    protected void setUp() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));

        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
