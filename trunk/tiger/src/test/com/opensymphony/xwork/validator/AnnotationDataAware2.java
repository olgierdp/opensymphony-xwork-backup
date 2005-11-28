/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.validator.annotations.RequiredStringValidator;


/**
 * Used to test hierarchy traversal for interfaces.
 *
 * @author Mark Woon
 */
public interface AnnotationDataAware2 extends AnnotationDataAware {

    @RequiredStringValidator(message = "You must enter a value for data.")
    public void setBling(String bling);

    public String getBling();
}
