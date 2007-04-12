/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ValidationAwareSupport;
import com.opensymphony.xwork.XWorkTestCase;
import com.opensymphony.xwork.validator.validators.RequiredStringValidator;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.test.Equidae;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.List;
import java.util.Map;

/**
 * @author Mark Woon
 * @author tm_jee (tm_jee (at) yahoo.co.uk )
 */
public class StringValidatorTest extends XWorkTestCase {

	public void testRequiredStringWithNullValue() throws Exception {
		Equidae equidae = new Equidae();
		equidae.setHorse(null);
		
		DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
		ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);
		
		assertTrue(context.hasFieldErrors());
	}
	
	
    public void testRequiredString() throws Exception {
        Equidae equidae = new Equidae();

        // everything should fail
        equidae.setHorse("");
        ActionContext.getContext().getValueStack().push(equidae);

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);

        assertTrue(context.hasFieldErrors());

        Map fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("horse"));
        assertEquals(2, ((List) fieldErrors.get("horse")).size());

        // trim = false should fail
        equidae.setHorse("  ");
        ActionContext.getContext().getValueStack().push(equidae);
        context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);

        assertTrue(context.hasFieldErrors());
        fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("horse"));

        List errors = (List) fieldErrors.get("horse");
        assertEquals(1, errors.size());
        assertEquals("trim", (String) errors.get(0));
    }

    public void testStringLength() throws Exception {
        Equidae equidae = new Equidae();

        equidae.setCow("asdf");
        equidae.setDonkey("asdf");
        ActionContext.getContext().getValueStack().push(equidae);

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);
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
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);
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
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);
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
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);
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
        ActionValidatorManagerFactory.getInstance().validate(equidae, (String) null, context);
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
    }

    public void testGetSetTrim() {
        RequiredStringValidator val = new RequiredStringValidator();

        val.setTrim(true);
        assertEquals(true, val.getTrim());

        val.setTrim(false);
        assertEquals(false, val.getTrim());
    }

    protected void setUp() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));

        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
}
