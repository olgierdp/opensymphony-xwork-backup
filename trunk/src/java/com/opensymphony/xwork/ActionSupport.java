/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork;


/**
 * ActionSupport
 * @author Jason Carreira
 * Created Feb 10, 2003 9:47:39 AM
 */
public class ActionSupport extends BaseActionSupport {
    //~ Methods ////////////////////////////////////////////////////////////////

    public String doDefault() throws Exception {
        return INPUT;
    }

    public String doExecute() throws Exception {
        return SUCCESS;
    }

    public String execute() throws Exception {
        doValidation();

        if (hasErrors()) {
            return INPUT;
        }

        return doExecute();
    }

    /**
     * Subclasses may override this method to provide validation
     * of input data. The execute() method calls doValidation()
     * in the beginning of its code (which will delegate to this method),
     * so as to check input data before doing the actual processing.
     *
     * <p>If any application errors arise these should be registered
     * by calling addActionError() or addFieldError().</p>
     */
    protected void doValidation() {
    }
}
