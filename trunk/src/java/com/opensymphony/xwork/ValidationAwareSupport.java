/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ValidationAwareSupport
 * @author Jason Carreira
 * Created Aug 2, 2003 11:56:27 PM
 */
public class ValidationAwareSupport implements ValidationAware {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Collection actionErrors;
    private Map fieldErrors;

    //~ Methods ////////////////////////////////////////////////////////////////

    public synchronized void setActionErrors(Collection errorMessages) {
        this.actionErrors = errorMessages;
    }

    public synchronized Collection getActionErrors() {
        return Collections.unmodifiableCollection(internalGetActionErrors());
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
        return Collections.unmodifiableMap(internalGetFieldErrors());
    }

    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
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

    private Map internalGetFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new HashMap();
        }

        return fieldErrors;
    }
}
