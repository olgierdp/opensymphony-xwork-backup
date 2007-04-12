/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

/**
 * ModelDrivenAnnotationAction
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 *         Created Apr 8, 2003 6:27:29 PM
 */
public class ModelDrivenAnnotationAction extends ActionSupport implements ModelDriven {

    private String foo;
    private TestBean model = new TestBean();


    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    /**
     * @return the model to be pushed onto the ValueStack after the Action itself
     */
    public Object getModel() {
        return model;
    }
}
