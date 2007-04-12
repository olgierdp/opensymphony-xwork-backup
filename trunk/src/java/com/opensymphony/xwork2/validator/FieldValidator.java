/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

/**
 * The FieldValidator interface defines the methods to be implemented by FieldValidators.
 * Which are used by the XWork validation framework to validate Action properties before
 * executing the Action.
 */
public interface FieldValidator extends Validator {

    /**
     * Sets the field name to validate with this FieldValidator
     *
     * @param fieldName the field name
     */
    void setFieldName(String fieldName);

    /**
     * Gets the field name to be validated
     *
     * @return the field name
     */
    String getFieldName();

}
