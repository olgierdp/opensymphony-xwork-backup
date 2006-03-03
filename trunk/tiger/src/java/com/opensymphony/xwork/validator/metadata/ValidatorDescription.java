/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator.metadata;

/**
 * <code>ValidatorDescription</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public interface ValidatorDescription {


    /**
     * Returns the validator XML definition.
     *
     * @return the validator XML definition.
     */
    String asXml();

    /**
     * Returns the field name to create the validation rule for.
     *
     * @return The field name to create the validation rule for
     */
    String getFieldName();

    /**
     * Sets the I18N message key.
     * @param key the I18N message key
     */
    void setKey(String key);

    /**
     * Sets the default validator failure message.
     *
     * @param message the default validator failure message
     */
    void setMessage(String message);

    /**
     * Set the shortCircuit flag.
     *
     * @param shortCircuit the shortCircuit flag.
     */
    void setShortCircuit(boolean shortCircuit);

    boolean isSimpleValidator();
}
