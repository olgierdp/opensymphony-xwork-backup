/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * Allows accumulation of multiple exceptions within a single job exception.
 * @author Tim Dawson
 */
public class DelegatingHandlerException extends Exception {
    //~ Instance fields ////////////////////////////////////////////////////////

    final ArrayList exceptions = new ArrayList();
    final String message;
    boolean error = false;
    boolean fatal = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    * constructor, string message is used for logging
    */
    public DelegatingHandlerException(String message) {
        this.message = message;
    }

    /**
    * empty constructor
    */
    public DelegatingHandlerException() {
        this.message = "";
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * returns true if addFatal() has been called
    */
    public boolean isFatal() {
        return fatal;
    }

    /**
    * adds an error (i.e. non-fatal) exception to the list
    */
    public void addError(Exception e) {
        exceptions.add(e);
        error = true;
    }

    /**
    * adds a fatal exception to the list; if a fatal exception is added, it is
    * always the last exception but there's not much point in enforcing that
    * as a restriction, we just mark a flag that a fatal exception was added
    */
    public void addFatal(Exception e) {
        exceptions.add(e);
        fatal = true;
    }

    /**
    * adds a warning exception to the list (doesn't mark the exception as an error)
    */
    public void addWarning(Exception e) {
        exceptions.add(e);
    }

    /**
    * provides access to the exceptions
    */
    public Iterator exceptions() {
        Iterator it = Collections.unmodifiableList(exceptions).iterator();

        return it;
    }

    /**
    * returns true if addError() or addFatal() has been called
    */
    public boolean hasErrors() {
        return error || fatal;
    }
}
