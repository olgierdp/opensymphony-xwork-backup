/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.Date;


/**
 *
 *
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class Foo {
    //~ Instance fields ////////////////////////////////////////////////////////

    Bar bar;
    Date birthday;
    String title;
    long[] points;
    boolean useful;
    int number;
    long aLong;
    Foo child;
    Foo[] relatives;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setPoints(long[] points) {
        this.points = points;
    }

    public long[] getPoints() {
        return points;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUseful(boolean useful) {
        this.useful = useful;
    }

    public boolean isUseful() {
        return useful;
    }

    public long getALong() {
        return aLong;
    }

    public void setALong(long aLong) {
        this.aLong = aLong;
    }

    public void setChild(Foo child) {
        this.child = child;
    }

    public Foo getChild() {
        return child;
    }

    public Foo[] getRelatives() {
        return relatives;
    }

    public void setRelatives(Foo[] relatives) {
        this.relatives = relatives;
    }
}
