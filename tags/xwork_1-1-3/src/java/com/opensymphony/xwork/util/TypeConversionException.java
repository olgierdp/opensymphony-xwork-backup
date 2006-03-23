/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.XworkException;


/**
 * TypeConversionException should be thrown by any TypeConverters which fail to convert values
 *
 * @author Jason Carreira
 *         Created Oct 3, 2003 12:18:33 AM
 */
public class TypeConversionException extends XworkException {

    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public TypeConversionException() {
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public TypeConversionException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public TypeConversionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public TypeConversionException(String s, Throwable cause) {
        super(s, cause);
    }
}
