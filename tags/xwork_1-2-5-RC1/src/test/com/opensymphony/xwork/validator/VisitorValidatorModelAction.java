/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.validator;

import com.opensymphony.xwork.ModelDriven;


/**
 * VisitorValidatorModelAction
 *
 * @author Jason Carreira
 *         Date: Mar 18, 2004 11:26:46 AM
 */
public class VisitorValidatorModelAction extends VisitorValidatorTestAction implements ModelDriven {

    /**
     * @return the model to be pushed onto the ValueStack instead of the Action itself
     */
    public Object getModel() {
        return getBean();
    }
}
