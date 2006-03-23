/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.conversion.annotations.Conversion;
import com.opensymphony.xwork.conversion.annotations.TypeConversion;
import com.opensymphony.xwork.util.Bar;
import com.opensymphony.xwork.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork.validator.annotations.Validation;


/**
 * Implemented by SimpleAction3 and TestBean2 to test class hierarchy traversal.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
@Validation()
@Conversion()
public interface AnnotationDataAware {

    void setBarObj(Bar b);

    @TypeConversion(
            converter = "com.opensymphony.xwork.util.FooBarConverter"
    )
    Bar getBarObj();

    @RequiredFieldValidator(message = "You must enter a value for data.")
    @RequiredStringValidator(message = "You must enter a value for data.")
    void setData(String data);

    String getData();
}
