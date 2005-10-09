/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;


/**
 * Validator
 *
 * @author Jason Carreira
 *         Created Feb 15, 2003 3:55:51 PM
 */
public interface Validator {

    void setDefaultMessage(String message);

    String getDefaultMessage();

    String getMessage(Object object);

    void setMessageKey(String key);

    String getMessageKey();

    /**
     * This method will be called before validate with a non-null ValidatorContext.
     *
     * @param validatorContext
     */
    void setValidatorContext(ValidatorContext validatorContext);

    ValidatorContext getValidatorContext();

    /**
     * The validation implementation must guarantee that setValidatorContext will
     * be called with a non-null ValidatorContext before validate is called.
     *
     * @param object
     * @throws ValidationException
     */
    void validate(Object object) throws ValidationException;
}
