/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.SimpleAction;
import com.opensymphony.xwork.util.Bar;


/**
 * Extend SimpleAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public class SimpleAction3 extends SimpleAction implements DataAware {

    private Bar bar;
    private String data;


    public void setBarObj(Bar b) {
        bar = b;
    }

    public Bar getBarObj() {
        return bar;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
