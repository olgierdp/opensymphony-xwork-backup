/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.ValidationException;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class FieldExpressionValidator extends FieldValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String expression;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Action action) throws ValidationException {
        String fieldName = getFieldName();

        Boolean answer = Boolean.FALSE;
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        Object obj = stack.findValue(expression);

        if ((obj != null) && (obj instanceof Boolean)) {
            answer = (Boolean) obj;
        } else {
            log.warn("Got result of " + obj + " when trying to get Boolean.");
        }

        if (!answer.booleanValue()) {
            addFieldError(fieldName, action);
        }
    }
}
