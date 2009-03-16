/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.TestBean;

import java.util.ArrayList;
import java.util.List;


/**
 * VisitorValidatorTestAction
 *
 * @author Jason Carreira
 *         Created Aug 4, 2003 1:00:04 AM
 */
public class VisitorValidatorTestAction extends ActionSupport {

    private List testBeanList = new ArrayList();
    private String context;
    private TestBean bean = new TestBean();
    private TestBean[] testBeanArray;


    public VisitorValidatorTestAction() {
        testBeanArray = new TestBean[5];

        for (int i = 0; i < 5; i++) {
            testBeanArray[i] = new TestBean();
            testBeanList.add(new TestBean());
        }
    }


    public void setBean(TestBean bean) {
        this.bean = bean;
    }

    public TestBean getBean() {
        return bean;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setTestBeanArray(TestBean[] testBeanArray) {
        this.testBeanArray = testBeanArray;
    }

    public TestBean[] getTestBeanArray() {
        return testBeanArray;
    }

    public void setTestBeanList(List testBeanList) {
        this.testBeanList = testBeanList;
    }

    public List getTestBeanList() {
        return testBeanList;
    }
}
