/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Dummy validator context to use to capture error messages.
 *
 * @author Mark Woon
 * @author Matthew Payne
 */
public class GenericValidatorContext extends DelegatingValidatorContext {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Collection m_actionErrors;
    private Collection m_actionMessages;
    private Map m_fieldErrors;

    //~ Constructors ///////////////////////////////////////////////////////////

    public GenericValidatorContext(Object object) {
        super(object);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public synchronized void setActionErrors(Collection errorMessages) {
        this.m_actionErrors = errorMessages;
    }

    public synchronized Collection getActionErrors() {
        return new ArrayList(internalGetActionErrors());
    }

    public synchronized void setActionMessages(Collection messages) {
        this.m_actionMessages = messages;
    }

    public synchronized Collection getActionMessages() {
        return new ArrayList(internalGetActionMessages());
    }

    public synchronized void setFieldErrors(Map errorMap) {
        this.m_fieldErrors = errorMap;
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
        return (m_actionErrors != null) && !m_actionErrors.isEmpty();
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
        return (m_fieldErrors != null) && !m_fieldErrors.isEmpty();
    }

    private Collection internalGetActionErrors() {
        if (m_actionErrors == null) {
            m_actionErrors = new ArrayList();
        }

        return m_actionErrors;
    }

    private Collection internalGetActionMessages() {
        if (m_actionMessages == null) {
            m_actionMessages = new ArrayList();
        }

        return m_actionMessages;
    }

    private Map internalGetFieldErrors() {
        if (m_fieldErrors == null) {
            m_fieldErrors = new HashMap();
        }

        return m_fieldErrors;
    }
}
