/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.util.TextUtils;

import com.opensymphony.xwork.validator.ValidationException;


/**
 * EmailValidator checks that a given String field, if not empty, is a valid email address.
 */
public class EmailValidator extends FieldValidatorSupport {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String value = (String) this.getFieldValue(fieldName, object);

        if (value == null) {
            return;
        } else {
            value = value.trim();

            if (value.length() == 0) {
                return;
            }
        }

        if (!TextUtils.verifyEmail(value)) {
            addFieldError(fieldName, object);
        }
    }
}
