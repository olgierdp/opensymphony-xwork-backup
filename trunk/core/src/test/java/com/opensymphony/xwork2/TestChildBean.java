/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * TestBean
 */
public class TestChildBean {

    private Date birth;
    private String name;
    private int count;


    public TestChildBean() {
        Calendar cal = new GregorianCalendar(1900, 01, 01);
        setBirth(cal.getTime());
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
