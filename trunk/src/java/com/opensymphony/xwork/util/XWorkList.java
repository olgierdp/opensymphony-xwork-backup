/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;

import java.util.ArrayList;


/**
 * A simple list that guarantees that {@link #get(int)} and {@link #set(int, Object)} will always
 * work regardless of the current size of the list.  Empty beans will be created to fill the gap
 * between the current list size and the requested index using ObjectFactory's
 * {@link ObjectFactory#buildBean(java.lang.Class) buildBean} method.
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

    /**
     * Returns the element at the specified position in this list.  An object is guaranteed to be
     * returned since it will create empty beans to fill the gap between the current list size and
     * the requested index.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     */
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

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * This method is guaranteed to work since it will create empty beans to fill the gap between
     * the current list size and the requested index to enable the element to be set.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     */
    public Object set(int index, Object element) {
        if (index >= this.size()) {
            get(index);
        }

        return super.set(index, element);
    }
}
