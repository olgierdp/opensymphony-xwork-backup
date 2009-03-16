/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Date;


/**
 * TestBean
 *
 * @author Jason Carreira
 *         Created Aug 4, 2003 12:39:53 AM
 */
public class TestBean {

    private Date birth;
    private String name;
    private int count;


    public TestBean() {
    }


    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getBirth() {
        return birth;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
