/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.util.List;


/**
 * @author <a href="mailto:plightbo@cisco.com">Pat Lightbody</a>
 * @author $Author$
 * @version $Revision$
 */
public class Cat {

    public static final String SCIENTIFIC_NAME = "Feline";


    Foo foo;
    List kittens;
    String name;


    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setKittens(List kittens) {
        this.kittens = kittens;
    }

    public List getKittens() {
        return kittens;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
