/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Test bean.
 *
 * @author Mark Woon
 */
public class User implements UserMarker {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Collection m_collection;
    private List m_list;
    private Map m_map;
    private String m_email;
    private String m_email2;
    private String m_name;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setCollection(Collection collection) {
        m_collection = collection;
    }

    public Collection getCollection() {
        return m_collection;
    }

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

    public void setList(List l) {
        m_list = l;
    }

    public List getList() {
        return m_list;
    }

    public void setMap(Map m) {
        m_map = m;
    }

    public Map getMap() {
        return m_map;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }
}
