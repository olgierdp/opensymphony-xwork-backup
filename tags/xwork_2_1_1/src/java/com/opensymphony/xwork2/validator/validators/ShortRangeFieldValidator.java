/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;


/**
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if the short specified is within a certain range.
 * <!-- END SNIPPET: javadoc -->
 * 
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *              <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *              <li>min - the minimum value (if none is specified, it will not be checked) </li>
 *              <li>max - the maximum value (if none is specified, it will not be checked) </li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: examples -->
 *              &lt;validators>
 *           &lt;!-- Plain Validator Syntax --&gt;
 *           &lt;validator type="short">
 *               &lt;param name="fieldName"&gt;age&lt;/param&gt;
 *               &lt;param name="min"&gt;20&lt;/param&gt;
 *               &lt;param name="max"&gt;50&lt;/param&gt;
 *               &lt;message&gt;Age needs to be between ${min} and ${max}&lt;/message&gt;
 *           &lt;/validator&gt;
 *           
 *           &lt;!-- Field Validator Syntax --&gt;
 *           &lt;field name="age"&gt;
 *               &lt;field-validator type="short"&gt;
 *                   &lt;param name="min"&gt;20&lt;/param&gt;
 *                   &lt;param name="max"&gt;50&lt;/param&gt;
 *                   &lt;message&gt;Age needs to be between ${min} and ${max}&lt;/message&gt;
 *               &lt;/field-validator&gt;
 *           &lt;/field&gt;
 *      &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 * 
 * 
 * 
 * @version $Date$
 */
public class ShortRangeFieldValidator extends AbstractRangeValidator {

    Short max = null;
    Short min = null;


    public void setMax(Short max) {
        this.max = max;
    }

    public Short getMax() {
        return max;
    }

    public Comparable getMaxComparatorValue() {
        return max;
    }

    public void setMin(Short min) {
        this.min = min;
    }

    public Short getMin() {
        return min;
    }

    public Comparable getMinComparatorValue() {
        return min;
    }
}
