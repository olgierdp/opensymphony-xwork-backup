/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.util.TextUtils;

import com.opensymphony.xwork.validator.ValidationException;


/**
 * RequiredStringValidator checks that a String field is non-null and has a length > 0 (ie it isn't "")
 *
 * @author $Author$
 * @version $Revision$
 */
public class RequiredStringValidator extends FieldValidatorSupport {
    //~ Methods ////////////////////////////////////////////////////////////////

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (!(value instanceof String) || !TextUtils.stringSet((String) value)) {
            addFieldError(fieldName, object);
        }
    }
}
