/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.ModelDrivenAnnotationAction;


/**
 * Extend ModelDrivenAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 * @author Rainer Hermanns
 */
public class ModelDrivenAnnotationAction2 extends ModelDrivenAnnotationAction {

    private TestBean2 model = new TestBean2();


    /**
     * @return the model to be pushed onto the ValueStack after the Action itself
     */
    public Object getModel() {
        return model;
    }
}
