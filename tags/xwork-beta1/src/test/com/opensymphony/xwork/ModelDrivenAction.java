/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;

import java.util.Date;


/**
 * ModelDrivenAction
 * @author Jason Carreira
 * Created Apr 8, 2003 6:27:29 PM
 */
public class ModelDrivenAction extends ActionSupport implements ModelDriven {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Date date = new Date();

    //~ Methods ////////////////////////////////////////////////////////////////

    public Date getDate() {
        return date;
    }

    /**
     * @return the model to be pushed onto the ValueStack instead of the Action itself
     */
    public Object getModel() {
        return date;
    }
}
