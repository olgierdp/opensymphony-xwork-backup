/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;


/**
 * StringLengthFieldValidator
 * @author Jason Carreira
 * Date: May 20, 2004 9:02:16 PM
 */
public class StringLengthFieldValidator extends FieldValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private int maxLength = 0;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String val = (String) getFieldValue(fieldName, object);

        if (val.length() > maxLength) {
            addFieldError(fieldName, object);
        }
    }
}
