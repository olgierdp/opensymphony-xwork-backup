/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.XWorkConverter;
import com.opensymphony.xwork.validator.ValidationException;

import java.util.Map;


/**
 * <p>
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if a conversion error occured for this field.
 * <!-- END SNIPPET: javadoc -->
 * </p>
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *     <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * </pre>
 * 
 * 
 * <!-- START SNIPPET: example -->
 *     &lt;!-- Plain Validator Syntax --&gt;
 *     &lt;validator type="conversion"&gt;
 *     		&lt;param name="fieldName"&gt;myField&lt;/param&gt;
 *          &lt;message&gt;Conversion Error Occurred&lt;/message&gt;
 *     &lt;/validator&gt;
 *      
 *     &lt;!-- Field Validator Syntax --&gt;
 *     &lt;field name="myField"&gt;
 *        &lt;field-validator type="conversion"&gt;
 *           &lt;message&gt;Conversion Error Occurred&lt;/message&gt;
 *        &lt;/field-validator&gt;
 *     &lt;/field&gt;
 * <!-- END SNIPPET: example -->
 * 
 *
 * @author Jason Carreira
 *         Date: Nov 28, 2003 1:58:49 PM
 */
public class ConversionErrorFieldValidator extends FieldValidatorSupport {

    /**
     * The validation implementation must guarantee that setValidatorContext will
     * be called with a non-null ValidatorContext before validate is called.
     *
     * @param object
     * @throws ValidationException
     */
    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
        ActionContext context = ActionContext.getContext();
        Map conversionErrors = context.getConversionErrors();

        if (conversionErrors.containsKey(fullFieldName)) {
            if ((defaultMessage == null) || (defaultMessage.trim().equals(""))) {
                defaultMessage = XWorkConverter.getConversionErrorMessage(fullFieldName, context.getValueStack());
            }

            addFieldError(fieldName, object);
        }
    }
}
