/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * This file is copyright (c) 2001-2004, Board of Trustees of Stanford
 * University.  Created in the laboratory of Professor Russ B. Altman
 * (russ.altman@stanford.edu), Stanford University, Department of
 * Medicine, as part of the NIH PharmGKB knowledge base development
 * effort.  This work is supported by NIH U01GM61374.  Contact
 * help@pharmgkb.org for assistance, questions or suggestions.
 */
package com.opensymphony.xwork.test;

import com.opensymphony.xwork.ModelDrivenAction;


/**
 * Extend ModelDrivenAction to test class hierarchy traversal.
 *
 * @author Mark Woon
 */
public class ModelDrivenAction2 extends ModelDrivenAction {
    //~ Instance fields ////////////////////////////////////////////////////////

    private TestBean2 model = new TestBean2();

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return the model to be pushed onto the ValueStack after the Action itself
     */
    public Object getModel() {
        return model;
    }
}
