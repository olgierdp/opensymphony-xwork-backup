/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;


/**
 * ConfigurationException
 * @author Jason Carreira
 * Created Feb 24, 2003 8:15:08 AM
 */
public class ConfigurationException extends Exception {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Throwable throwable;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructs an <code>Exception</code> with no specified detail message.
     */
    public ConfigurationException() {
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     *
     * @param   s   the detail message.
     */
    public ConfigurationException(String s) {
        super(s);
    }

    /**
     * Constructs an <code>Exception</code> with no specified detail message.
     */
    public ConfigurationException(Throwable throwable) {
        this.throwable = throwable;
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     *
     * @param   s   the detail message.
     */
    public ConfigurationException(String s, Throwable throwable) {
        super(s);
        this.throwable = throwable;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String toString() {
        if (throwable == null) {
            return super.toString();
        }

        return super.toString() + " with nested exception " + throwable;
    }
}
