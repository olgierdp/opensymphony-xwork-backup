/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.ModelDrivenAction;


/**
 * Extend ModelDrivenAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public class ModelDrivenAction2 extends ModelDrivenAction {

    private TestBean2 model = new TestBean2();


    /**
     * @return the model to be pushed onto the ValueStack after the Action itself
     */
    @Override
    public Object getModel() {
        return model;
    }
}
