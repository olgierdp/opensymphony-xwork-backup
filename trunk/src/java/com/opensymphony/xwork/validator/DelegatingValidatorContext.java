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

    public DelegatingValidatorContext(Object object) {
        if (object instanceof ValidationAware) {
            validationAware = (ValidationAware) object;
        } else {
            validationAware = new LoggingValidationAware(object);
        }

        if (object instanceof LocaleAware) {
            localeAware = (LocaleAware) object;
        } else {
            localeAware = new LocaleAwareSupport(object.getClass());
        }
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

    //~ Inner Classes //////////////////////////////////////////////////////////

    private class LoggingValidationAware implements ValidationAware {
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
