/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.LocaleAware;
import com.opensymphony.xwork.ValidationAware;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.Validator;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.MissingResourceException;


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

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setDefaultMessage(String message) {
        this.defaultMessage = message;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessage(Action action) {
        String message;

        if ((messageKey != null) && (action instanceof LocaleAware)) {
            try {
                LocaleAware localeAware = (LocaleAware) action;
                message = localeAware.getText(messageKey);
            } catch (MissingResourceException e) {
                message = defaultMessage;
            }
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

    protected Object getFieldValue(String name, Action action) throws ValidationException {
        try {
            return Ognl.getValue(name, action);
        } catch (OgnlException e) {
            final String msg = "Caught exception while getting value for field " + name;
            log.error(msg, e);
            throw new ValidationException(msg);
        }
    }

    protected void addActionError(Action action) {
        if (action instanceof ValidationAware) {
            ValidationAware validationAware = (ValidationAware) action;
            validationAware.addActionError(getMessage(action));
        } else {
            log.error("Validation error: " + getMessage(action));
        }
    }

    protected void addFieldError(String propertyName, Action action) {
        if (action instanceof ValidationAware) {
            ValidationAware validationAction = (ValidationAware) action;
            validationAction.addFieldError(propertyName, getMessage(action));
        } else {
            log.error("Validation error for " + propertyName + ":" + getMessage(action));
        }
    }
}
