/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;


/**
 * AbstractRangeValidator
 *
 * @author Jason Carreira
 * @author Cameron Braid
 *         Created Feb 9, 2003 1:16:28 AM
 */
public abstract class AbstractRangeValidator extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        Object obj = getFieldValue(getFieldName(), object);
        Comparable value = (Comparable) obj;

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null) {
            return;
        }

        // only check for a minimum value if the min parameter is set
        if ((getMinComparatorValue() != null) && (value.compareTo(getMinComparatorValue()) < 0)) {
            addFieldError(getFieldName(), object);
        }

        // only check for a maximum value if the max parameter is set
        if ((getMaxComparatorValue() != null) && (value.compareTo(getMaxComparatorValue()) > 0)) {
            addFieldError(getFieldName(), object);
        }
    }

    protected abstract Comparable getMaxComparatorValue();

    protected abstract Comparable getMinComparatorValue();
}
