/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;

/**
 * <!-- START SNIPPET: javadoc -->
 * Validates a string field using a regular expression.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * 
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 	  <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *    <li>expression - The RegExp expression  REQUIRED</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 *    &lt;validators&gt;
 *        &lt;!-- Plain Validator Syntax --&gt;
 *        &lt;validator type="regex"&gt;
 *            &lt;param name="fieldName"&gt;myStrangePostcode&lt;/param&gt;
 *            &lt;param name="expression"&gt;&lt;![CDATA[([aAbBcCdD][123][eEfFgG][456])]]&lt;&gt;/param&gt;
 *        &lt;/validator&gt;
 *    
 *        &lt;!-- Field Validator Syntax --&gt;
 *        &lt;field name="myStrangePostcode"&gt;
 *            &lt;field-validator type="regex"&gt;
 *               &lt;param name="expression"&gt;&lt;![CDATA[([aAbBcCdD][123][eEfFgG][456])]]&gt;&lt;/param&gt;
 *            &lt;/field-validator&gt;
 *        &lt;/field&gt;
 *    &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Quake Wang
 * @version $Date$ $Revision$
 */
public class RegexFieldValidator extends FieldValidatorSupport {
    
    private String expression;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null)
            return;
        if (!(value instanceof String) || !((String) value).matches(expression)) {
            addFieldError(fieldName, object);
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
