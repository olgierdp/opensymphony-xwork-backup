/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlUtil;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.Validator;
import com.opensymphony.xwork.validator.ValidatorContext;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ValidatorSupport
 * @author Jason Carreira
 * Created Feb 15, 2003 3:58:21 PM
 */
public abstract class ValidatorSupport implements Validator {
    //~ Instance fields ////////////////////////////////////////////////////////

    protected final Log log = LogFactory.getLog(this.getClass());
    private String defaultMessage = "";
    private String messageKey = null;
    private ValidatorContext validatorContext;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setDefaultMessage(String message) {
        this.defaultMessage = message;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessage(Object object) {
        String message;

        if (messageKey != null) {
            message = validatorContext.getText(messageKey, defaultMessage);
        } else {
            message = defaultMessage;
        }

        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(this);
        message = TextParseUtil.translateVariables(message, stack);
        stack.pop();

        return message;
    }

    public void setMessageKey(String key) {
        messageKey = key;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    protected Object getFieldValue(String name, Object object) throws ValidationException {
        try {
            return Ognl.getValue(OgnlUtil.compile(name), Ognl.createDefaultContext(object), object);
        } catch (OgnlException e) {
            final String msg = "Caught exception while getting value for field " + name;
            log.error(msg, e);
            throw new ValidationException(msg);
        }
    }

    protected void addActionError(Object object) {
        validatorContext.addActionError(getMessage(object));
    }

    protected void addFieldError(String propertyName, Object object) {
        validatorContext.addFieldError(propertyName, getMessage(object));
    }
}
