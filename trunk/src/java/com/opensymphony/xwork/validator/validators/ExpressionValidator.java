/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.ValidationException;


/**
 * ExpressionValidator
 * @author Jason Carreira
 * Created Feb 15, 2003 9:59:04 PM
 */
public class ExpressionValidator extends ValidatorSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String expression;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Object object) throws ValidationException {
        Boolean answer = Boolean.FALSE;
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        Object obj = null;

        try {
            obj = stack.findValue(expression);
        } catch (Exception e) {
            log.warn("Caught exception while evaluating expression " + expression, e);
        }

        if ((obj != null) && (obj instanceof Boolean)) {
            answer = (Boolean) obj;
        } else {
            log.warn("Got result of " + obj + " when trying to get Boolean.");
        }

        if (!answer.booleanValue()) {
            addActionError(object);
        }
    }
}
