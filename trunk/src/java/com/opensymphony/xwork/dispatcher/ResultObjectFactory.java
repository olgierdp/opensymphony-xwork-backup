/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;


/**
 * This interface provides access to result objects for the
 * {@link com.opensymphony.xwork.result.ObjectResult ObjectResult} result to use.
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public interface ResultObjectFactory {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get the result object corresponding to the given key
     * @param key The key of the object to get
     * @return Either the result object corresponding to the given key or <code>null</code> if no
     * result object exists for that key
     */
    Object getResultObject(String key);
}
