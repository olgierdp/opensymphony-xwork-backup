/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.HashMap;


/**
 * User: plightbo
 * Date: Jan 13, 2004
 * Time: 9:21:03 PM
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
                o = clazz.newInstance();
                this.put(key, o);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        return o;
    }
}
