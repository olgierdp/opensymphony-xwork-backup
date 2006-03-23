/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import java.util.*;


/**
 * Dummy validator context to use to capture error messages.
 *
 * @author Mark Woon
 * @author Matthew Payne
 */
public class GenericValidatorContext extends DelegatingValidatorContext {

    private Collection actionErrors;
    private Collection actionMessages;
    private Map fieldErrors;


    public GenericValidatorContext(Object object) {
        super(object);
    }


    public synchronized void setActionErrors(Collection errorMessages) {
        this.actionErrors = errorMessages;
    }

    public synchronized Collection getActionErrors() {
        return new ArrayList(internalGetActionErrors());
    }

    public synchronized void setActionMessages(Collection messages) {
        this.actionMessages = messages;
    }

    public synchronized Collection getActionMessages() {
        return new ArrayList(internalGetActionMessages());
    }

    public synchronized void setFieldErrors(Map errorMap) {
        this.fieldErrors = errorMap;
    }

    /**
     * Get the field specific errors.
     *
     * @return an unmodifiable Map with errors mapped from fieldname (String) to Collection of String error messages
     */
    public synchronized Map getFieldErrors() {
        return new HashMap(internalGetFieldErrors());
    }

    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
    }

    /**
     * Add an Action level message to this Action
     */
    public void addActionMessage(String aMessage) {
        internalGetActionMessages().add(aMessage);
    }

    public synchronized void addFieldError(String fieldName, String errorMessage) {
        final Map errors = internalGetFieldErrors();
        List thisFieldErrors = (List) errors.get(fieldName);

        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList();
            errors.put(fieldName, thisFieldErrors);
        }

        thisFieldErrors.add(errorMessage);
    }

    public synchronized boolean hasActionErrors() {
        return (actionErrors != null) && !actionErrors.isEmpty();
    }

    /**
     * Note that this does not have the same meaning as in WW 1.x
     *
     * @return (hasActionErrors() || hasFieldErrors())
     */
    public synchronized boolean hasErrors() {
        return (hasActionErrors() || hasFieldErrors());
    }

    public synchronized boolean hasFieldErrors() {
        return (fieldErrors != null) && !fieldErrors.isEmpty();
    }

    private Collection internalGetActionErrors() {
        if (actionErrors == null) {
            actionErrors = new ArrayList();
        }

        return actionErrors;
    }

    private Collection internalGetActionMessages() {
        if (actionMessages == null) {
            actionMessages = new ArrayList();
        }

        return actionMessages;
    }

    private Map internalGetFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new HashMap();
        }

        return fieldErrors;
    }
}
