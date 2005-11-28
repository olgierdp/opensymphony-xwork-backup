/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.util.Bar;


/**
 * Extend SimpleAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public class SimpleAnnotationAction3 extends SimpleAnnotationAction implements AnnotationDataAware {

    private Bar bar;
    private String data;


    public void setBarObj(Bar b) {
        bar = b;
    }

    public Bar getBarObj() {
        return bar;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
