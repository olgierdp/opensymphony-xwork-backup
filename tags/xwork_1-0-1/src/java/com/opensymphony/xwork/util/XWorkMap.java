/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;

import java.util.HashMap;


/**
 * A simple map that when requested a key that it doesn't yet hold will
 * create an empty beans using ObjectFactory's
 * {@link ObjectFactory#buildBean(java.lang.Class) buildBean} method.
 *
 * @author Patrick Lightbody
 */
public class XWorkMap extends HashMap {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Class clazz;

    //~ Constructors ///////////////////////////////////////////////////////////

    public XWorkMap(Class clazz) {
        this.clazz = clazz;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object get(Object key) {
        Object o = super.get(key);

        if (o == null) {
            try {
                o = ObjectFactory.getObjectFactory().buildBean(clazz);
                this.put(key, o);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return o;
    }
}
