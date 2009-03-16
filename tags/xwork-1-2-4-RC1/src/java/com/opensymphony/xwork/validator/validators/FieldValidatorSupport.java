/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.FieldValidator;


/**
 * Base class for field validators.
 *
 * @author Jason Carreira
 */
public abstract class FieldValidatorSupport extends ValidatorSupport implements FieldValidator {

    private String fieldName;
    private String type;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setValidatorType(String type) {
        this.type = type;
    }

    public String getValidatorType() {
        return type;
    }
}
