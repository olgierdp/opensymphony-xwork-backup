/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.util;


/**
 *
 *
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class Dog {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final String SCIENTIFIC_NAME = "Canine";

    //~ Instance fields ////////////////////////////////////////////////////////

    Cat hates;
    String name;
    int[] childAges;
    int age;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setChildAges(int[] childAges) {
        this.childAges = childAges;
    }

    public int[] getChildAges() {
        return childAges;
    }

    public void setHates(Cat hates) {
        this.hates = hates;
    }

    public Cat getHates() {
        return hates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int computeDogYears() {
        return age * 7;
    }

    public int multiplyAge(int by) {
        return age * by;
    }
}
