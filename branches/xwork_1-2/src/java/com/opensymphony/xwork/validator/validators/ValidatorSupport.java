/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.TextParseUtil;
import com.opensymphony.xwork.validator.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Abstract implementation of the Validator interface suitable for subclassing.
 *
 * @author Jason Carreira
 * @author tmjee
 * @version $Date$ $Id$
 */
public abstract class ValidatorSupport implements Validator, ShortCircuitableValidator {

    protected final Log log = LogFactory.getLog(this.getClass());
    protected String defaultMessage = "";
    protected String messageKey = null;
    private ValidatorContext validatorContext;
    private boolean shortCircuit;
    private boolean parse = false;
    private String type;
    private String[] messageParameters;


    public void setParse(boolean parse) { 
    	this.parse = parse;
    }
    
    public boolean getParse() {
    	return parse;
    }
    
    public void setDefaultMessage(String message) {
        this.defaultMessage = message;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessage(Object object) {
        String message;
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        stack.push(this);

        if (messageKey != null) {
            if ((defaultMessage == null) || (defaultMessage.trim().equals(""))) {
                defaultMessage = messageKey;
            }
            if ( validatorContext == null) {
                validatorContext = new DelegatingValidatorContext(object);
            }

            // The message key we have are ognl expression, let's parse it first
            List parsedMessageParameters = null;
            if (messageParameters != null) {
                parsedMessageParameters = new ArrayList();
                for (int a=0; a<messageParameters.length; a++) {
                    if (messageParameters[a] != null) {
                        try {
                            Object val = stack.findValue(messageParameters[a]);
                            parsedMessageParameters.add(val);
                        } catch(Exception e) {
                            // if there's an exception in parsing, we'll just treat the expression itself as the
                            // parameter
                            log.warn("exception while parsing message parameter ["+messageParameters[a]+"]", e);
                            parsedMessageParameters.add(messageParameters[a]);
                        }
                    }
                }
            }

            message = validatorContext.getText(messageKey, defaultMessage, parsedMessageParameters);
        } else {
            message = defaultMessage;
        }

        message = TextParseUtil.translateVariables(message, stack);

        stack.pop();

        if (pop) {
            stack.pop();
        }

        return message;
    }

    public void setMessageKey(String key) {
        messageKey = key;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public String[] getMessageParameters() {
        return messageParameters;
    }

    public void setShortCircuit(boolean shortcircuit) {
        shortCircuit = shortcircuit;
    }

    public boolean isShortCircuit() {
        return shortCircuit;
    }

    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    public void setValidatorType(String type) {
        this.type = type;
    }

    public String getValidatorType() {
        return type;
    }
    
    /**
     * Parse <code>expression</code> passed in against value stack. Only parse
     * when 'parse' param is set to true, else just returns the expression unparsed.
     * 
     * @param expression
     * @return
     */
    protected Object conditionalParse(String expression) {
    	if (parse) {
    		OgnlValueStack stack = ActionContext.getContext().getValueStack();
    		return TextParseUtil.translateVariables('$', expression, stack);
    	}
    	return expression;
    }

    /**
     * Return the field value named <code>name</code> from <code>object</code>, 
     * <code>object</code> should have the appropriate getter/setter.
     * 
     * @param name
     * @param object
     * @return
     * @throws ValidationException
     */
    protected Object getFieldValue(String name, Object object) throws ValidationException {
        OgnlValueStack stack = ActionContext.getContext().getValueStack();

        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        Object retVal = stack.findValue(name);

        if (pop) {
            stack.pop();
        }

        return retVal;
    }

    protected void addActionError(Object object) {
        validatorContext.addActionError(getMessage(object));
    }

    protected void addFieldError(String propertyName, Object object) {
        validatorContext.addFieldError(propertyName, getMessage(object));
    }
}
