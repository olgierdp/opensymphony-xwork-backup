/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Collection;
import java.util.Map;

/**
 * ValidationAware classes can accept Action (class level) or field level error messages. Action level messages are kept
 * in a Collection. Field level error messages are kept in a Map from String field name to a List of field error msgs.
 *
 * @author plightbo 
 */
public interface ValidationAware {

    /**
     * Set the Collection of Action-level String error messages.
     *
     * @param errorMessages Collection of String error messages
     */
    void setActionErrors(Collection errorMessages);

    /**
     * Get the Collection of Action-level error messages for this action. Error messages should not
     * be added directly here, as implementations are free to return a new Collection or an
     * Unmodifiable Collection.
     *
     * @return Collection of String error messages
     */
    Collection getActionErrors();

    /**
     * Set the Collection of Action-level String messages (not errors).
     *
     * @param messages Collection of String messages (not errors).
     */
    void setActionMessages(Collection messages);

    /**
     * Get the Collection of Action-level messages for this action. Messages should not be added
     * directly here, as implementations are free to return a new Collection or an Unmodifiable
     * Collection.
     *
     * @return Collection of String messages
     */
    Collection getActionMessages();

    /**
     * Set the field error map of fieldname (String) to Collection of String error messages.
     *
     * @param errorMap field error map
     */
    void setFieldErrors(Map errorMap);

    /**
     * Get the field specific errors associated with this action. Error messages should not be added
     * directly here, as implementations are free to return a new Collection or an Unmodifiable
     * Collection.
     *
     * @return Map with errors mapped from fieldname (String) to Collection of String error messages
     */
    Map getFieldErrors();

    /**
     * Add an Action-level error message to this Action.
     *
     * @param anErrorMessage  the error message
     */
    void addActionError(String anErrorMessage);

    /**
     * Add an Action-level message to this Action.
     *
     * @param aMessage  the message
     */
    void addActionMessage(String aMessage);

    /**
     * Add an error message for a given field.
     *
     * @param fieldName    name of field
     * @param errorMessage the error message
     */
    void addFieldError(String fieldName, String errorMessage);

    /**
     * Check whether there are any Action-level error messages.
     *
     * @return true if any Action-level error messages have been registered
     */
    boolean hasActionErrors();

    /**
     * Checks whether there are any Action-level messages.
     *
     * @return true if any Action-level messages have been registered
     */
    boolean hasActionMessages();

    /**
     * Checks whether there are any action errors or field errors.
     * <p/>
     * <b>Note</b>: that this does not have the same meaning as in WW 1.x.
     *
     * @return <code>(hasActionErrors() || hasFieldErrors())</code>
     */
    boolean hasErrors();

    /**
     * Check whether there are any field errors associated with this action.
     *
     * @return whether there are any field errors
     */
    boolean hasFieldErrors();

}
