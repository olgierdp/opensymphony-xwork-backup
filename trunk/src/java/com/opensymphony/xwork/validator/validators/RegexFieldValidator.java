package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.validator.ValidationException;

/**
 * Validates a string field using a regular expression.
 *
 * @author Quake Wang
 * @version $Revision$
 * @since 2004-7-23
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