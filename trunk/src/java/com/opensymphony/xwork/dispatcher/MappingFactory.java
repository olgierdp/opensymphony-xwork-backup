/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.dispatcher;


/**
 * This interface provides a way of accessing the POJO to action mapping configurations
 * @author <a href="mailto:yellek@dev.java.net">Peter Kelley</a>
 */
public interface MappingFactory {
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Get a mapping set with the given name.
     * @param name The name of the mapping set to get
     * @return Either the mapping set with the given name or <code>null</code> if no mapping set
     * exists with that name
     */
    MappingSet getMappingSet(String name);
}
