/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *
 * @author $Author$
 * @version $Revision$
 */
public class CompoundRoot extends ArrayList {
    //~ Constructors ///////////////////////////////////////////////////////////

    public CompoundRoot() {
    }

    public CompoundRoot(List list) {
        super(list);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public CompoundRoot cutStack(int index) {
        return new CompoundRoot(subList(index, size()));
    }

    public Object peek() {
        return get(0);
    }

    public Object pop() {
        return remove(0);
    }

    public void push(Object o) {
        add(0, o);
    }
}
