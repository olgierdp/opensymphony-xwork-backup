/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.ArrayList;
import java.util.Collection;
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

    public void setActionErrors(Collection errorMessages) {
        this.actionErrors = errorMessages;
    }

    public Collection getActionErrors() {
        if (actionErrors == null) {
            actionErrors = new ArrayList();
        }

        return actionErrors;
    }

    public void setFieldErrors(Map errorMap) {
        this.fieldErrors = errorMap;
    }

    public Map getFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new HashMap();
        }

        return fieldErrors;
    }

    public void addActionError(String anErrorMessage) {
        getActionErrors().add(anErrorMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        final Map errors = getFieldErrors();
        List thisFieldErrors = (List) errors.get(fieldName);

        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList();
            errors.put(fieldName, thisFieldErrors);
        }

        thisFieldErrors.add(errorMessage);
    }

    public boolean hasActionErrors() {
        return (actionErrors != null) && !actionErrors.isEmpty();
    }

    /**
    * Note that this does not have the same meaning as in WW 1.x
    * @return (hasActionErrors() || hasFieldErrors())
    */
    public boolean hasErrors() {
        return (hasActionErrors() || hasFieldErrors());
    }

    public boolean hasFieldErrors() {
        return (fieldErrors != null) && !fieldErrors.isEmpty();
    }
}
