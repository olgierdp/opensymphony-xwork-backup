/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;


/**
 * IntRangeFieldValidator
 *
 * Created : Jan 20, 2003 11:36:43 PM
 *
 * @author Jason Carreira
 */
public class IntRangeFieldValidator extends AbstractRangeValidator {
    //~ Instance fields ////////////////////////////////////////////////////////

    Integer max = null;
    Integer min = null;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMax() {
        return max;
    }

    public Comparable getMaxComparatorValue() {
        return max;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMin() {
        return min;
    }

    public Comparable getMinComparatorValue() {
        return min;
    }
}
