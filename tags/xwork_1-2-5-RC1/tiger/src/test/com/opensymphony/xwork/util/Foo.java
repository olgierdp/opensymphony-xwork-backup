/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author Rainer Hermanns
 * @version $Revision$
 */
public class Foo {

    Bar bar;
    Date birthday;
    Foo child;
    List cats;
    List moreCats;
    List strings;
    Set barSet;
    Map catMap;
    Map anotherCatMap;
    String title;
    long[] points;
    Foo[] relatives;
    boolean useful;
    int number;
    long aLong;


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

    public void setCatMap(Map catMap) {
        this.catMap = catMap;
    }

    public Map getCatMap() {
        return catMap;
    }

    public void setCats(List cats) {
        this.cats = cats;
    }

    public List getCats() {
        return cats;
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

    /**
     * @return Returns the anotherCatMap.
     */
    public Map getAnotherCatMap() {
        return anotherCatMap;
    }

    /**
     * @param anotherCatMap The anotherCatMap to set.
     */
    public void setAnotherCatMap(Map anotherCatMap) {
        this.anotherCatMap = anotherCatMap;
    }

    /**
     * @return Returns the moreCats.
     */
    public List getMoreCats() {
        return moreCats;
    }

    /**
     * @param moreCats The moreCats to set.
     */
    public void setMoreCats(List moreCats) {
        this.moreCats = moreCats;
    }

    /**
     * @return Returns the catSet.
     */
    public Set getBarSet() {
        return barSet;
    }

    /**
     * @param catSet The catSet to set.
     */
    public void setBarSet(Set catSet) {
        this.barSet = catSet;
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

    public void setStrings(List strings) {
        this.strings = strings;
    }

    public List getStrings() {
        return strings;
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
}
