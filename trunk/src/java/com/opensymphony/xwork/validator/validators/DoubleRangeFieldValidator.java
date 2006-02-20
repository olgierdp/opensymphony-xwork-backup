/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;

/**
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if the double specified is within a certain range.
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *                 <li>fieldName - The field name this validator is validating. Required if using
Plain-Validator Syntax otherwise not required</li>
 *                 <li>minInclusive - the minimum inclusive value (if none is specified, it will
not be checked) </li>
 *                 <li>maxInclusive - the maximum inclusive value (if none is specified, it will
not be checked) </li>
 *                 <li>minExclusive - the minimum exclusive value (if none is specified, it will
not be checked) </li>
 *                 <li>maxExclusive - the maximum exclusive value (if none is specified, it will
not be checked) </li> *
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 *                 &lt;validators>
 *           &lt;!-- Plain Validator Syntax --&gt;
 *           &lt;validator type="double">
 *               &lt;param name="fieldName"&gt;percentage&lt;/param&gt;
 *               &lt;param name="minInclusive"&gt;20.1&lt;/param&gt;
 *               &lt;param name="maxInclusive"&gt;50.1&lt;/param&gt;
 *               &lt;message&gt;Age needs to be between ${minInclusive} and
${maxInclusive} (inclusive)&lt;/message&gt;
 *           &lt;/validator&gt;
 *
 *           &lt;!-- Field Validator Syntax --&gt;
 *           &lt;field name="percentage"&gt;
 *               &lt;field-validator type="double"&gt;
 *                   &lt;param name="minExclusive"&gt;0.123&lt;/param&gt;
 *                   &lt;param name="maxExclusive"&gt;99.98&lt;/param&gt;
 *                   &lt;message&gt;Percentage needs to be between ${minExclusive}
and ${maxExclusive} (exclusive)&lt;/message&gt;
 *               &lt;/field-validator&gt;
 *           &lt;/field&gt;
 *      &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class DoubleRangeFieldValidator extends FieldValidatorSupport {
    
    Double maxInclusive = null;
    Double minInclusive = null;
    Double minExclusive = null;
    Double maxExclusive = null;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Double value;
        try {
            Object obj = this.getFieldValue(fieldName, object);
            if (obj == null) {
                return;
            }
            value = Double.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return;
        }

        if ((maxInclusive != null && value.compareTo(maxInclusive) > 0) ||
                (minInclusive != null && value.compareTo(minInclusive) < 0) ||
                (maxExclusive != null && value.compareTo(maxExclusive) >= 0) ||
                (minExclusive != null && value.compareTo(minExclusive) <= 0)) {
            addFieldError(fieldName, object);
        }
    }

    public void setMaxInclusive(Double maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Double getMaxInclusive() {
        return maxInclusive;
    }

    public void setMinInclusive(Double minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Double getMinInclusive() {
        return minInclusive;
    }

    public Double getMinExclusive() {
        return minExclusive;
    }

    public void setMinExclusive(Double minExclusive) {
        this.minExclusive = minExclusive;
    }

    public Double getMaxExclusive() {
        return maxExclusive;
    }

    public void setMaxExclusive(Double maxExclusive) {
        this.maxExclusive = maxExclusive;
    }
}
