/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * This file is copyright (c) 2001-2004, Board of Trustees of Stanford
 * University.  Created in the laboratory of Professor Russ B. Altman
 * (russ.altman@stanford.edu), Stanford University, Department of
 * Medicine, as part of the NIH PharmGKB knowledge base development
 * effort.  This work is supported by NIH U01GM61374.  Contact
 * help@pharmgkb.org for assistance, questions or suggestions.
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
