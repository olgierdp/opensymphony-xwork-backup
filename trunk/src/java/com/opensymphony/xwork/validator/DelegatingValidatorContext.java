/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.LocaleAware;
import com.opensymphony.xwork.LocaleAwareSupport;
import com.opensymphony.xwork.ValidationAware;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * DelegatingValidatorContext
 * @author Jason Carreira
 * Created Aug 3, 2003 12:33:30 AM
 */
public class DelegatingValidatorContext implements ValidatorContext {
    //~ Instance fields ////////////////////////////////////////////////////////

    private LocaleAware localeAware;
    private ValidationAware validationAware;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DelegatingValidatorContext(ValidationAware validationAware, LocaleAware localeAware) {
        this.localeAware = localeAware;
        this.validationAware = validationAware;
    }

    public DelegatingValidatorContext(Object object) {
        this(makeValidationAware(object), makeLocaleAware(object));
    }

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

    public String getText(String aTextName, List args) {
        return localeAware.getText(aTextName, args);
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

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    protected static LocaleAware makeLocaleAware(Object object) {
        if (object instanceof LocaleAware) {
            return (LocaleAware) object;
        } else {
            return new LocaleAwareSupport(object.getClass());
        }
    }

    protected static ValidationAware makeValidationAware(Object object) {
        if (object instanceof ValidationAware) {
            return (ValidationAware) object;
        } else {
            return new LoggingValidationAware(object);
        }
    }

    protected void setLocaleAware(LocaleAware localeAware) {
        this.localeAware = localeAware;
    }

    protected LocaleAware getLocaleAware() {
        return localeAware;
    }

    protected void setValidationAware(ValidationAware validationAware) {
        this.validationAware = validationAware;
    }

    protected ValidationAware getValidationAware() {
        return validationAware;
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private static class LoggingValidationAware implements ValidationAware {
        private Log log;

        public LoggingValidationAware(Object obj) {
            log = LogFactory.getLog(obj.getClass());
        }

        public void setActionErrors(Collection errorMessages) {
            for (Iterator iterator = errorMessages.iterator();
                    iterator.hasNext();) {
                String s = (String) iterator.next();
                addActionError(s);
            }
        }

        public Collection getActionErrors() {
            return null;
        }

        public void setFieldErrors(Map errorMap) {
            for (Iterator iterator = errorMap.entrySet().iterator();
                    iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                addFieldError((String) entry.getKey(), (String) entry.getValue());
            }
        }

        public Map getFieldErrors() {
            return null;
        }

        public void addActionError(String anErrorMessage) {
            log.error("Validation error: " + anErrorMessage);
        }

        public void addFieldError(String fieldName, String errorMessage) {
            log.error("Validation error for " + fieldName + ":" + errorMessage);
        }

        public boolean hasActionErrors() {
            return false;
        }

        public boolean hasErrors() {
            return false;
        }

        public boolean hasFieldErrors() {
            return false;
        }
    }
}
