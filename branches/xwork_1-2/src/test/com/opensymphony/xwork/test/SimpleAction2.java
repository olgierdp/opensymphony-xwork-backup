/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.SimpleAction;


/**
 * SimpleAction2
 *
 * @author Jason Carreira
 *         Created Jun 14, 2003 9:51:12 PM
 */
public class SimpleAction2 extends SimpleAction {

    private int count;


    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
