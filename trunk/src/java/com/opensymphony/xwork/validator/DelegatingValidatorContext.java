/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.*;

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

    private LocaleProvider localeProvider;
    private TextProvider textProvider;
    private ValidationAware validationAware;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DelegatingValidatorContext(ValidationAware validationAware, TextProvider textProvider, LocaleProvider localeProvider) {
        this.textProvider = textProvider;
        this.validationAware = validationAware;
        this.localeProvider = localeProvider;
    }

    public DelegatingValidatorContext(Object object) {
        this.localeProvider = makeLocaleProvider(object);
        this.validationAware = makeValidationAware(object);
        this.textProvider = makeTextProvider(object, localeProvider);
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

    /**
 * Translates a simple field name into a full field name in Ognl syntax
 * @param fieldName
 * @return
 */
    public String getFullFieldName(String fieldName) {
        return fieldName;
    }

    public Locale getLocale() {
        return localeProvider.getLocale();
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

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    protected static LocaleProvider makeLocaleProvider(Object object) {
        if (object instanceof LocaleProvider) {
            return (LocaleProvider) object;
        } else {
            return new ActionContextLocaleProvider();
        }
    }

    public static TextProvider makeTextProvider(Object object, LocaleProvider localeProvider) {
        if (object instanceof TextProvider) {
            return (TextProvider) object;
        } else {
            return new TextProviderSupport(object.getClass(), localeProvider);
        }
    }

    protected static ValidationAware makeValidationAware(Object object) {
        if (object instanceof ValidationAware) {
            return (ValidationAware) object;
        } else {
            return new LoggingValidationAware(object);
        }
    }

    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    protected TextProvider getTextProvider() {
        return textProvider;
    }

    protected void setValidationAware(ValidationAware validationAware) {
        this.validationAware = validationAware;
    }

    protected ValidationAware getValidationAware() {
        return validationAware;
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private static class ActionContextLocaleProvider implements LocaleProvider {
        public Locale getLocale() {
            return ActionContext.getContext().getLocale();
        }
    }

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
