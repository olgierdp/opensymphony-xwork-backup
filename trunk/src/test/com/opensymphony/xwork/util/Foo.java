/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.Date;
import java.util.List;
import java.util.Map;


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
    Foo child;
    String title;
    long[] points;
    Foo[] relatives;
    boolean useful;
    int number;
    long aLong;
    List cats;
    Map catMap;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setALong(long aLong) {
        this.aLong = aLong;
    }

    public long getALong() {
        return aLong;
    }

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

    public void setChild(Foo child) {
        this.child = child;
    }

    public Foo getChild() {
        return child;
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

    public void setRelatives(Foo[] relatives) {
        this.relatives = relatives;
    }

    public Foo[] getRelatives() {
        return relatives;
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

    public List getCats() {
        return cats;
    }

    public void setCats(List cats) {
        this.cats = cats;
    }

    public Map getCatMap() {
        return catMap;
    }

    public void setCatMap(Map catMap) {
        this.catMap = catMap;
    }
}
