/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork.validator.annotations.IntRangeFieldValidator;

/**
 * SimpleAction2
 *
 * @author Jason Carreira
 *         Created Jun 14, 2003 9:51:12 PM
 */
public class SimpleAnnotationAction2 extends SimpleAnnotationAction {

    private int count;

    /*
    <field name="count">
        <field-validator type="required">
            <message>You must enter a value for count.</message>
        </field-validator>
        <field-validator type="int">
            <param name="min">0</param>
            <param name="max">5</param>
            <message>count must be between ${min} and ${max}, current value is ${count}.</message>
        </field-validator>
    </field>
    */

    @RequiredFieldValidator(message = "You must enter a value for count.")
    @IntRangeFieldValidator(min = "0", max = "5", message = "count must be between ${min} and ${max}, current value is ${count}.")
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
