/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;


/**
 * Test bean.
 *
 * @author Mark Woon
 */
public class User implements UserMarker {
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
