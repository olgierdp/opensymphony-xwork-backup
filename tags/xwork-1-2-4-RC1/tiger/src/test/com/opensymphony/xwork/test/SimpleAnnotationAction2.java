/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork.SimpleAnnotationAction;

/**
 * SimpleAction2
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 *         Created Jun 14, 2003 9:51:12 PM
 */
public class SimpleAnnotationAction2 extends SimpleAnnotationAction {

    private int count;

    @RequiredFieldValidator(message = "You must enter a value for count.")
    @IntRangeFieldValidator(min = "0", max = "5", message = "count must be between ${min} and ${max}, current value is ${count}.")
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
