/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A simple wrapper around an exception, providing an easy way to print out the stack trace of the exception as well as
 * a way to get a handle on the exception itself.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Matthew E. Porter (matthew dot porter at metissian dot com)
 */
public class ExceptionHolder {

    private Exception exception;

    /**
     * Holds the given exception
     *
     * @param exception  the exception to hold.
     */
    public ExceptionHolder(Exception exception) {
        this.exception = exception;
    }

    /**
     * Gets the holded exception
     *
     * @return  the holded exception
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * Gets the holded exception stacktrace using {@link Exception#printStackTrace()}.
     *
     * @return  stacktrace
     */
    public String getExceptionStack() {
        String exceptionStack = null;

        if (getException() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            try {
                getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            }
            finally {
                try {
                    sw.close();
                    pw.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return exceptionStack;
    }
    
}
