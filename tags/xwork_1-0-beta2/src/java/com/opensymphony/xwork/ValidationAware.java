/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Collection;
import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface ValidationAware {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Set the Collection of Action level String error messages
     * @param errorMessages
     */
    void setActionErrors(Collection errorMessages);

    /**
    * Get the Collection of Action level error messages for this action
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
    * Get the field specific errors associated with this action.
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
