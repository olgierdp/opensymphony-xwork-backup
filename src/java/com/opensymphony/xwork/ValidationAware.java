/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.io.Serializable;

import java.util.Collection;
import java.util.Map;


/**
 * ValidationAware classes can accept Action (class level) or field level error messages. Action level messages are kept
 * in a Collection. Field level error messages are kept in a Map from String field name to a List of field error msgs.
 *
 * @author $author$
 * @version $Revision$
 */
public interface ValidationAware extends Serializable {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Set the Collection of Action level String error messages
     * @param errorMessages
     */
    void setActionErrors(Collection errorMessages);

    /**
    * Get the Collection of Action level error messages for this action. Error messages should not be added directly
    * here, as implementations are free to return a new Collection or an Unmodifiable Collection.
    *
    * @return Collection of String error messages
    */
    Collection getActionErrors();

    /**
     *  Set the field error map of fieldname (String) to Collection of String error messages
     * @param errorMap
     */
    void setFieldErrors(Map errorMap);

    /**
    * Get the field specific errors associated with this action. Error messages should not be added directly
    * here, as implementations are free to return a new Collection or an Unmodifiable Collection.
    *
    * @return Map with errors mapped from fieldname (String) to Collection of String error messages
    */
    Map getFieldErrors();

    /**
    * Add an Action level error message to this Action
    *
    * @param   anErrorMessage
    */
    void addActionError(String anErrorMessage);

    /**
    * Add an error message for a given field
    *
    * @param   fieldName  name of field
    * @param   errorMessage  the error message
    */
    void addFieldError(String fieldName, String errorMessage);

    /**
    * Check whether there are any Action level error messages
    *
    * @return true if any Action level error messages have been registered
    */
    boolean hasActionErrors();

    /**
     * @return (hasActionErrors() || hasFieldErrors())
     */
    boolean hasErrors();

    /**
    * Check whether there are any field errors associated with this action.
    *
    * @return     whether there are any field errors
    */
    boolean hasFieldErrors();
}
