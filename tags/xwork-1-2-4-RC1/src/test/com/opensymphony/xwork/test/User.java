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

    private Collection collection;
    private List list;
    private Map map;
    private String email;
    private String email2;
    private String name;


    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail2(String email) {
        email2 = email;
    }

    public String getEmail2() {
        return email2;
    }

    public void setList(List l) {
        list = l;
    }

    public List getList() {
        return list;
    }

    public void setMap(Map m) {
        map = m;
    }

    public Map getMap() {
        return map;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
