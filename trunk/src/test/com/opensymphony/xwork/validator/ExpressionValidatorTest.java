/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.ValidationAwareSupport;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.providers.MockConfigurationProvider;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ExpressionValidatorTest
 * @author Jason Carreira
 * Created Feb 15, 2003 10:42:22 PM
 */
public class ExpressionValidatorTest extends TestCase {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void testExpressionValidationOfStringLength() throws ValidationException {
        TestBean bean = new TestBean();
        bean.setName("abc");
        ActionContext.getContext().getValueStack().push(bean);

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManager.validate(bean, "expressionValidation", context);
        assertTrue(context.hasFieldErrors());

        final Map fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("name"));

        List nameErrors = (List) fieldErrors.get("name");
        assertEquals(1, nameErrors.size());
        assertEquals("Name must be greater than 5 characters, it is currently 'abc'", nameErrors.get(0));

        bean.setName("abcdefg");
        context = new DelegatingValidatorContext(new ValidationAwareSupport());
        ActionValidatorManager.validate(bean, "expressionValidation", context);
        assertFalse(context.hasFieldErrors());
    }

    public void testExpressionValidatorFailure() {
        HashMap params = new HashMap();
        params.put("date", "12/23/2002");
        params.put("foo", "5");
        params.put("bar", "7");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasActionErrors());

            Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
            assertEquals(1, errors.size());

            String message = (String) errors.iterator().next();
            assertNotNull(message);
            assertEquals("Foo must be greater than Bar. Foo = 5, Bar = 7.", message);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testExpressionValidatorSuccess() {
        HashMap params = new HashMap();

        //make it not fail
        params.put("date", "12/23/2002");
        params.put("foo", "10");
        params.put("bar", "7");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = ActionProxyFactory.getFactory().createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
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
