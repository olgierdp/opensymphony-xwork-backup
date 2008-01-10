/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.List;
import java.util.ArrayList;

import java.net.URL;

import com.opensymphony.xwork.util.location.Locatable;
import com.opensymphony.xwork.util.location.Location;
import com.opensymphony.xwork.util.location.LocationUtils;


/**
 * A generic runtime exception that optionally contains Location information 
 *
 * @author Jason Carreira
 */
public class XworkException extends RuntimeException implements Locatable {

    private Location location;


    /**
     * Constructs a <code>XworkException</code> with no detail message.
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
     * detail message and target.
     *
     * @param s the detail message.
     * @param target the target of the exception.
     */
    public XworkException(String s, Object target) {
        this(s, (Throwable) null, target);
    }

    /**
     * Constructs a <code>XworkException</code> with the root cause
     *
     * @param cause The wrapped exception
     */
    public XworkException(Throwable cause) {
        this(null, cause, null);
    }
    
    /**
     * Constructs a <code>XworkException</code> with the root cause and target
     *
     * @param cause The wrapped exception
     * @param target The target of the exception
     */
    public XworkException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message and exception cause.
     *
     * @param s the detail message.
     * @param cause the wrapped exception
     */
    public XworkException(String s, Throwable cause) {
        this(s, cause, null);
    }
    
    
     /**
     * Constructs a <code>XworkException</code> with the specified
     * detail message, cause, and target
     *
     * @param s the detail message.
     * @param cause The wrapped exception
     * @param target The target of the exception
     */
    public XworkException(String s, Throwable cause, Object target) {
        super(s, cause);
        
        this.location = LocationUtils.getLocation(target);
        if (this.location == Location.UNKNOWN) {
            this.location = LocationUtils.getLocation(cause);
        }
    }


    /**
     * Gets the underlying cause
     * 
     * @deprecated Use getCause()
     */
    public Throwable getThrowable() {
        return getCause();
    }


    /**
     * Gets the location of the error, if available
     */
    public Location getLocation() {
        return this.location;
    }
    
    
    /**
     * Gets a source code snippet with the default padding
     */
    public List getSnippet() {
        return getSnippet(2);
    }
    
    
    /**
     * Gets a source code snippet with the default padding
     *
     * @param padding The amount of lines before and after the error to include
     */
    public List getSnippet(int padding) {
        List snippet = new ArrayList();
        if (location != null && location.getLineNumber() > 0) {
            try {
                InputStream in = new URL(location.getURI()).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                
                int lineno = 0;
                int errno = location.getLineNumber();
                String line;
                while ((line = reader.readLine()) != null) {
                    lineno++;
                    if (lineno >= errno - padding && lineno <= errno + padding) {
                        snippet.add(line);
                    }
                }
            } catch (Exception ex) {
                // ignoring as snippet not available isn't a big deal
            }
        }
        return snippet;
    }
            

    /**
     * Returns a short description of this throwable object, including the 
     * location. If no detailed message is available, it will use the message
     * of the underlying exception if available.
     *
     * @return a string representation of this <code>Throwable</code>.
     */
    public String toString() {
        String msg = getMessage();
        if (msg == null && getCause() != null) {
            msg = getCause().getMessage();
        }

        if (location != null) {
            if (msg != null) {
                return msg + " - " + location.toString();
            } else {
                return location.toString();
            }
        } else {
            return msg;
        }
    }
}