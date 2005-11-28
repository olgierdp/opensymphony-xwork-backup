/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork.validator.annotations.Validations;
import com.opensymphony.xwork.validator.annotations.RequiredStringValidator;

import java.util.Date;


/**
 * TestBean
 * @author Jason Carreira
 * Created Aug 4, 2003 12:39:53 AM
 */
public class TestBean {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Date birth;
    private String name;
    private int count;

    //~ Constructors ///////////////////////////////////////////////////////////

    public TestBean() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getBirth() {
        return birth;
    }

    /*
    <field name="count">
        <field-validator type="int" short-circuit="true">
            <param name="min">1</param>
            <param name="max">100</param>
            <message key="invalid.count">Invalid Count!</message>
        </field-validator>
        <field-validator type="int">
            <param name="min">20</param>
            <param name="max">80</param>
            <message key="invalid.count.bad">Smaller Invalid Count: ${count}</message>
        </field-validator>
    </field>
    */
    @Validations(
            intRangeFields = {
                @IntRangeFieldValidator(shortCircuit = true, min = "1", max="100", key="invalid.count", message = "Invalid Count!"),
                @IntRangeFieldValidator(shortCircuit = true, min = "20", max="28", key="invalid.count.bad", message = "Smaller Invalid Count: ${count}")
            }

    )
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    /*
    <field name="name">
        <field-validator type="requiredstring">
            <message>You must enter a name.</message>
        </field-validator>
    </field>
    */
    @RequiredStringValidator(message = "You must enter a name.")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
