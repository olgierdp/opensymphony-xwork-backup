/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

/**
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if the double specified is within a certain range.
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 * 		<li>min - the minimum value (if none is specified, it will not be checked) </li>
 * 		<li>max - the maximum value (if none is specified, it will not be checked) </li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 * 		&lt;validators>
 *           &lt;!-- Plain Validator Syntax --&gt;
 *           &lt;validator type="double">
 *               &lt;param name="fieldName"&gt;percentage&lt;/param&gt;
 *               &lt;param name="min"&gt;20.1&lt;/param&gt;
 *               &lt;param name="max"&gt;50.1&lt;/param&gt;
 *               &lt;message&gt;Age needs to be between ${min} and ${max}&lt;/message&gt;
 *           &lt;/validator&gt;
 *
 *           &lt;!-- Field Validator Syntax --&gt;
 *           &lt;field name="percentage"&gt;
 *               &lt;field-validator type="double"&gt;
 *                   &lt;param name="min"&gt;0.123&lt;/param&gt;
 *                   &lt;param name="max"&gt;99.98&lt;/param&gt;
 *                   &lt;message&gt;Percentage needs to be between ${min} and ${max}&lt;/message&gt;
 *               &lt;/field-validator&gt;
 *           &lt;/field&gt;
 *      &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public class DoubleRangeFieldValidator extends AbstractRangeValidator {

    Double max = null;
    Double min = null;


    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMax() {
        return max;
    }

    public Comparable getMaxComparatorValue() {
        return max;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMin() {
        return min;
    }

    public Comparable getMinComparatorValue() {
        return min;
    }
}
