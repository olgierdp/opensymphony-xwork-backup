/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork.test.AnnotationDataAware;


/**
 * Used to test hierarchy traversal for interfaces.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
public interface AnnotationDataAware2 extends AnnotationDataAware {

    @RequiredStringValidator(message = "You must enter a value for data.")
    public void setBling(String bling);

    public String getBling();
}
