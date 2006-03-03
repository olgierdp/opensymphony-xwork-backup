/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.XworkException;


/**
 * @author Mike
 */
public class ReferenceResolverException extends XworkException {

    /**
     *
     */
    public ReferenceResolverException() {
        super();
    }

    /**
     * @param s
     */
    public ReferenceResolverException(String s) {
        super(s);
    }

    /**
     * @param s
     * @param cause
     */
    public ReferenceResolverException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * @param cause
     */
    public ReferenceResolverException(Throwable cause) {
        super(cause);
    }
}
