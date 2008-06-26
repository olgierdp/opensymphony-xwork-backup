/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;


/**
 * StringLengthFieldValidator checks that a String field is of a certain length.  If the "minLength"
 * parameter is specified, it will make sure that the String has at least that many characters.  If
 * the "maxLength" parameter is specified, it will make sure that the String has at most that many
 * characters.  The "trim" parameter determines whether it will {@link String#trim() trim} the
 * String before performing the length check.  If unspecified, the String will be trimmed.
 *
 *
 * @author Jason Carreira
 * @author Mark Woon
 */
public class StringLengthFieldValidator extends FieldValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean doTrim = true;
    private int maxLength = -1;
    private int minLength = -1;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setTrim(boolean trim) {
        doTrim = trim;
    }

    public boolean getTrim() {
        return doTrim;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String val = (String) getFieldValue(fieldName, object);

        if (val == null) {
            addFieldError(fieldName, object);
        } else {
            if (doTrim) {
                val = val.trim();
            }

            if ((minLength > -1) && (val.length() < minLength)) {
                addFieldError(fieldName, object);
            } else if ((maxLength > -1) && (val.length() > maxLength)) {
                addFieldError(fieldName, object);
            }
        }
    }
}
