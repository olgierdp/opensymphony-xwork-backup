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


/**
 * Test bean.
 *
 * @author Mark Woon
 */
public class User {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String m_email;
    private String m_email2;
    private String m_name;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setEmail(String email) {
        m_email = email;
    }

    public String getEmail() {
        return m_email;
    }

    public void setEmail2(String email) {
        m_email2 = email;
    }

    public String getEmail2() {
        return m_email2;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }
}
