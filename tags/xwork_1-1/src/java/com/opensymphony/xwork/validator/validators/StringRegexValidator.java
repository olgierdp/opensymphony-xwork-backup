/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * StringRegexValidator checks that a given String field, if not empty,
 * matches the configured regular expression.
 *
 * @author jhouse
 * @see #setRegex(String)
 * @see #setCaseSensitive(boolean)
 */
public class StringRegexValidator extends FieldValidatorSupport {

    private String regex = ".";
    private boolean caseSensitive = true;


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

        Pattern pattern = null;
        if (isCaseSensitive())
            pattern = Pattern.compile(getRegex());
        else
            pattern = Pattern.compile(getRegex(), Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(value);

        if (!matcher.matches()) {
            addFieldError(fieldName, object);
        }
    }

    /**
     * @return Returns the regular expression to be matched.
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Sets the regular expression to be matched.
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * @return Returns whether the expression should be matched against in
     *         a case-sensitive way.  Default is <code>true</code>.
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the expression should be matched against in
     * a case-sensitive way.  Default is <code>true</code>.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }


}


