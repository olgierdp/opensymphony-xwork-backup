/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.validators.ExpressionValidator;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for ExpressionValidator.
 *
 * @author Jason Carreira
 * @author Claus Ibsen
 */
public class ExpressionValidatorTest extends XWorkTestCase {

    public void testExpressionValidationOfStringLength() throws ValidationException {
        TestBean bean = new TestBean();
        bean.setName("abc");
        ActionContext.getContext().getValueStack().push(bean);

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManagerFactory.getInstance().validate(bean, "expressionValidation", context);
        assertTrue(context.hasFieldErrors());

        final Map fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("name"));

        List nameErrors = (List) fieldErrors.get("name");
        assertEquals(1, nameErrors.size());
        assertEquals("Name must be greater than 5 characters, it is currently 'abc'", nameErrors.get(0));

        bean.setName("abcdefg");
        context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManagerFactory.getInstance().validate(bean, "expressionValidation", context);
        assertFalse(context.hasFieldErrors());
    }

    public void testExpressionValidatorFailure() throws Exception {
        HashMap params = new HashMap();
        params.put("date", "12/23/2002");
        params.put("foo", "5");
        params.put("bar", "7");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasActionErrors());

        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(1, errors.size());

        String message = (String) errors.iterator().next();
        assertNotNull(message);
        assertEquals("Foo must be greater than Bar. Foo = 5, Bar = 7.", message);
    }

    public void testExpressionValidatorSuccess() throws Exception {
        HashMap params = new HashMap();

        //make it not fail
        params.put("date", "12/23/2002");
        params.put("foo", "10");
        params.put("bar", "7");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    public void testGetSetExpresion() {
        ExpressionValidator ev = new ExpressionValidator();
        ev.setExpression("{top}");
        assertEquals("{top}", ev.getExpression());
    }

    public void testNoBooleanExpression() throws Exception {
        Mock mock = new Mock(ValidationAware.class);
        mock.expect("addActionError", C.ANY_ARGS);

        ExpressionValidator ev = new ExpressionValidator();
        ev.setValidatorContext(new DelegatingValidatorContext(mock.proxy()));
        ev.setExpression("{top}");

        ev.validate("Hello"); // {top} will evalute to Hello that is not a Boolean
        mock.verify();
    }

    protected void setUp() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));

        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new MockConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();
    }
    
}
