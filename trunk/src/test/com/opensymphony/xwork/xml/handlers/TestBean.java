/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.xml.handlers;

import java.util.ArrayList;
import java.util.List;


/**
 * TestBean
 * @author Jason Carreira
 * Created May 17, 2003 1:09:38 PM
 */
public class TestBean {
    //~ Instance fields ////////////////////////////////////////////////////////

    private List children = new ArrayList();
    private String name = "name";
    private int count = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public TestBean() {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public List getChildren() {
        return children;
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

    public void addChild(TestBean bean) {
        children.add(bean);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TestBean)) {
            return false;
        }

        final TestBean testBean = (TestBean) o;

        if (count != testBean.count) {
            return false;
        }

        if (!name.equals(testBean.name)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = count;
        result = (29 * result) + name.hashCode();

        return result;
    }
}
