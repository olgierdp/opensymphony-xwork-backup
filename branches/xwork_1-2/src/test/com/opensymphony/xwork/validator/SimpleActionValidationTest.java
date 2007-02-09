/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.validators.ValidatorSupport;

import java.util.*;


/**
 * SimpleActionValidationTest
 * <p/>
 * Created : Jan 20, 2003 11:04:25 PM
 *
 * @author Jason Carreira
 */
public class SimpleActionValidationTest extends XWorkTestCase {

    private Locale origLocale;


    public void testAliasValidation() {
        HashMap params = new HashMap();
        params.put("baz", "10");

        //valid values
        params.put("bar", "7");
        params.put("date", "12/23/2002");
        params.put("percentage", "1.23456789");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();

            ValidationAware validationAware = (ValidationAware) proxy.getAction();
            assertFalse(validationAware.hasFieldErrors());

            // put in an out-of-range value to see if the old validators still work
            params.put("bar", "42");
            proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ALIAS_NAME, extraContext);
            proxy.execute();
            validationAware = (ValidationAware) proxy.getAction();
            assertTrue(validationAware.hasFieldErrors());

            Map errors = validationAware.getFieldErrors();
            assertTrue(errors.containsKey("baz"));

            List bazErrors = (List) errors.get("baz");
            assertEquals(1, bazErrors.size());

            String message = (String) bazErrors.get(0);
            assertEquals("baz out of range.", message);
            assertTrue(errors.containsKey("bar"));

            List barErrors = (List) errors.get("bar");
            assertEquals(1, barErrors.size());
            message = (String) barErrors.get(0);
            assertEquals("bar must be between 6 and 10, current value is 42.", message);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testLookingUpFieldNameAsTextKey() {
        HashMap params = new HashMap();

        // should cause a message
        params.put("baz", "-1");

        //valid values
        params.put("bar", "7");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List bazErrors = (List) errors.get("baz");
            assertEquals(1, bazErrors.size());

            String errorMessage = (String) bazErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("Baz Field must be greater than 0", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMessageKey() {
        HashMap params = new HashMap();
        params.put("foo", "200");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            OgnlValueStack stack = new OgnlValueStack();
            ActionContext.setContext(new ActionContext(stack.getContext()));
            ActionContext.getContext().setLocale(Locale.US);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List fooErrors = (List) errors.get("foo");
            assertEquals(1, fooErrors.size());

            String errorMessage = (String) fooErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("Foo Range Message", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMessageKeyIsReturnedIfNoOtherDefault() throws ValidationException {
        Validator validator = new ValidatorSupport() {
            public void validate(Object object) throws ValidationException {
                addActionError(object);
            }
        };

        String messageKey = "does.not.exist";
        validator.setMessageKey(messageKey);

        ValidatorContext validatorContext = new DelegatingValidatorContext(new SimpleAction());
        validator.setValidatorContext(validatorContext);
        validator.validate(this);
        assertTrue(validatorContext.hasActionErrors());

        Collection errors = validatorContext.getActionErrors();
        assertEquals(1, errors.size());
        assertEquals(messageKey, errors.toArray()[0]);
    }

    public void testParamterizedMessage() {
        HashMap params = new HashMap();
        params.put("bar", "42");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List barErrors = (List) errors.get("bar");
            assertEquals(1, barErrors.size());

            String errorMessage = (String) barErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("bar must be between 6 and 10, current value is 42.", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSubPropertiesAreValidated() {
        HashMap params = new HashMap();
        params.put("baz", "10");

        //valid values
        params.put("foo", "8");
        params.put("bar", "7");
        params.put("date", "12/23/2002");

        params.put("bean.name", "Name should be valid");

        // this should cause a message
        params.put("bean.count", "100");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_SUBPROPERTY_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List beanCountErrors = (List) errors.get("bean.count");
            assertEquals(1, beanCountErrors.size());

            String errorMessage = (String) beanCountErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("bean.count out of range.", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void setUp() throws Exception {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        ConfigurationManager.destroyConfiguration();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }

    protected void tearDown() throws Exception {
        Locale.setDefault(origLocale);
    }
}
