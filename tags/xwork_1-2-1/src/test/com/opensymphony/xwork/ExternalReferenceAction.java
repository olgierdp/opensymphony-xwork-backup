/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.opensymphony.xwork;


/**
 * @author Mike
 *         <p/>
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ExternalReferenceAction implements Action {

    private Foo foo;


    /**
     * @param foo The foo to set.
     */
    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    /**
     * @return Returns the foo.
     */
    public Foo getFoo() {
        return foo;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
