/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import com.opensymphony.xwork.util.LocalizedTextUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by IntelliJ IDEA.
 * User: Mike
 * Date: May 27, 2003
 * Time: 3:45:29 PM
 * To change this template use Options | File Templates.
 */
public class BaseActionSupport implements Action, ValidationAware, LocaleAware, Serializable {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected transient static final Log LOG = LogFactory.getLog(ActionSupport.class);

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

    /**
    * Get the locale for this action.
    *
    * Applications may customize how locale is chosen by
    * subclassing ActionSupport and override this methodName.
    *
    * @return     the locale to use
    */
    public Locale getLocale() {
        return ActionContext.getContext().getLocale();
    }

    /**
    * Get a text from the resource bundles associated with this action.
    * The resource bundles are searched, starting with the one associated
    * with this particular action, and testing all its superclasses' bundles.
    * It will stop once a bundle is found that contains the given text. This gives
    * a cascading style that allow global texts to be defined for an application base
    * class.
    *
    * @param   aTextName  name of text to be found
    * @return     value of named text
    */
    public String getText(String aTextName) {
        Class thisClass = getClass();

        return LocalizedTextUtil.findText(thisClass, aTextName);
    }

    /**
    * Get the named bundle.
    *
    * You can override the getLocale() methodName to change the behaviour of how
    * to choose locale for the bundles that are returned. Typically you would
    * use the LocaleAware interface to get the users configured locale, or use
    * your own methodName to allow the user to select the locale and store it in
    * the session (by using the SessionAware interface).
    *
    * @param   aBundleName  bundle name
    * @return     a resource bundle
    */
    public ResourceBundle getTexts(String aBundleName) {
        return LocalizedTextUtil.findResourceBundle(aBundleName, getLocale());
    }

    /**
    * Get the resource bundle associated with this action.
    * This will be based on the actual subclass that is used.
    *
    * @return     resouce bundle
    */
    public ResourceBundle getTexts() {
        return getTexts(getClass().getName());
    }

    public void addActionError(String anErrorMessage) {
        getActionErrors().add(anErrorMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        getFieldErrors().put(fieldName, errorMessage);
    }

    public String execute() throws Exception {
        return SUCCESS;
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
