/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.Action;


/**
 * Validator
 * @author Jason Carreira
 * Created Feb 15, 2003 3:55:51 PM
 */
public interface Validator {
    //~ Methods ////////////////////////////////////////////////////////////////

    void setDefaultMessage(String message);

    String getDefaultMessage();

    String getMessage(Action action);

    void setMessageKey(String key);

    String getMessageKey();

    void validate(Action action) throws ValidationException;
}
