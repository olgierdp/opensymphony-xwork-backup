/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;


/**
 * ModelDrivenAction
 *
 * @author Jason Carreira
 *         Created Apr 8, 2003 6:27:29 PM
 */
public class ModelDrivenAction extends ActionSupport implements ModelDriven {

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
