/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.opensymphony.xwork.util.location.Locatable;
import com.opensymphony.xwork.util.location.Location;
import com.opensymphony.xwork.util.location.LocationUtils;


/**
 * XworkException
 *
 * @author Jason Carreira
 *         Created Sep 7, 2003 12:15:03 AM
 */
public class XworkException extends RuntimeException implements Locatable {

    private Location location;


    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public XworkException() {
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public XworkException(String s) {
        this(s, null, null);
    }
    
    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message and location.
     *
     * @param s the detail message.
     */
    public XworkException(String s, Object target) {
        this(s, (Throwable) null, target);
    }

    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public XworkException(Throwable cause) {
        this(null, cause, null);
    }
    
    /**
     * Constructs a <code>XworkException</code> with no detail  message.
     */
    public XworkException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public XworkException(String s, Throwable cause) {
        this(s, cause, null);
    }
    
    
     /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public XworkException(String s, Throwable cause, Object target) {
        super(s, cause);
        
        this.location = LocationUtils.getLocation(target);
        if (this.location == Location.UNKNOWN) {
            this.location = LocationUtils.getLocation(cause);
        }
    }


    public Throwable getThrowable() {
        return getCause();
    }
    
    public Location getLocation() {
        return this.location;
    }

    /**
     * Returns a short description of this throwable object.
     * If this <code>Throwable</code> object was
     * {@link #XworkException(String) created} with an error message string,
     * then the result is the concatenation of three strings:
     * <ul>
     * <li>The name of the actual class of this object
     * <li>": " (a colon and a space)
     * <li>The result of the {@link #getMessage} method for this object
     * </ul>
     * If this <code>Throwable</code> object was {@link #XworkException() created}
     * with no error message string, then the name of the actual class of
     * this object is returned.
     *
     * @return a string representation of this <code>Throwable</code>.
     */
    public String toString() {
        String msg = getMessage();
        if (msg == null && getCause() != null) {
            msg = getCause().getMessage();
        }
        return msg + " - " + location.toString();
    }
}
