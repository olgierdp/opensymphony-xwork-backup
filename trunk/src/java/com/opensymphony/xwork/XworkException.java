/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * XworkException
 * @author Jason Carreira
 * Created Sep 7, 2003 12:15:03 AM
 */
public class XworkException extends RuntimeException {
    //~ Instance fields ////////////////////////////////////////////////////////

    Throwable throwable;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    * Constructs a <code>XworkException</code> with no detail  message.
    */
    public XworkException() {
    }

    /**
    * Constructs a <code>XworkException</code> with the specified
    * detail message.
    *
    * @param   s   the detail message.
    */
    public XworkException(String s) {
        super(s);
    }

    /**
    * Constructs a <code>XworkException</code> with no detail  message.
    */
    public XworkException(Throwable cause) {
        this.throwable = cause;
    }

    /**
    * Constructs a <code>XworkException</code> with the specified
    * detail message.
    *
    * @param   s   the detail message.
    */
    public XworkException(String s, Throwable cause) {
        super(s);
        this.throwable = cause;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Throwable getThrowable() {
        return throwable;
    }

    /**
    * Prints this <code>Throwable</code> and its backtrace to the
    * specified print stream.
    *
    * @param s <code>PrintStream</code> to use for output
    */
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);

        if (throwable != null) {
            s.println("with nested exception " + throwable);
            throwable.printStackTrace(s);
        }
    }

    /**
    * Prints this <code>Throwable</code> and its backtrace to the specified
    * print writer.
    *
    * @param s <code>PrintWriter</code> to use for output
    * @since   JDK1.1
    */
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);

        if (throwable != null) {
            s.println("with nested exception " + throwable);
            throwable.printStackTrace(s);
        }
    }

    /**
    * Returns a short description of this throwable object.
    * If this <code>Throwable</code> object was
    * {@link #Throwable(String) created} with an error message string,
    * then the result is the concatenation of three strings:
    * <ul>
    * <li>The name of the actual class of this object
    * <li>": " (a colon and a space)
    * <li>The result of the {@link #getMessage} method for this object
    * </ul>
    * If this <code>Throwable</code> object was {@link #Throwable() created}
    * with no error message string, then the name of the actual class of
    * this object is returned.
    *
    * @return  a string representation of this <code>Throwable</code>.
    */
    public String toString() {
        if (throwable == null) {
            return super.toString();
        }

        return super.toString() + "\n    with nested exception \n" + throwable.toString();
    }
}
