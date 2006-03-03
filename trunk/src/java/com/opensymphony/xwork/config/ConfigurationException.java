/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.XworkException;


/**
 * ConfigurationException
 *
 * @author Jason Carreira
 *         Created Feb 24, 2003 8:15:08 AM
 */
public class ConfigurationException extends XworkException {

    /**
     * Constructs a <code>ConfigurationException</code> with no detail  message.
     */
    public ConfigurationException() {
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with no detail  message.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Throwable cause) {
        super(s, cause);
    }
}
