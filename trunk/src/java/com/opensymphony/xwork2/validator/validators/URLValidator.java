/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.util.TextUtils;
import com.opensymphony.xwork2.validator.ValidationException;


/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * URLValidator checks that a given field is a String and a valid URL
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: parameters -->
 * 
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 * </ul>
 * 
 * <!-- END SNIPPET: parameters -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 * 
 *     &lt;validators&gt;
 *          &lt;!-- Plain Validator Syntax --&gt;
 *          &lt;validator type="url"&gt;
 *              &lt;param name="fieldName"&gt;myHomePage&lt;/param&gt;
 *              &lt;message&gt;Invalid homepage url&lt;/message&gt;
 *          &lt;/validator&gt;
 *          
 *          &lt;!-- Field Validator Syntax --&gt;
 *          &lt;field name="myHomepage"&gt;
 *              &lt;message&gt;Invalid homepage url&lt;/message&gt;
 *          &lt;/field&gt;
 *     &lt;/validators&gt;
 *     
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 *
 * @author $Author$
 * @version $Date$ $Revision$
 */
public class URLValidator extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null || value.toString().length() == 0) {
            return;
        }

        if (!(value.getClass().equals(String.class)) || !TextUtils.verifyUrl((String) value)) {
            addFieldError(fieldName, object);
        }
    }
}
