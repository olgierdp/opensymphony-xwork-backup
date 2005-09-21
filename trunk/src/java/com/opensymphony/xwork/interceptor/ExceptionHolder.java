package com.opensymphony.xwork.interceptor;

/**
 * User: Matthew E. Porter (matthew dot porter at metissian dot com)
 * Date: Sep 21, 2005
 * Time: 3:09:12 PM
 */
public class ExceptionHolder {
    private Exception exception;

    public ExceptionHolder(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return this.exception;
    }
    
}
