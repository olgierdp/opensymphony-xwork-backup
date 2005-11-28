/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.util.Bar;
import com.opensymphony.xwork.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork.validator.annotations.Validation;
import com.opensymphony.xwork.validator.annotations.RequiredStringValidator;


/**
 * Implemented by SimpleAction3 and TestBean2 to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
@Validation()
public interface AnnotationDataAware {

    void setBarObj(Bar b);

    Bar getBarObj();

    @RequiredFieldValidator(message = "You must enter a value for data.")
    @RequiredStringValidator(message = "You must enter a value for data.")
    void setData(String data);

    String getData();
}
