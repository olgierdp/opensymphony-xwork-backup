/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.AnnotatedTestBean;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.util.Bar;
import com.opensymphony.xwork2.util.Cat;


/**
 * Extend TestBean to test class hierarchy traversal.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
@Conversion()
public class AnnotationTestBean2 extends AnnotatedTestBean implements AnnotationDataAware {

    private Bar bar;
    private String data;
    private Cat cat;


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

    public Cat getCat() {
        return cat;
    }

    @TypeConversion(
            key = "cat", converter = "com.opensymphony.xwork2.conversion.impl.FooBarConverter"
    )
    public void setCat(Cat cat) {
        this.cat = cat;
    }
}
