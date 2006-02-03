/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.io.Serializable;
import java.util.*;

/**
 * Provides a default implementation of ValidationAware. Returns new collections for
 * errors and messages (defensive copy).
 *
 * @author Jason Carreira
 */
public class ValidationAwareSupport implements ValidationAware, Serializable {

    private Collection actionErrors;
    private Collection actionMessages;
    private Map fieldErrors;


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

    public synchronized Map getFieldErrors() {
        return new HashMap(internalGetFieldErrors());
    }

    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
    }

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

    public boolean hasActionMessages() {
        return (actionMessages != null) && !actionMessages.isEmpty();
    }

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