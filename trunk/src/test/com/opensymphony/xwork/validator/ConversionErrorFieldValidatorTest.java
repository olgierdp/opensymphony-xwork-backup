package com.opensymphony.xwork.validator;

import junit.framework.TestCase;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.ValidationAwareSupport;
import com.opensymphony.xwork.validator.validators.ConversionErrorFieldValidator;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.HashMap;
import java.util.Map;

/**
 * ConversionErrorFieldValidatorTest
 * @author Jason Carreira
 * Date: Nov 28, 2003 3:45:37 PM
 */
public class ConversionErrorFieldValidatorTest extends TestCase {
    public void testConversionErrorsAreAddedToFieldErrors() throws ValidationException {
        ActionContext oldContext = ActionContext.getContext();
        try {
            OgnlValueStack stack = new OgnlValueStack();
            ActionContext context = new ActionContext(stack.getContext());
            ActionContext.setContext(context);
            Map conversionErrors = new HashMap();
            conversionErrors.put("foo","bar");
            context.setConversionErrors(conversionErrors);
            ConversionErrorFieldValidator validator = new ConversionErrorFieldValidator();
            ValidationAware validationAware = new ValidationAwareSupport();
            DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(validationAware);
            validator.setValidatorContext(validatorContext);
            validator.setFieldName("foo");
            assertEquals(0,validationAware.getFieldErrors().size());
            validator.validate(validationAware);
            Map fieldErrors = validationAware.getFieldErrors();
            assertTrue(fieldErrors.containsKey("foo"));
        } finally {
            ActionContext.setContext(oldContext);
        }
    }
}
