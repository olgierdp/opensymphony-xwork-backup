/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork.config;

import com.opensymphony.xwork.XworkException;


/**
 * @author Mike
 *         <p/>
 *         To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ReferenceResolverException extends XworkException {
    //~ Constructors ///////////////////////////////////////////////////////////

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
