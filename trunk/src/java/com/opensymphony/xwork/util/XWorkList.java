/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;

import java.util.AbstractList;
import java.util.ArrayList;


/**
 * A simple list that when requested an index that it doesn't yet hold will
 * create empty beans all the way up to and including that index using
 * ObjectFactory's {@link ObjectFactory#buildBean(java.lang.Class) buildBean} method.
 *
 * @author Patrick Lightbody
 */
public class XWorkList extends ArrayList {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Class clazz;

    //~ Constructors ///////////////////////////////////////////////////////////

    public XWorkList(Class clazz) {
        this.clazz = clazz;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public synchronized Object get(int index) {
        while (index >= this.size()) {
            try {
                this.add(ObjectFactory.getObjectFactory().buildBean(clazz));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return super.get(index);
    }
}
