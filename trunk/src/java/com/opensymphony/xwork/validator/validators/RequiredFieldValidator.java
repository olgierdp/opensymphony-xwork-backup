/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.validator.ValidationException;


/**
 * RequiredFieldValidator
 *
 * @author $Author$
 * @version $Revision$
 */
public class RequiredFieldValidator extends FieldValidatorSupport {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Action action) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, action);

        if (value == null) {
            addFieldError(fieldName, action);
        }
    }
}
