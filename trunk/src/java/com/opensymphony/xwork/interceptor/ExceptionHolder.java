package com.opensymphony.xwork.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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

    public String getExceptionStack() throws IOException {
        String exceptionStack = null;

        if (getException() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            try {
                getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            }
            finally {
                sw.close();
                pw.close();
            }
        }

        return exceptionStack;
    }

}
