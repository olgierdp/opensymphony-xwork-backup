/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

import java.util.Collection;
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
public class BaseActionSupport implements Action, Serializable, ValidationAware, LocaleAware {
    //~ Static fields/initializers /////////////////////////////////////////////

    protected transient static final Log LOG = LogFactory.getLog(ActionSupport.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private final LocaleAware localeAware = new LocaleAwareSupport(getClass());
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

    public Map getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public Locale getLocale() {
        return localeAware.getLocale();
    }

    public String getText(String aTextName) {
        return localeAware.getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return localeAware.getText(aTextName, defaultValue);
    }

    public ResourceBundle getTexts(String aBundleName) {
        return localeAware.getTexts(aBundleName);
    }

    public ResourceBundle getTexts() {
        return localeAware.getTexts();
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
}
