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

    private boolean m_doTrim = true;
    private int m_maxLength = -1;
    private int m_minLength = -1;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMaxLength(int maxLength) {
        this.m_maxLength = maxLength;
    }

    public int getMaxLength() {
        return m_maxLength;
    }

    public void setMinLength(int minLength) {
        this.m_minLength = minLength;
    }

    public int getMinLength() {
        return m_minLength;
    }

    public void setTrim(boolean trim) {
        m_doTrim = trim;
    }

    public boolean getTrim() {
        return m_doTrim;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String val = (String) getFieldValue(fieldName, object);

        if (val == null) {
            addFieldError(fieldName, object);
        } else {
            if (m_doTrim) {
                val = val.trim();
            }

            if ((m_minLength > -1) && (val.length() < m_minLength)) {
                addFieldError(fieldName, object);
            } else if ((m_maxLength > -1) && (val.length() > m_maxLength)) {
                addFieldError(fieldName, object);
            }
        }
    }
}
