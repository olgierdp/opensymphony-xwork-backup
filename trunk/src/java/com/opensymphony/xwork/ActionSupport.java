/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

import java.util.*;


/**
 * User: Mike
 * Date: May 27, 2003
 * Time: 3:45:29 PM
 */
public class ActionSupport implements Action, Serializable, Validateable, ValidationAware, TextProvider, LocaleProvider {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected transient static final Log LOG = LogFactory.getLog(ActionSupport.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private final TextProvider textProvider = new TextProviderSupport(getClass(), this);
    private final ValidationAware validationAware = new ValidationAwareSupport();

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setActionErrors(Collection errorMessages) {
        validationAware.setActionErrors(errorMessages);
    }

    public Collection getActionErrors() {
        return validationAware.getActionErrors();
    }

    public void setFieldErrors(Map errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    /**
     * @return a Map of field names -> List of errors or null if no errors have been added
     */
    public Map getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public Locale getLocale() {
        return (ActionContext.getContext() != null) ?
                ActionContext.getContext().getLocale() :
                null;
    }


    public String getText(String aTextName) {
        return textProvider.getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, List args) {
        return textProvider.getText(aTextName, args);
    }

    public String getText(String aTextName, String defaultValue, List args) {
        return textProvider.getText(aTextName, defaultValue, args);
    }

    public ResourceBundle getTexts(String aBundleName) {
        return textProvider.getTexts(aBundleName);
    }

    public ResourceBundle getTexts() {
        return textProvider.getTexts();
    }

    public void addActionError(String anErrorMessage) {
        validationAware.addActionError(anErrorMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        validationAware.addFieldError(fieldName, errorMessage);
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    /**
     * Subclasses should override this method to provide validations.
     */
    public void validate() {
    }
}
