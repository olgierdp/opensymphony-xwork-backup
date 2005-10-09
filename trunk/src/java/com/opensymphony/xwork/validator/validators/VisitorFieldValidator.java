/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.validators;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.validator.ActionValidatorManager;
import com.opensymphony.xwork.validator.DelegatingValidatorContext;
import com.opensymphony.xwork.validator.ValidationException;
import com.opensymphony.xwork.validator.ValidatorContext;

import java.util.Collection;


/**
 * The VisitorFieldValidator allows you to forward validation to object
 * properties of your action using the object's own validation files.  This
 * allows you to use the ModelDriven development pattern and manage your
 * validations for your models in one place, where they belong, next to your
 * model classes.  The VisitorFieldValidator can handle either simple Object
 * properties, Collections of Objects, or Arrays.
 *
 * @author Jason Carreira
 *         Created Aug 2, 2003 10:27:48 PM
 */
public class VisitorFieldValidator extends FieldValidatorSupport {

    private String context;
    private boolean appendPrefix = true;


    /**
     * Sets whether the field name of this field validator should be prepended to the field name of
     * the visited field to determine the full field name when an error occurs.  The default is
     * true.
     */
    public void setAppendPrefix(boolean appendPrefix) {
        this.appendPrefix = appendPrefix;
    }

    /**
     * Flags whether the field name of this field validator should be prepended to the field name of
     * the visited field to determine the full field name when an error occurs.  The default is
     * true.
     */
    public boolean isAppendPrefix() {
        return appendPrefix;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(object);

        String visitorContext = (context == null) ? ActionContext.getContext().getName() : context;

        if (value == null) {
            return;
        }

        if (value instanceof Collection) {
            Collection coll = (Collection) value;
            Object[] array = coll.toArray();

            validateArrayElements(array, fieldName, visitorContext);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;

            validateArrayElements(array, fieldName, visitorContext);
        } else {
            validateObject(fieldName, value, visitorContext);
        }

        stack.pop();
    }

    private void validateArrayElements(Object[] array, String fieldName, String visitorContext) throws ValidationException {
        for (int i = 0; i < array.length; i++) {
            Object o = array[i];
            validateObject(fieldName + "[" + i + "]", o, visitorContext);
        }
    }

    private void validateObject(String fieldName, Object o, String visitorContext) throws ValidationException {
        OgnlValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(o);

        ValidatorContext validatorContext;

        if (appendPrefix) {
            validatorContext = new AppendingValidatorContext(getValidatorContext(), o, fieldName, getMessage(o));
        } else {
            ValidatorContext parent = getValidatorContext();
            validatorContext = new DelegatingValidatorContext(parent, DelegatingValidatorContext.makeTextProvider(o, parent), parent);
        }

        ActionValidatorManager.validate(o, visitorContext, validatorContext);
        stack.pop();
    }


    private class AppendingValidatorContext extends DelegatingValidatorContext {
        Object o;
        String field;
        String message;

        public AppendingValidatorContext(ValidatorContext parent, Object object, String field, String message) {
            super(parent, makeTextProvider(object, parent), parent);

            //            super(parent);
            this.field = field;
            this.message = message;
        }

        /**
         * Translates a simple field name into a full field name in Ognl syntax
         *
         * @param fieldName
         * @return
         */
        public String getFullFieldName(String fieldName) {
            return field + "." + fieldName;
        }

        public void addActionError(String anErrorMessage) {
            super.addFieldError(field, message + anErrorMessage);
        }

        public void addFieldError(String fieldName, String errorMessage) {
            super.addFieldError(getFullFieldName(fieldName), message + errorMessage);
        }
    }
}
