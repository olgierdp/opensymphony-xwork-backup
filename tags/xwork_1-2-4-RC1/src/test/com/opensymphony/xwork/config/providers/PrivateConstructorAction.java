/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork.config.providers;

import com.opensymphony.xwork.Action;

/**
 * Action with nu public constructor.
 * <p/>
 * Used for unit test of {@link XmlConfigurationProvider}.
 *
 * @author Claus Ibsen
 */
public class PrivateConstructorAction implements Action {

    private int foo;

    private PrivateConstructorAction() {
        // should be private, no constructor
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public void setFoo(int foo) {
        this.foo = foo;
    }

}
