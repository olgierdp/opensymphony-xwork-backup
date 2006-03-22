/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.ValidationAwareSupport;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.validators.ConversionErrorFieldValidator;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ConversionErrorFieldValidatorTest
 *
 * @author Jason Carreira
 *         Date: Nov 28, 2003 3:45:37 PM
 */
public class ConversionErrorFieldValidatorTest extends TestCase {

    private static final String defaultFooMessage = "Invalid field value for field \"foo\".";


    private ActionContext oldContext;
    private ConversionErrorFieldValidator validator;
    private ValidationAware validationAware;


    public void setUp() {
        oldContext = ActionContext.getContext();

        OgnlValueStack stack = new OgnlValueStack();
        ActionContext context = new ActionContext(stack.getContext());
        ActionContext.setContext(context);

        Map conversionErrors = new HashMap();
        conversionErrors.put("foo", "bar");
        context.setConversionErrors(conversionErrors);
        validator = new ConversionErrorFieldValidator();
        validationAware = new ValidationAwareSupport();

        DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(validationAware);
        stack.push(validatorContext);
        validator.setValidatorContext(validatorContext);
        validator.setFieldName("foo");
        assertEquals(0, validationAware.getFieldErrors().size());
    }

    public void testConversionErrorMessageUsesProvidedMessage() throws ValidationException {
        String message = "default message";
        validator.setDefaultMessage(message);
        validator.validate(validationAware);

        Map fieldErrors = validationAware.getFieldErrors();
        assertTrue(fieldErrors.containsKey("foo"));
        assertEquals(message, ((List) fieldErrors.get("foo")).get(0));
    }

    public void testConversionErrorsAreAddedToFieldErrors() throws ValidationException {
        validator.validate(validationAware);

        Map fieldErrors = validationAware.getFieldErrors();
        assertTrue(fieldErrors.containsKey("foo"));
        assertEquals(defaultFooMessage, ((List) fieldErrors.get("foo")).get(0));
    }

    protected void tearDown() throws Exception {
        ActionContext.setContext(oldContext);
    }
}
