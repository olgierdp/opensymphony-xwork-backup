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
    //~ Instance fields ////////////////////////////////////////////////////////

    private Bar m_bar;
    private String m_data;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setBarObj(Bar b) {
        m_bar = b;
    }

    public Bar getBarObj() {
        return m_bar;
    }

    public void setData(String data) {
        m_data = data;
    }

    public String getData() {
        return m_data;
    }
}
