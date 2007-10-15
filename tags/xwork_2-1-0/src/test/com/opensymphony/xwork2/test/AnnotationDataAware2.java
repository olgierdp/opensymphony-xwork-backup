/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.test.AnnotationDataAware;


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
