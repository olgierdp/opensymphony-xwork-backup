/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import java.util.Date;


/**
 * DateRangeFieldValidator
 * @author Jason Carreira
 * Created Feb 9, 2003 1:23:42 AM
 */
public class DateRangeFieldValidator extends AbstractRangeValidator {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Date max;
    private Date min;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMax(Date max) {
        this.max = max;
    }

    public Date getMax() {
        return max;
    }

    public void setMin(Date min) {
        this.min = min;
    }

    public Date getMin() {
        return min;
    }

    protected Comparable getMaxComparatorValue() {
        return max;
    }

    protected Comparable getMinComparatorValue() {
        return min;
    }
}
