/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;

import java.util.AbstractList;
import java.util.ArrayList;


/**
 * User: plightbo
 * Date: Jan 13, 2004
 * Time: 7:02:33 PM
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
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }

        return super.get(index);
    }
}
