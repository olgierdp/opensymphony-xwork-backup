/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.XWorkConverter;
import com.opensymphony.xwork.validator.ValidationException;

import java.util.Map;


/**
 * ConversionErrorFieldValidator
 * @author Jason Carreira
 * Date: Nov 28, 2003 1:58:49 PM
 */
public class ConversionErrorFieldValidator extends FieldValidatorSupport {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
 * The validation implementation must guarantee that setValidatorContext will
 * be called with a non-null ValidatorContext before validate is called.
 * @param object
 * @throws ValidationException
 */
    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
        ActionContext context = ActionContext.getContext();
        Map conversionErrors = context.getConversionErrors();

        if (conversionErrors.containsKey(fullFieldName)) {
            if ((defaultMessage == null) || (defaultMessage.trim().equals(""))) {
                defaultMessage = XWorkConverter.getConversionErrorMessage(fullFieldName, context.getValueStack());
            }

            addFieldError(fieldName, object);
        }
    }
}
