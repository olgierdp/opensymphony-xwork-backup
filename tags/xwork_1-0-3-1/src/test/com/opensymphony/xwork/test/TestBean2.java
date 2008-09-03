/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.TestBean;
import com.opensymphony.xwork.util.Bar;


/**
 * Extend TestBean to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public class TestBean2 extends TestBean implements DataAware {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Bar bar;
    private String data;

    //~ Methods ////////////////////////////////////////////////////////////////

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